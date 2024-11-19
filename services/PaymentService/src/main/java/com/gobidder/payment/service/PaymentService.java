package com.gobidder.payment.service;

import com.gobidder.payment.dto.CreditCardRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
public class PaymentService {

    @Value("${auth.service.endpoint}")
    private String authServiceEndpoint;

    private final WebClient webClient;

    public PaymentService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    public Map<String, Object> fetchUserDataFromAuthService(String jwtToken) {
        return webClient.get()
                .uri(authServiceEndpoint)
                .header("Authorization", "Bearer " + jwtToken)
                .retrieve()
                .bodyToMono(Map.class)
                .block();
    }

    public boolean validateCreditCard(CreditCardRequest request) {
        if (request.getCardNumber() == null || request.getCardNumber().isEmpty()) {
            return false;
        }
        return luhnCheck(request.getCardNumber());
    }

    private boolean luhnCheck(String cardNumber) {
        int sum = 0;
        boolean alternate = false;

        for (int i = cardNumber.length() - 1; i >= 0; i--) {
            int n = Character.getNumericValue(cardNumber.charAt(i));
            if (alternate) {
                n *= 2;
                if (n > 9) {
                    n -= 9;
                }
            }
            sum += n;
            alternate = !alternate;
        }

        return (sum % 10 == 0);
    }
}