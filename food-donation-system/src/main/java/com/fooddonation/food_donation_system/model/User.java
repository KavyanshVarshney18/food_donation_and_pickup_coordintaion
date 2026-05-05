package com.fooddonation.food_donation_system.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "users")
public class User {
    @Id
    private String id;
    private String name;
    private String phone;
    private String password;
    private UserRole role;
    private double latitude;
    private double longitude;
    private int totalMealsDonated;
}
