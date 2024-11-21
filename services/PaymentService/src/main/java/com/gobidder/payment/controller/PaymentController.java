package com.gobidder.payment.controller;

import com.gobidder.payment.dto.CreditCardRequest;
import com.gobidder.payment.dto.PaymentResponse;
import com.gobidder.payment.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/validate")
    public PaymentResponse validatePayment(@RequestBody CreditCardRequest request) {
        boolean isValid = paymentService.validateCreditCard(request);
        String message = isValid ? "Transaction successful" : "Invalid card details";
        return new PaymentResponse(isValid, message);
    }

    @PostMapping("/validate/jwt")
    public PaymentResponse validatePaymentJwt(@RequestBody Map<String, String> tokenPayload) {
        String token = tokenPayload.get("token");

        if (token == null || token.isEmpty()) {
            return new PaymentResponse(false, "Invalid token");
        }

        try {
            Map<String, Object> userData = paymentService.fetchUserDataFromAuthService(token);

            String fullName = (String) userData.get("fullName");
            Map<String, String> paymentInfo = (Map<String, String>) userData.get("paymentInfo");

            CreditCardRequest creditCardRequest = new CreditCardRequest();
            creditCardRequest.setCardNumber(paymentInfo.get("cardNumber"));
            creditCardRequest.setCsv(paymentInfo.get("csv"));
            creditCardRequest.setExpirationDate(paymentInfo.get("expirationDate"));
            creditCardRequest.setName(fullName);
            creditCardRequest.setBillingAddress(paymentInfo.get("billingAddress"));

            boolean isValid = paymentService.validateCreditCard(creditCardRequest);
            String message = isValid ? "Transaction successful" : "Invalid card details";
            return new PaymentResponse(isValid, message);

        } catch (Exception e) {
            return new PaymentResponse(false, "Error occurred: " + e.getMessage());
        }
    }
}
