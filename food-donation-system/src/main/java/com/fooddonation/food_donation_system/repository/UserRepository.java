package com.fooddonation.food_donation_system.repository;

import com.fooddonation.food_donation_system.model.User;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByPhone(String phone);
}
