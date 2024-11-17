package com.gobidder.payment.controller;

import com.gobidder.payment.dto.CreditCardRequest;
import com.gobidder.payment.dto.PaymentResponse;
import com.gobidder.payment.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
}
