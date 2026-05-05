package com.fooddonation.food_donation_system.dto;

import com.fooddonation.food_donation_system.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private User user;
}
