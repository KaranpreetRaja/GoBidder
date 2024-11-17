package com.gobidder.auth.service;

import com.gobidder.auth.dtos.LoginDto;
import com.gobidder.auth.dtos.RegisterDto;
import com.gobidder.auth.model.User;
import com.gobidder.auth.model.PaymentInfo;
import com.gobidder.auth.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    public AuthService(
            UserRepository userRepository,
            AuthenticationManager authenticationManager,
            PasswordEncoder passwordEncoder
    ) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User signup(RegisterDto input) {
        PaymentInfo paymentInfo = new PaymentInfo()
                .setCardNumber(input.getCardNumber())
                .setCsv(input.getCsv())
                .setExpirationDate(input.getExpirationDate())
                .setBillingAddress(input.getBillingAddress());

        User user = new User()
                .setFullName(input.getFullName())
                .setEmail(input.getEmail())
                .setPassword(passwordEncoder.encode(input.getPassword()))
                .setPaymentInfo(paymentInfo);

        return userRepository.save(user);
    }

    public User authenticate(LoginDto input) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        input.getEmail(),
                        input.getPassword()
                )
        );

        return userRepository.findByEmail(input.getEmail())
                .orElseThrow();
    }
}