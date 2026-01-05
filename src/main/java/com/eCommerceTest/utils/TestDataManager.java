package com.eCommerceTest.utils;

import com.eCommerceTest.models.Product;
import com.eCommerceTest.models.User;
import com.eCommerceTest.models.PaymentDetails;
import com.eCommerceTest.models.UserRegistrationData;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Centralized test data manager to load and access test data from JSON files.
 * Uses DataRepository for consolidated loading; legacy JSONUtils removed.
 */
public final class TestDataManager {
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final Logger log = LoggerFactory.getLogger(TestDataManager.class);

    private static List<User> users;
    private static List<Product> products;
    private static List<PaymentDetails> payments;
    private static volatile UserRegistrationData defaultRegistrationData;

    private TestDataManager() {
    }

    static {
        loadTestData();
    }

    private static void loadTestData() {
        try {
            users = DataRepository.load("/testData/users.json",
                    new com.fasterxml.jackson.core.type.TypeReference<List<com.eCommerceTest.models.User>>() {
                    });
            products = DataRepository.load("/testData/products.json",
                    new com.fasterxml.jackson.core.type.TypeReference<List<com.eCommerceTest.models.Product>>() {
                    });
            payments = DataRepository.load("/testData/payments.json",
                    new com.fasterxml.jackson.core.type.TypeReference<List<com.eCommerceTest.models.PaymentDetails>>() {
                    });
            log.info("Test data loaded successfully - Users: {}, Products: {}, Payments: {}", users.size(),
                    products.size(), payments.size());
        } catch (Exception e) {
            log.error("Failed to load test data: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to initialize test data", e);
        }
    }

    // ============= USER DATA METHODS =============

    /**
     * Get user by name from test data
     */
    public static Optional<User> getUserByName(String name) {
        return users.stream()
                .filter(u -> u.getName().equalsIgnoreCase(name))
                .findFirst();
    }

    /**
     * Get user by email from test data
     */
    public static Optional<User> getUserByEmail(String email) {
        return users.stream()
                .filter(u -> u.getEmail().equalsIgnoreCase(email))
                .findFirst();
    }

    /**
     * Get user by index (0-based)
     */
    public static User getUserByIndex(int index) {
        if (index < 0 || index >= users.size()) {
            throw new IllegalArgumentException("Invalid user index: " + index);
        }
        return users.get(index);
    }

    /**
     * Get all users
     */
    public static List<User> getAllUsers() {
        return users;
    }

    /**
     * Get default test user (first user in list)
     */
    public static User getDefaultUser() {
        return users.isEmpty() ? null : users.get(0);
    }

    // ============= PRODUCT DATA METHODS =============

    /**
     * Get product by name from test data
     */
    public static Optional<Product> getProductByName(String name) {
        return products.stream()
                .filter(p -> p.getName().equalsIgnoreCase(name))
                .findFirst();
    }

    /**
     * Get product by brand from test data
     */
    public static List<Product> getProductsByBrand(String brand) {
        return products.stream()
                .filter(p -> p.getBrand().equalsIgnoreCase(brand))
                .toList();
    }

    /**
     * Get product by ID from test data
     */
    public static Optional<Product> getProductById(int id) {
        return products.stream()
                .filter(p -> p.getId() == id)
                .findFirst();
    }

    /**
     * Get product by index (0-based)
     */
    public static Product getProductByIndex(int index) {
        if (index < 0 || index >= products.size()) {
            throw new IllegalArgumentException("Invalid product index: " + index);
        }
        return products.get(index);
    }

    /**
     * Get all products
     */
    public static List<Product> getAllProducts() {
        return products;
    }

    /**
     * Get default test product (first product in list)
     */
    public static Product getDefaultProduct() {
        return products.isEmpty() ? null : products.get(0);
    }

    // ============= PAYMENT DATA METHODS =============

    /**
     * Get payment details by cardholder name
     */
    public static Optional<PaymentDetails> getPaymentByCardholderName(String name) {
        return payments.stream()
                .filter(p -> p.getCardholderName().equalsIgnoreCase(name))
                .findFirst();
    }

    /**
     * Get payment by index (0-based)
     */
    public static PaymentDetails getPaymentByIndex(int index) {
        if (index < 0 || index >= payments.size()) {
            throw new IllegalArgumentException("Invalid payment index: " + index);
        }
        return payments.get(index);
    }

    /**
     * Get default payment details (first in list)
     */
    public static PaymentDetails getDefaultPayment() {
        return payments.isEmpty() ? null : payments.get(0);
    }

    /**
     * Get all payment details
     */
    public static List<PaymentDetails> getAllPayments() {
        return payments;
    }

    // ============= REGISTRATION DATA METHODS =============

