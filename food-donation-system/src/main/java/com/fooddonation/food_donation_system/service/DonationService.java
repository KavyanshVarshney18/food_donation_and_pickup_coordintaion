package com.fooddonation.food_donation_system.service;

import com.fooddonation.food_donation_system.dto.CompleteDonationRequest;
import com.fooddonation.food_donation_system.dto.CreateDonationRequest;
import com.fooddonation.food_donation_system.model.Donation;
import com.fooddonation.food_donation_system.model.DonationStatus;
import com.fooddonation.food_donation_system.model.Location;
import com.fooddonation.food_donation_system.model.OTPVerification;
import com.fooddonation.food_donation_system.model.User;
import com.fooddonation.food_donation_system.repository.DonationRepository;
import com.fooddonation.food_donation_system.repository.OTPVerificationRepository;
import com.fooddonation.food_donation_system.repository.UserRepository;
import com.fooddonation.food_donation_system.websocket.SocketEventPublisher;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DonationService {
    private static final double KM_RADIUS = 10.0;
    private static final int MEALS_PER_KG = 4;

    private final DonationRepository donationRepository;
    private final OTPVerificationRepository otpRepository;
    private final UserRepository userRepository;
    private final SocketEventPublisher socketEventPublisher;
    private final Random random = new Random();

    public Donation createDonation(String donorId, CreateDonationRequest request) {
        Donation donation = new Donation();
        donation.setFoodType(request.getFoodType());
        donation.setQuantity(request.getQuantity());
        donation.setDescription(request.getDescription());
        donation.setBestBeforeTime(request.getBestBeforeTime());
        donation.setCreatedAt(Instant.now());
        donation.setPickupLocation(new Location(request.getLat(), request.getLng()));
        donation.setStatus(DonationStatus.AVAILABLE);
        donation.setDonorId(donorId);
        donation.setImageUrl(request.getImageUrl());

        Donation saved = donationRepository.save(donation);
        socketEventPublisher.publish("DONATION_CREATED", saved);
        return saved;
    }

    public List<Donation> getNearbyDonations(double lat, double lng) {
        return donationRepository.findByStatus(DonationStatus.AVAILABLE)
            .stream()
            .filter(donation -> distanceKm(
                lat,
                lng,
                donation.getPickupLocation().getLat(),
                donation.getPickupLocation().getLng()) <= KM_RADIUS)
            .toList();
    }

    public List<Donation> getMyDonations(String userId) {
        return donationRepository.findByDonorIdOrVolunteerId(userId, userId);
    }

    public synchronized Donation claimDonation(String donationId, String volunteerId) {
        Donation donation = getDonationById(donationId);
        if (donation.getStatus() != DonationStatus.AVAILABLE) {
            throw new IllegalStateException("Donation is no longer available");
        }
        donation.setStatus(DonationStatus.CLAIMED);
        donation.setVolunteerId(volunteerId);
        Donation saved = donationRepository.save(donation);
        socketEventPublisher.publish("DONATION_CLAIMED", saved);
        return saved;
    }

    public OTPVerification generateOtp(String donationId) {
        Donation donation = getDonationById(donationId);
        if (donation.getStatus() != DonationStatus.CLAIMED) {
            throw new IllegalStateException("OTP can be generated only after claim");
        }

        OTPVerification otpVerification = otpRepository.findByDonationId(donationId)
            .orElse(new OTPVerification());
        otpVerification.setDonationId(donationId);
        otpVerification.setOtp(String.format("%04d", random.nextInt(10000)));
        otpVerification.setExpiryTime(Instant.now().plus(10, ChronoUnit.MINUTES));
        otpVerification.setVerified(false);
        return otpRepository.save(otpVerification);
    }

    public Donation verifyOtp(String donationId, String otp) {
        OTPVerification otpVerification = otpRepository.findByDonationId(donationId)
            .orElseThrow(() -> new IllegalArgumentException("OTP not generated"));

        if (otpVerification.isVerified()) {
            throw new IllegalStateException("OTP already verified");
        }
        if (Instant.now().isAfter(otpVerification.getExpiryTime())) {
            throw new IllegalStateException("OTP expired");
        }
        if (!otpVerification.getOtp().equals(otp)) {
            throw new IllegalArgumentException("Invalid OTP");
        }

        otpVerification.setVerified(true);
        otpRepository.save(otpVerification);

        Donation donation = getDonationById(donationId);
        donation.setStatus(DonationStatus.PICKED_UP);
        Donation saved = donationRepository.save(donation);
        socketEventPublisher.publish("DONATION_PICKED_UP", saved);
        return saved;
    }

    public Donation completeDonation(String donationId, CompleteDonationRequest request) {
        Donation donation = getDonationById(donationId);
        if (donation.getStatus() != DonationStatus.PICKED_UP) {
            throw new IllegalStateException("Donation must be picked up first");
        }
        donation.setStatus(DonationStatus.DISTRIBUTED);
        donation.setDistributionProofUrl(request.getDistributionProofUrl());
        Donation saved = donationRepository.save(donation);

        User donor = userRepository.findById(donation.getDonorId())
            .orElseThrow(() -> new IllegalArgumentException("Donor not found"));
        int donatedMeals = (int) Math.round(donation.getQuantity() * MEALS_PER_KG);
        donor.setTotalMealsDonated(donor.getTotalMealsDonated() + donatedMeals);
        userRepository.save(donor);

        socketEventPublisher.publish("DONATION_DISTRIBUTED", saved);
        return saved;
    }

    @Scheduled(fixedDelay = 60000)
    public void expireDonations() {
        List<Donation> activeDonations = donationRepository.findByStatusIn(
            List.of(DonationStatus.AVAILABLE, DonationStatus.CLAIMED));
        Instant now = Instant.now();
        activeDonations.stream()
            .filter(donation -> donation.getBestBeforeTime() != null && now.isAfter(donation.getBestBeforeTime()))
            .forEach(donation -> {
                donation.setStatus(DonationStatus.EXPIRED);
                Donation expired = donationRepository.save(donation);
                socketEventPublisher.publish("DONATION_EXPIRED", expired);
            });
    }

    private Donation getDonationById(String donationId) {
        return donationRepository.findById(donationId)
            .orElseThrow(() -> new IllegalArgumentException("Donation not found"));
    }

    private double distanceKm(double lat1, double lon1, double lat2, double lon2) {
        double earthRadius = 6371.0;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
            + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
            * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return earthRadius * c;
    }
}
