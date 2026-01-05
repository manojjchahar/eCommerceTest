package com.eCommerceTest.models;

import java.util.Map;

/**
 * POJO representing complete user registration/signup data.
 *
 * The signup form on automationexercise.com requires 15+ fields.
 * This class encapsulates all of them in a type-safe, reusable way.
 *
 * Why use this instead of passing individual parameters?
 *
 * BEFORE (Anti-pattern - too many parameters):
 *   public void fillRegistrationForm(String name, String email, String password,
 *       String title, String firstName, String lastName, String company,
 *       String address1, String address2, String country, String state,
 *       String city, String zipcode, String mobile, int day, int month, int year) {
 *       // Method signature is unreadable and error-prone
 *   }
 *
 * AFTER (Clean approach with POJO):
 *   public void fillRegistrationForm(UserRegistrationData data) {
 *       // Single parameter, type-safe, easy to test
 *   }
 *
 * Usage example:
 *   UserRegistrationData data = TestDataManager.getDefaultRegistrationData();
 *   registrationPage.fillForm(data);
 *
 * Benefits:
 * - Reduces method parameters from 17 to 1
 * - Provides compile-time type safety
 * - Makes code self-documenting
 * - Enables easy test data variations via JSON
 * - Supports builder pattern for flexible object creation
 */
public class UserRegistrationData {
    private final String name;
    private final String email;
    private final String password;
    private final String title; // Mr/Mrs/Miss
    private final String firstName;
    private final String lastName;
    private final String company;
    private final String address1;
    private final String address2;
    private final String country;
    private final String state;
    private final String city;
    private final String zipcode;
    private final String mobileNumber;
    private final int dayOfBirth;
    private final int monthOfBirth;
    private final int yearOfBirth;

    private UserRegistrationData(Builder builder) {
        this.name = builder.name;
        this.email = builder.email;
        this.password = builder.password;
        this.title = builder.title;
        this.firstName = builder.firstName;
        this.lastName = builder.lastName;
        this.company = builder.company;
        this.address1 = builder.address1;
        this.address2 = builder.address2;
        this.country = builder.country;
        this.state = builder.state;
        this.city = builder.city;
        this.zipcode = builder.zipcode;
        this.mobileNumber = builder.mobileNumber;
        this.dayOfBirth = builder.dayOfBirth;
        this.monthOfBirth = builder.monthOfBirth;
        this.yearOfBirth = builder.yearOfBirth;
    }

    /**
     * Builder pattern for flexible and readable object construction.
     *
     * Example usage:
     *   UserRegistrationData user = new UserRegistrationData.Builder()
     *       .name("John Doe")
     *       .email("john@example.com")
     *       .password("SecurePass123")
     *       .title("Mr")
     *       .firstName("John")
     *       .lastName("Doe")
     *       .country("Canada")
     *       .city("Toronto")
     *       .build();
     */
    public static class Builder {
        private String name;
        private String email;
        private String password;
        private String title;
        private String firstName;
        private String lastName;
        private String company = "";
        private String address1;
        private String address2 = "";
        private String country;
        private String state;
        private String city;
        private String zipcode;
        private String mobileNumber;
        private int dayOfBirth = 1;
        private int monthOfBirth = 1;
        private int yearOfBirth = 1990;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder firstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public Builder lastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public Builder company(String company) {
            this.company = company;
            return this;
        }

        public Builder address1(String address1) {
            this.address1 = address1;
            return this;
        }

        public Builder address2(String address2) {
            this.address2 = address2;
            return this;
        }

        public Builder country(String country) {
            this.country = country;
            return this;
        }

        public Builder state(String state) {
            this.state = state;
            return this;
        }

        public Builder city(String city) {
            this.city = city;
            return this;
        }

        public Builder zipcode(String zipcode) {
            this.zipcode = zipcode;
            return this;
        }

        public Builder mobileNumber(String mobileNumber) {
            this.mobileNumber = mobileNumber;
            return this;
        }

