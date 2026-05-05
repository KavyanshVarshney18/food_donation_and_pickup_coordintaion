package com.fooddonation.food_donation_system.dto;

import java.time.Instant;
import lombok.Data;

@Data
public class CreateDonationRequest {
    private String foodType;
    private double quantity;
    private String description;
    private Instant bestBeforeTime;
    private double lat;
    private double lng;
    private String imageUrl;
}
