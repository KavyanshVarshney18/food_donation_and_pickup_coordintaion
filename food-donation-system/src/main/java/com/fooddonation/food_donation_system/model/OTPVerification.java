package com.fooddonation.food_donation_system.model;

import java.time.Instant;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "otp_verifications")
public class OTPVerification {
    @Id
    private String id;
    private String donationId;
    private String otp;
    private Instant expiryTime;
    private boolean verified;
}
