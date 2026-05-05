package com.fooddonation.food_donation_system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FoodDonationSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(FoodDonationSystemApplication.class, args);
	}

}
