package com.fooddonation.food_donation_system.dto;

import com.fooddonation.food_donation_system.model.UserRole;
import lombok.Data;

@Data
public class RegisterRequest {
    private String name;
    private String phone;
    private String password;
    private UserRole role;
    private double latitude;
    private double longitude;
}
