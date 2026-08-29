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
    private final JWTUtil JWTUtil;
    private final UserService userService;
    @Override
    @Transactional
    public UserDTO signUp(SignUpDTO signUpDTO) {
        User user=userRepository.findByEmail(signUpDTO.getEmail()).orElse(null);
        if(user!=null){
            log.warn("Signup rejected because the user already exists");
            throw new RuntimeConflictException("An account already exists for the provided email address");
        }

        User mappedUser =mapper.map(signUpDTO,User.class);
        mappedUser.setRoles(Set.of(Role.RIDER));
        mappedUser.setPassword(passwordEncoder.encode(mappedUser.getPassword()));
        User savedUser=userRepository.save(mappedUser);

        //create User related Entities
        riderService.createNewRider(savedUser);
        walletService.createNewWallet(savedUser);
        log.info("User signup completed: userId={}", savedUser.getId());
        return mapper.map(savedUser,UserDTO.class);
    }
    @Override
    public DriverDTO onBoardNewDriver(Long userId,String vehicleId) {
        User user=userRepository.findById(userId).orElseThrow(
                ()->new ResourceNotFoundException("User not found: userId="+userId)
        );

        if(user.getRoles().contains(Role.DRIVER)){
            log.warn("Driver onboarding rejected because user is already a driver: userId={}", userId);
            throw new RuntimeConflictException("User is already registered as a driver: userId="+userId);
        }

        Driver driver=driverService.findByVehicleId(vehicleId);
        if(driver!=null)
        {
            if(driver.getUser().getId().equals(userId)){
                log.warn("Driver onboarding rejected because user is already assigned: userId={}", userId);
                throw new RuntimeConflictException("User is already registered as a driver: userId="+userId);

            }
            else{
                log.warn("Driver onboarding rejected because vehicle is assigned to another driver: userId={}", userId);
                throw new RuntimeConflictException("Vehicle is already registered to another driver: vehicleId="+vehicleId);
            }
        }
        Driver createDriver=Driver.builder()
                .status(DriverStatus.AVAILABLE)
                .user(user)
                .rating(0.0)
                .vehicleId(vehicleId)
                .build();
        user.getRoles().add(Role.DRIVER);
        userRepository.save(user);
        DriverDTO driverDTO = driverService.createNewDriver(createDriver);
        log.info("Driver onboarding completed: userId={}, driverId={}", userId, driverDTO.getId());
        return driverDTO;
    }

    @Override
    public String[] login(LoginRequestDTO loginRequestDTO) {
        Authentication authentication=authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequestDTO.getEmail(),loginRequestDTO.getPassword())
        );
        User user= (User) authentication.getPrincipal();
        String accessToken= JWTUtil.generateAccessToken(user);
        String refreshToken= JWTUtil.generateRefreshToken(user);

        log.info("Authentication succeeded: userId={}", user.getId());
        return new String[]{accessToken,refreshToken};
    }

    @Override
    public String refreshToken(String refreshToken) {
        Long userId= JWTUtil.getUserIdFromToken(refreshToken);
        User user=userService.getUserById(userId);
        log.debug("Access token refreshed: userId={}", userId);
        return JWTUtil.generateAccessToken(user);
    }
}
