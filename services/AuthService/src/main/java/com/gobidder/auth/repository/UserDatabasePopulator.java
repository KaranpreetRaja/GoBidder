package com.gobidder.auth.repository;

import com.gobidder.auth.model.User;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserDatabasePopulator {
    private static final Logger logger = LoggerFactory.getLogger(UserDatabasePopulator.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final List<User> initialUsers;

    @Autowired
    public UserDatabasePopulator(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        User user1 = new User()
            .setFullName("Daniel")
            .setEmail("dannyjdigio@gmail.com")
            .setPassword(passwordEncoder.encode("Password123"))
            .setPaymentInfo(null);
        this.initialUsers = new ArrayList<>();
        this.initialUsers.add(user1);
    }

    @PostConstruct
    public void initDatabase() {
        logger.info("Populating users in database");
        for (User user : this.initialUsers) {
            if (!this.userRepository.existsByEmail(user.getEmail())) {
                logger.info("Creating user {}", user.getEmail());
                this.userRepository.save(user);
                logger.info("Created user {}", user.getEmail());
            }
        }
    }

}
