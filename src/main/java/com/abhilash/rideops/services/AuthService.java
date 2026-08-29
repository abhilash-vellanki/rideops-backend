package com.abhilash.rideops.services;

import com.abhilash.rideops.dto.*;

public interface AuthService {
    UserDTO signUp(SignUpDTO signUpDTO);
    DriverDTO onBoardNewDriver(Long userId,String vehicleId);
    String[] login(LoginRequestDTO loginRequestDTO);
    String refreshToken(String refreshToken);
}