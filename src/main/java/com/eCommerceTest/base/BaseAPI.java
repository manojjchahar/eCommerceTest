package com.eCommerceTest.base;

import com.eCommerceTest.models.UserRegistrationData;
import com.eCommerceTest.utils.ConfigReader;

import com.eCommerceTest.utils.RetryPolicy;
import com.eCommerceTest.utils.Constants;
import io.restassured.RestAssured;
import io.restassured.filter.Filter;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

public abstract class BaseAPI implements IApiClient {

    private static final List<Filter> COMMON_FILTERS = new ArrayList<>();
    private static volatile String currentBaseUrl;

    static {
        String baseUrl = System.getProperty(Constants.PROP_API_BASE_URL,
                ConfigReader.getApiConfig("base_url", Constants.DEFAULT_API_BASE_URL));
        RestAssured.baseURI = baseUrl;
        currentBaseUrl = baseUrl;
        COMMON_FILTERS.add(new RequestLoggingFilter(LogDetail.METHOD));
        COMMON_FILTERS.add(new ResponseLoggingFilter(LogDetail.STATUS));
        try {
            Class<?> clazz = Class.forName("io.qameta.allure.restassured.AllureRestAssured");
            Object inst = clazz.getDeclaredConstructor().newInstance();
            COMMON_FILTERS.add((Filter) inst);
        } catch (Exception ignored) {
        }
        COMMON_FILTERS.add(new com.eCommerceTest.utils.RetryFilter(
                RetryPolicy.builder()
                        .fixed()
                        .attempts(3)
                        .fixedDelay(300)
                        .codes(Set.of(429, 500, 502, 503, 504))
                        .build()));
    }

    @Override
    public RequestSpecification spec() {
        String desired = System.getProperty(Constants.PROP_API_BASE_URL, currentBaseUrl);
        if (!desired.equals(currentBaseUrl)) {
            RestAssured.baseURI = desired;
            currentBaseUrl = desired;
        }
        List<Filter> filters = new ArrayList<>(COMMON_FILTERS);
        if (currentBaseUrl.contains("httpbin.org")) {
            filters.removeIf(f -> f instanceof ResponseLoggingFilter);
        }
        RequestSpecification rs = RestAssured.given().filters(filters);
        String token = System.getProperty(Constants.PROP_API_TOKEN, ConfigReader.getApiConfig("token", ""));
        if (token != null && !token.isBlank()) {
            rs.header(Constants.HDR_AUTHORIZATION, "Bearer " + token.trim());
        }
        return rs;
    }

    public RequestSpecification buildFormSpec(Map<String, Object> data) {
        RequestSpecification req = spec().contentType(Constants.CT_FORM_URLENCODED);
        BiConsumer<String, Object> addIfPresent = (key, val) -> {
            if (val != null)
                req.formParam(key, String.valueOf(val));
        };
        addCommonFormParams(data, addIfPresent);
        return req;
    }

    public RequestSpecification buildFormSpec(UserRegistrationData data) {
        RequestSpecification req = spec().contentType(Constants.CT_FORM_URLENCODED);
        BiConsumer<String, Object> add = (k, v) -> {
            if (v != null)
                req.formParam(k, String.valueOf(v));
        };
        add.accept("name", data.getName());
        add.accept("email", data.getEmail());
        add.accept("password", data.getPassword());
        add.accept("title", data.getTitle());
        add.accept("firstname", data.getFirstName());
        add.accept("lastname", data.getLastName());
        add.accept("company", data.getCompany());
        add.accept("address1", data.getAddress1());
        add.accept("address2", data.getAddress2());
        add.accept("country", data.getCountry());
        add.accept("state", data.getState());
        add.accept("city", data.getCity());
        add.accept("zipcode", data.getZipcode());
        add.accept("mobile_number", data.getMobileNumber());
        add.accept("birth_date", data.getDayOfBirth());
        add.accept("birth_month", data.getMonthOfBirth());
        add.accept("birth_year", data.getYearOfBirth());
        return req;
    }

    private void addCommonFormParams(Map<String, Object> data, BiConsumer<String, Object> add) {
        add.accept("name", data.getOrDefault("name", data.get("fullName")));
        add.accept("email", data.get("email"));
        add.accept("password", data.get("password"));
        add.accept("title", data.get("title"));
        if (data.containsKey("dayOfBirth"))
            add.accept("birth_date", data.get("dayOfBirth"));
        if (data.containsKey("monthOfBirth"))
            add.accept("birth_month", data.get("monthOfBirth"));
        if (data.containsKey("yearOfBirth"))
            add.accept("birth_year", data.get("yearOfBirth"));
        if (data.containsKey("birth_date"))
            add.accept("birth_date", data.get("birth_date"));
        if (data.containsKey("birth_month"))
            add.accept("birth_month", data.get("birth_month"));
        if (data.containsKey("birth_year"))
            add.accept("birth_year", data.get("birth_year"));
        if (data.containsKey("firstName"))
            add.accept("firstname", data.get("firstName"));
        if (data.containsKey("lastName"))
            add.accept("lastname", data.get("lastName"));
        if (data.containsKey("firstname"))
            add.accept("firstname", data.get("firstname"));
        if (data.containsKey("lastname"))
            add.accept("lastname", data.get("lastname"));
        add.accept("company", data.get("company"));
        add.accept("address1", data.get("address1"));
        add.accept("address2", data.get("address2"));
        add.accept("country", data.get("country"));
        add.accept("zipcode", data.get("zipcode"));
        add.accept("state", data.get("state"));
        add.accept("city", data.get("city"));
        if (data.containsKey("mobileNumber"))
            add.accept("mobile_number", data.get("mobileNumber"));
        if (data.containsKey("mobile_number"))
            add.accept("mobile_number", data.get("mobile_number"));
    }
}
