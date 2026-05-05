package com.fooddonation.food_donation_system.model;

import java.time.Instant;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "donations")
public class Donation {
    @Id
    private String id;
    private String foodType;
    private double quantity;
    private String description;
    private Instant bestBeforeTime;
    private Instant createdAt;
    private Location pickupLocation;
    private DonationStatus status;
    private String donorId;
    private String volunteerId;
    private String imageUrl;
    private String distributionProofUrl;
}
