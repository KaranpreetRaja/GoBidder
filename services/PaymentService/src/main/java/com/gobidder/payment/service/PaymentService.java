package com.gobidder.payment.service;

import com.gobidder.payment.dto.CreditCardRequest;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {

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