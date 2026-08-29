package com.abhilash.rideops.services;

import com.abhilash.rideops.entities.User;
import com.abhilash.rideops.exceptions.ResourceNotFoundException;
import com.abhilash.rideops.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Invalid email or password"));
    }

    public User getUserById(Long userId) {
        log.debug("Loading user by ID: userId={}", userId);
        return userRepository.findById(userId).orElseThrow(
                ()->new ResourceNotFoundException("User not found: userId="+userId)
        );
    }


}
