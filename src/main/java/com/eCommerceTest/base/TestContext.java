package com.ecommercefull.base;

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
}
