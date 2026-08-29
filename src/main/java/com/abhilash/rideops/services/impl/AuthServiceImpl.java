package com.abhilash.rideops.services.impl;

import com.abhilash.rideops.dto.*;
import com.abhilash.rideops.entities.*;
import com.abhilash.rideops.entities.enums.DriverStatus;
import com.abhilash.rideops.entities.enums.Role;
import com.abhilash.rideops.exceptions.ResourceNotFoundException;
import com.abhilash.rideops.exceptions.RuntimeConflictException;
import com.abhilash.rideops.repositories.*;
import com.abhilash.rideops.services.*;
import com.abhilash.rideops.utils.JWTUtil;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.HashSet;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.HexFormat;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {
    private final ModelMapper mapper;
    private final UserRepository userRepository;

    private final RiderService riderService;
    private final WalletService walletService;
    private final DriverService driverService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;
    private final UserService userService;
    private final RefreshTokenSessionRepository refreshTokenSessionRepository;
    @Override
    @Transactional
    public UserDTO signUp(SignUpDTO signUpDTO) {
        String normalizedEmail = signUpDTO.getEmail().trim().toLowerCase();
        User user = userRepository.findByEmail(normalizedEmail).orElse(null);
        if(user!=null){
            log.warn("Signup rejected because the user already exists");
            throw new RuntimeConflictException("An account already exists for the provided email address");
        }

        User mappedUser =mapper.map(signUpDTO,User.class);
        mappedUser.setEmail(normalizedEmail);
        mappedUser.setRoles(new HashSet<>(Set.of(Role.RIDER)));
        mappedUser.setPassword(passwordEncoder.encode(mappedUser.getPassword()));
        User savedUser=userRepository.save(mappedUser);

        //create User related Entities
        riderService.createNewRider(savedUser);
        walletService.createNewWallet(savedUser);
        log.info("User signup completed: userId={}", savedUser.getId());
        return mapper.map(savedUser,UserDTO.class);
    }
    @Override
    @Transactional
    public DriverDTO onBoardNewDriver(Long userId,String vehicleId) {
        String normalizedVehicleId = vehicleId.trim().toUpperCase(Locale.ROOT);
        User user=userRepository.findByIdWithRoles(userId).orElseThrow(
                ()->new ResourceNotFoundException("User not found: userId="+userId)
        );

        if(user.getRoles() != null && user.getRoles().contains(Role.DRIVER)){
            log.warn("Driver onboarding rejected because user is already a driver: userId={}", userId);
            throw new RuntimeConflictException("User is already registered as a driver: userId="+userId);
        }

        driverService.findByVehicleId(normalizedVehicleId).ifPresent(driver -> {
            if(driver.getUser().getId().equals(userId)){
                log.warn("Driver onboarding rejected because user is already assigned: userId={}", userId);
                throw new RuntimeConflictException("User is already registered as a driver: userId="+userId);

            }
            else{
                log.warn("Driver onboarding rejected because vehicle is assigned to another driver: userId={}", userId);
                throw new RuntimeConflictException(
                        "Vehicle is already registered to another driver: vehicleId=" + normalizedVehicleId);
            }
        });
        Driver createDriver=Driver.builder()
                .status(DriverStatus.OFFLINE)
                .user(user)
                .rating(0.0)
                .vehicleId(normalizedVehicleId)
                .build();
        Set<Role> roles = user.getRoles() == null ? new HashSet<>() : new HashSet<>(user.getRoles());
        roles.add(Role.DRIVER);
        user.setRoles(roles);
        userRepository.save(user);
        DriverDTO driverDTO = driverService.createNewDriver(createDriver);
        log.info("Driver onboarding completed: userId={}, driverId={}", userId, driverDTO.getId());
        return driverDTO;
    }

    @Override
    @Transactional
    public TokenPair login(LoginRequestDTO loginRequestDTO) {
        Authentication authentication=authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequestDTO.getEmail().trim().toLowerCase(), loginRequestDTO.getPassword())
        );
        User user= (User) authentication.getPrincipal();
        log.info("Authentication succeeded: userId={}", user.getId());
        return issueTokenPair(user);
    }

    @Override
    @Transactional(noRollbackFor = JwtException.class)
    public TokenPair refreshToken(String refreshToken) {
        JWTUtil.TokenClaims claims = jwtUtil.parseRefreshToken(refreshToken);
        String currentTokenHash = hashToken(refreshToken);
        RefreshTokenSession currentSession = refreshTokenSessionRepository
                .findByTokenHash(currentTokenHash)
                .orElseThrow(() -> new JwtException("Refresh token is not recognized"));

        if (!currentSession.getUser().getId().equals(claims.userId())) {
            throw new JwtException("Refresh token subject does not match its session");
        }
        if (currentSession.isRevoked()) {
            refreshTokenSessionRepository.revokeAllActiveForUser(claims.userId(), Instant.now());
            log.warn("Refresh token reuse detected; all sessions revoked: userId={}", claims.userId());
            throw new JwtException("Refresh token reuse detected");
        }
        if (currentSession.getExpiresAt().isBefore(Instant.now())) {
            currentSession.setRevokedAt(Instant.now());
            throw new JwtException("Refresh token session has expired");
        }

        User user = userService.getUserById(claims.userId());
        JWTUtil.GeneratedRefreshToken replacement = jwtUtil.generateRefreshToken(user);
        String replacementHash = hashToken(replacement.token());
        Instant now = Instant.now();
        currentSession.setRevokedAt(now);
        currentSession.setReplacedByTokenHash(replacementHash);
        refreshTokenSessionRepository.save(currentSession);
        saveRefreshSession(user, replacement, replacementHash);

        log.info("Refresh token rotated: userId={}", user.getId());
        return new TokenPair(jwtUtil.generateAccessToken(user), replacement.token());
    }

    @Override
    @Transactional
    public void logout(String refreshToken) {
        refreshTokenSessionRepository.findByTokenHash(hashToken(refreshToken))
                .filter(session -> !session.isRevoked())
                .ifPresent(session -> session.setRevokedAt(Instant.now()));
    }

    private TokenPair issueTokenPair(User user) {
        String accessToken = jwtUtil.generateAccessToken(user);
        JWTUtil.GeneratedRefreshToken refreshToken = jwtUtil.generateRefreshToken(user);
        saveRefreshSession(user, refreshToken, hashToken(refreshToken.token()));
        return new TokenPair(accessToken, refreshToken.token());
    }

    private void saveRefreshSession(User user, JWTUtil.GeneratedRefreshToken token, String tokenHash) {
        refreshTokenSessionRepository.save(RefreshTokenSession.builder()
                .user(user)
                .tokenHash(tokenHash)
                .expiresAt(token.expiresAt())
                .build());
    }

    private String hashToken(String token) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(token.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (java.security.NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is not available", exception);
        }
    }
}
