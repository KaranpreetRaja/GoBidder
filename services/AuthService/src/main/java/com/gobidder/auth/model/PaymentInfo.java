package com.gobidder.auth.model;

import jakarta.persistence.Embeddable;

@Embeddable
public class PaymentInfo {
    private String cardNumber;
    private String csv;
    private String expirationDate;
    private String billingAddress;

    public String getCardNumber() {
        return cardNumber;
    }

    public PaymentInfo setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
        return this;
    }

    public String getCsv() {
        return csv;
    }

    public PaymentInfo setCsv(String csv) {
        this.csv = csv;
        return this;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public PaymentInfo setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
        return this;
    }

    public String getBillingAddress() {
        return billingAddress;
    }

    public PaymentInfo setBillingAddress(String billingAddress) {
        this.billingAddress = billingAddress;
        return this;
    }
}
