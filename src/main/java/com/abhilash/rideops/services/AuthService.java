package com.abhilash.rideops.services;

import com.abhilash.rideops.dto.*;

public interface AuthService {
    UserDTO signUp(SignUpDTO signUpDTO);
    DriverDTO onBoardNewDriver(Long userId,String vehicleId);
    TokenPair login(LoginRequestDTO loginRequestDTO);
    TokenPair refreshToken(String refreshToken);
    void logout(String refreshToken);
}
