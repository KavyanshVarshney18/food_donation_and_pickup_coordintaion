package com.fooddonation.food_donation_system.repository;

import com.fooddonation.food_donation_system.model.OTPVerification;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface OTPVerificationRepository extends MongoRepository<OTPVerification, String> {
    Optional<OTPVerification> findByDonationId(String donationId);
}
