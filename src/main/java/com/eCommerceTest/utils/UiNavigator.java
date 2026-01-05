package com.eCommerceTest.utils;

import org.openqa.selenium.WebDriver;

/**
 * Centralized UI navigation helper to eliminate repetitive base + path lookups
 * in step definition classes. All methods preserve existing default values.
 */
public final class UiNavigator {

    private static final String DEFAULT_BASE = "https://www.automationexercise.com";

    private UiNavigator() {}

    private static String base() {
        String raw = ConfigReader.getUiConfig("base_url", DEFAULT_BASE);
        return raw.endsWith("/") ? raw.substring(0, raw.length()-1) : raw; // normalize
    }

    private static String path(String key, String defPath) {
        String p = ConfigReader.getUiConfig(key, defPath);
        if (p == null || p.isBlank()) return ""; // allow base only
        return p.startsWith("/") ? p : "/" + p;
    }

    private static String build(String key, String defPath) {
        return base() + path(key, defPath);
    }

    // Generic open by key
    public static void open(WebDriver driver, String key, String defPath) {
        driver.get(build(key, defPath));
    }

    // Specific convenience methods
    public static void openBase(WebDriver driver) { driver.get(base()); }
    public static void openSignup(WebDriver driver) { open(driver, "signup_page", "/signup"); }
    public static void openLogin(WebDriver driver) { open(driver, "login_page", "/login"); }
    public static void openProducts(WebDriver driver) { open(driver, "products_page", "/products"); }
    public static void openCart(WebDriver driver) { open(driver, "cart_page", "/view_cart"); }

    // URL getters (if needed elsewhere)
    public static String signupUrl() { return build("signup_page", "/signup"); }
    public static String loginUrl() { return build("login_page", "/login"); }
    public static String productsUrl() { return build("products_page", "/products"); }
    public static String cartUrl() { return build("cart_page", "/view_cart"); }
}

