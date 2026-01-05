package com.ecommercefull.models;

public class AuthResponse {
    private String message;
    private int responseCode;

    public AuthResponse() {}

    public AuthResponse(String message, int responseCode) {
        this.message = message;
        this.responseCode = responseCode;
    }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public int getResponseCode() { return responseCode; }
    public void setResponseCode(int responseCode) { this.responseCode = responseCode; }
}