    /**
     * Get default registration data (lazy loaded from JSON).
     * This includes all fields required for the signup form on
     * automationexercise.com:
     * - Basic info: name, email, password, title
     * - Personal details: firstName, lastName, date of birth
     * - Address: company, address1, address2, country, state, city, zipcode
     * - Contact: mobileNumber
     *
     * Using this method eliminates the need to pass 15+ parameters in step
     * definitions.
     * Instead of:
     * userCompletesAccountCreation("John", "Doe", "Mr", "123 Main St", "Toronto",
     * ...)
     *
     * You can simply call:
     * TestDataManager.getDefaultRegistrationData()
     *
     * Benefits:
     * - Clean step definitions with no parameter clutter
     * - Easy to modify test data without touching code
     * - Supports multiple registration scenarios via different JSON files
     * - Type-safe access to all registration fields
     *
     * @return UserRegistrationData object with all signup form fields populated
     */
    public static UserRegistrationData getDefaultRegistrationData() {
        if (defaultRegistrationData == null) {
            synchronized (TestDataManager.class) {
                if (defaultRegistrationData == null) {
                    defaultRegistrationData = loadRegistrationData("/testData/registration-data.json");
                }
            }
        }
        return defaultRegistrationData;
    }

    /**
     * Get registration data by profile name (e.g., "premium-user", "basic-user").
     * Useful for testing different user types or scenarios.
     *
     * Example usage:
     * UserRegistrationData premiumUser =
     * TestDataManager.getRegistrationDataByProfile("premium-user");
     *
     * @param profileName The profile identifier in the JSON array
     * @return UserRegistrationData matching the profile name
     */
    public static Optional<UserRegistrationData> getRegistrationDataByProfile(String profileName) {
        try (InputStream is = TestDataManager.class.getResourceAsStream("/testData/registration-data.json")) {
            if (is == null) {
                log.error("Registration data file not found");
                return Optional.empty();
            }
            JsonNode root = MAPPER.readTree(is);
            if (root.isArray()) {
                for (JsonNode node : root) {
                    if (profileName.equalsIgnoreCase(node.path("profile").asText())) {
                        return Optional.of(buildRegistrationData(node));
                    }
                }
            }
        } catch (Exception e) {
            log.error("Failed to load registration data by profile: {} - {}", profileName, e.getMessage(), e);
        }
        return Optional.empty();
    }

    private static UserRegistrationData loadRegistrationData(String path) {
        try (InputStream is = TestDataManager.class.getResourceAsStream(path)) {
            if (is == null) {
                throw new RuntimeException("Registration data file not found: " + path);
            }
            JsonNode root = MAPPER.readTree(is);

            // If it's an array, take the first element as default
            JsonNode node = root.isArray() ? root.get(0) : root;

            return buildRegistrationData(node);
        } catch (Exception e) {
            log.error("Failed loading registration data from {}: {}", path, e.getMessage(), e);
            throw new RuntimeException("Failed to initialize registration data", e);
        }
    }

    private static UserRegistrationData buildRegistrationData(JsonNode node) {
        return new UserRegistrationData.Builder()
                .name(node.path("name").asText())
                .email(node.path("email").asText())
                .password(node.path("password").asText())
                .title(node.path("title").asText())
                .firstName(node.path("firstName").asText())
                .lastName(node.path("lastName").asText())
                .company(node.path("company").asText(""))
                .address1(node.path("address1").asText())
                .address2(node.path("address2").asText(""))
                .country(node.path("country").asText())
                .state(node.path("state").asText())
                .city(node.path("city").asText())
                .zipcode(node.path("zipcode").asText())
                .mobileNumber(node.path("mobileNumber").asText())
                .dayOfBirth(node.path("dayOfBirth").asInt(1))
                .monthOfBirth(node.path("monthOfBirth").asInt(1))
                .yearOfBirth(node.path("yearOfBirth").asInt(1990))
                .build();
    }

    /**
     * Reload test data (useful for data-driven tests)
     */
    public static void reload() {
        loadTestData();
    }

    /**
     * Select a record from a JSON array under /testData by name or profile key.
     * Special-case users.json to avoid cache type mismatch (List<User> vs
     * List<Map>) causing ClassCastException.
     */
    public static Map<String, Object> selectRecord(String fileName, String nameOrProfile) {
        if (fileName == null || fileName.isBlank())
            return java.util.Collections.emptyMap();
        String fn = fileName.trim();
        // Handle users.json using already loaded typed list to avoid casting issues.
        if ("users.json".equalsIgnoreCase(fn)) {
            if (users == null || users.isEmpty())
                return java.util.Collections.emptyMap();
            String key = nameOrProfile == null || nameOrProfile.isBlank() ? null : nameOrProfile.trim();
            com.eCommerceTest.models.User chosen = users.stream()
                    .filter(u -> key == null || "default".equalsIgnoreCase(key) || u.getName().equalsIgnoreCase(key))
                    .findFirst()
                    .orElse(users.get(0));
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("name", chosen.getName());
            map.put("email", chosen.getEmail());
            map.put("password", chosen.getPassword());
            return map;
        }
        // Fallback generic path for other test data files (not previously cached as
        // typed models).
        List<Map<String, Object>> records = DataRepository.loadJsonArray(fn);
        if (records == null || records.isEmpty())
            return java.util.Collections.emptyMap();
        if (nameOrProfile == null || nameOrProfile.isBlank())
            return records.get(0);
        String key = nameOrProfile.trim();
        for (Map<String, Object> r : records) {
            Object candidate = "default".equalsIgnoreCase(key) ? r.get("profile") : r.get("name");
            if (candidate != null && key.equalsIgnoreCase(String.valueOf(candidate)))
                return r;
        }
        return records.get(0);
    }
}
