package com.abhilash.rideops.controllers;

import com.abhilash.rideops.dto.*;
import com.abhilash.rideops.services.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    @PostMapping("/signup")
    public ResponseEntity<UserDTO> signUp(@RequestBody SignUpDTO signUpDTO){
        return new ResponseEntity<>(authService.signUp(signUpDTO), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO loginRequestDTO, HttpServletResponse response){
        String[] tokens =authService.login(loginRequestDTO);
        Cookie cookie=new Cookie("refreshToken",tokens[1]);
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
        return ResponseEntity.ok(new LoginResponseDTO(tokens[0]));
    }

    @PostMapping("/onBoardNewDriver/{userId}/{vehicleId}")
    @Secured("ROLE_ADMIN")
    public ResponseEntity<DriverDTO> onBoardNewDriver(@PathVariable Long userId, @PathVariable String vehicleId){
        return new ResponseEntity<>(authService.onBoardNewDriver(userId,vehicleId),HttpStatus.CREATED);
    }

    @PostMapping("/refresh")
    public String refresh(HttpServletRequest request){
        String refreshToken= Arrays.stream(Optional.ofNullable(request.getCookies()).orElseGet(() -> new Cookie[0]))
                .filter(cookie -> "refreshToken".equals(cookie.getName()))
                .findFirst()
                .map(Cookie::getValue)
                .orElseThrow(()->new AuthenticationServiceException("Refresh token cookie is missing"));

        return authService.refreshToken(refreshToken);
    }
}
