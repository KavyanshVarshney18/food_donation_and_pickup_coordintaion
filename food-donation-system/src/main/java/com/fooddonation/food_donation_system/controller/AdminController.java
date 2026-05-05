package com.fooddonation.food_donation_system.controller;

import com.fooddonation.food_donation_system.model.User;
import com.fooddonation.food_donation_system.repository.UserRepository;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    private final UserRepository userRepository;

    @GetMapping("/leaderboard")
    public List<User> leaderboard() {
        return userRepository.findAll().stream()
            .sorted(Comparator.comparingInt(User::getTotalMealsDonated).reversed())
            .limit(10)
            .toList();
    }
}
