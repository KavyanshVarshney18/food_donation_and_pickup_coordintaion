package com.fooddonation.food_donation_system.repository;

import com.fooddonation.food_donation_system.model.Donation;
import com.fooddonation.food_donation_system.model.DonationStatus;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DonationRepository extends MongoRepository<Donation, String> {
    List<Donation> findByStatus(DonationStatus status);

    List<Donation> findByDonorIdOrVolunteerId(String donorId, String volunteerId);

    List<Donation> findByStatusIn(List<DonationStatus> statuses);
}