        public Builder dayOfBirth(int dayOfBirth) {
            this.dayOfBirth = dayOfBirth;
            return this;
        }

        public Builder monthOfBirth(int monthOfBirth) {
            this.monthOfBirth = monthOfBirth;
            return this;
        }

        public Builder yearOfBirth(int yearOfBirth) {
            this.yearOfBirth = yearOfBirth;
            return this;
        }

        public UserRegistrationData build() {
            return new UserRegistrationData(this);
        }
    }

    // Getters
    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getTitle() {
        return title;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getCompany() {
        return company;
    }

    public String getAddress1() {
        return address1;
    }

    public String getAddress2() {
        return address2;
    }

    public String getCountry() {
        return country;
    }

    public String getState() {
        return state;
    }

    public String getCity() {
        return city;
    }

    public String getZipcode() {
        return zipcode;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public int getDayOfBirth() {
        return dayOfBirth;
    }

    public int getMonthOfBirth() {
        return monthOfBirth;
    }

    public int getYearOfBirth() {
        return yearOfBirth;
    }

    /**
     * Convert registration data to a basic User object.
     * Useful for storing in ScenarioContext after successful registration.
     */
    public User toUser() {
        return new User(name, email, password);
    }

    @Override
    public String toString() {
        return "UserRegistrationData{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", title='" + title + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", city='" + city + '\'' +
                ", country='" + country + '\'' +
                '}';
    }

    public static UserRegistrationData fromMap(Map<String, Object> m) {
        if (m == null) return null;
        Builder b = new Builder()
                .name(str(m.get("name")))
                .email(str(m.get("email")))
                .password(str(m.get("password")))
                .title(str(m.get("title")))
                .firstName(firstNameFrom(m))
                .lastName(lastNameFrom(m))
                .company(str(m.get("company")))
                .address1(str(m.get("address1")))
                .address2(str(m.get("address2")))
                .country(str(m.get("country")))
                .state(str(m.get("state")))
                .city(str(m.get("city")))
                .zipcode(str(m.get("zipcode")))
                .mobileNumber(mobileFrom(m))
                .dayOfBirth(intOrDefault(m.get("dayOfBirth"), intOrDefault(m.get("birth_date"), 1)))
                .monthOfBirth(intOrDefault(m.get("monthOfBirth"), intOrDefault(m.get("birth_month"), 1)))
                .yearOfBirth(intOrDefault(m.get("yearOfBirth"), intOrDefault(m.get("birth_year"), 1990)));
        return b.build();
    }

    public Map<String, Object> toFormParams() {
        return Map.ofEntries(
                Map.entry("name", name),
                Map.entry("email", email),
                Map.entry("password", password),
                Map.entry("title", title),
                Map.entry("firstname", firstName),
                Map.entry("lastname", lastName),
                Map.entry("company", company),
                Map.entry("address1", address1),
                Map.entry("address2", address2),
                Map.entry("country", country),
                Map.entry("state", state),
                Map.entry("city", city),
                Map.entry("zipcode", zipcode),
                Map.entry("mobile_number", mobileNumber),
                Map.entry("birth_date", dayOfBirth),
                Map.entry("birth_month", monthOfBirth),
                Map.entry("birth_year", yearOfBirth)
        );
    }


    private static String str(Object o) { return o == null ? null : String.valueOf(o); }
    private static int intOrDefault(Object o, int def) { try { return o == null ? def : Integer.parseInt(String.valueOf(o)); } catch (Exception e){ return def; } }
    private static String firstNameFrom(Map<String,Object> m){ return str(m.getOrDefault("firstName", m.get("firstname"))); }
    private static String lastNameFrom(Map<String,Object> m){ return str(m.getOrDefault("lastName", m.get("lastname"))); }
    private static String mobileFrom(Map<String,Object> m){ return str(m.getOrDefault("mobileNumber", m.get("mobile_number"))); }
}
