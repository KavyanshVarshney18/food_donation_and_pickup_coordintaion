package com.fooddonation.food_donation_system.controller;

import com.fooddonation.food_donation_system.dto.CompleteDonationRequest;
import com.fooddonation.food_donation_system.dto.CreateDonationRequest;
import com.fooddonation.food_donation_system.dto.VerifyOtpRequest;
import com.fooddonation.food_donation_system.model.Donation;
import com.fooddonation.food_donation_system.model.OTPVerification;
import com.fooddonation.food_donation_system.service.DonationService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/donations")
@RequiredArgsConstructor
public class DonationController {
    private final DonationService donationService;

    @PostMapping("/create")
    public Donation createDonation(@RequestHeader("X-User-Id") String donorId, @RequestBody CreateDonationRequest request) {
        return donationService.createDonation(donorId, request);
    }

    @GetMapping("/nearby")
    public List<Donation> getNearbyDonations(@RequestParam double lat, @RequestParam double lng) {
        return donationService.getNearbyDonations(lat, lng);
    }

    @GetMapping("/my")
    public List<Donation> getMyDonations(@RequestHeader("X-User-Id") String userId) {
        return donationService.getMyDonations(userId);
    }

    @PostMapping("/{id}/claim")
    public Donation claimDonation(@PathVariable String id, @RequestHeader("X-User-Id") String volunteerId) {
        return donationService.claimDonation(id, volunteerId);
    }

    @PostMapping("/{id}/generate-otp")
    public OTPVerification generateOtp(@PathVariable String id) {
        return donationService.generateOtp(id);
    }

    @PostMapping("/{id}/verify-otp")
    public Donation verifyOtp(@PathVariable String id, @RequestBody VerifyOtpRequest request) {
        return donationService.verifyOtp(id, request.getOtp());
    }

    @PostMapping("/{id}/complete")
    public Donation completeDonation(@PathVariable String id, @RequestBody CompleteDonationRequest request) {
        return donationService.completeDonation(id, request);
    }
}
