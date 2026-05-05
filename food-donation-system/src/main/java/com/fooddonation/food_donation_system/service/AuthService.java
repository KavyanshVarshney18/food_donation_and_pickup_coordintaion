package com.fooddonation.food_donation_system.service;

import com.fooddonation.food_donation_system.dto.AuthResponse;
import com.fooddonation.food_donation_system.dto.LoginRequest;
import com.fooddonation.food_donation_system.dto.RegisterRequest;
import com.fooddonation.food_donation_system.model.User;
import com.fooddonation.food_donation_system.repository.UserRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthResponse register(RegisterRequest request) {
        userRepository.findByPhone(request.getPhone()).ifPresent(existing -> {
            throw new IllegalArgumentException("Phone already registered");
        });

        User user = new User();
        user.setName(request.getName());
        user.setPhone(request.getPhone());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        user.setLatitude(request.getLatitude());
        user.setLongitude(request.getLongitude());
        user.setTotalMealsDonated(0);

        User savedUser = userRepository.save(user);
        return new AuthResponse(generateToken(savedUser.getId()), savedUser);
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByPhone(request.getPhone())
            .orElseThrow(() -> new IllegalArgumentException("Invalid phone or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid phone or password");
        }
        return new AuthResponse(generateToken(user.getId()), user);
    }

    private String generateToken(String userId) {
        return "demo-token-" + userId + "-" + UUID.randomUUID();
    }
}
