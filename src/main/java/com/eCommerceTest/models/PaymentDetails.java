package com.eCommerceTest.models;

/**
 * POJO for payment details - can be used with test data
 */
public class PaymentDetails {
    private String cardholderName;
    private String cardNumber;
    private String cvc;
    private String expiryMonth;
    private String expiryYear;

    public PaymentDetails() {}

    public PaymentDetails(String cardholderName, String cardNumber, String cvc, String expiryMonth, String expiryYear) {
        this.cardholderName = cardholderName;
        this.cardNumber = cardNumber;
        this.cvc = cvc;
        this.expiryMonth = expiryMonth;
        this.expiryYear = expiryYear;
    }

    public String getCardholderName() { return cardholderName; }
    public void setCardholderName(String cardholderName) { this.cardholderName = cardholderName; }

    public String getCardNumber() { return cardNumber; }
    public void setCardNumber(String cardNumber) { this.cardNumber = cardNumber; }

    public String getCvc() { return cvc; }
    public void setCvc(String cvc) { this.cvc = cvc; }

    public String getExpiryMonth() { return expiryMonth; }
    public void setExpiryMonth(String expiryMonth) { this.expiryMonth = expiryMonth; }

    public String getExpiryYear() { return expiryYear; }
    public void setExpiryYear(String expiryYear) { this.expiryYear = expiryYear; }
}


