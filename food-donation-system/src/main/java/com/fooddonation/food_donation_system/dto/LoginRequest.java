package com.fooddonation.food_donation_system.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String phone;
    private String password;
}
