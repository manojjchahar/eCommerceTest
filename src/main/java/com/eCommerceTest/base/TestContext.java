package com.eCommerceTest.base;

import org.openqa.selenium.WebDriver;

import java.util.HashMap;
import java.util.Map;

public class TestContext {

    private final Map<String, Object> context = new HashMap<>();
    private WebDriver driver;

    public WebDriver getDriver() {
        return driver;
    }

    public void setDriver(WebDriver driver) {
        this.driver = driver;
    }

    public void set(String key, Object value) {
        context.put(key, value);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String key) {
        return (T) context.get(key);
    }

    // Typed helpers for safer retrieval
    public String getAsString(String key) {
        Object v = context.get(key);
        return v == null ? null : String.valueOf(v);
    }

    public Integer getAsInt(String key) {
        Object v = context.get(key);
        if (v instanceof Integer i) return i;
        try {
            return v == null ? null : Integer.parseInt(String.valueOf(v));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public <T> T getOrDefault(String key, T defaultValue) {
        Object v = context.get(key);
        if (v == null) return defaultValue;
        try {
            @SuppressWarnings("unchecked") T casted = (T) v;
            return casted;
        } catch (ClassCastException e) {
            return defaultValue;
        }
    }
}
