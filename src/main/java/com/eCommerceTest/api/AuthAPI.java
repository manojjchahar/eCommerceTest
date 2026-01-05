package com.eCommerceTest.api;

import com.eCommerceTest.base.BaseAPI;
import com.eCommerceTest.models.UserRegistrationData;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.Map;

public class AuthAPI extends BaseAPI {

    public Response login(String email, String password) {
        return spec()
                .contentType("application/x-www-form-urlencoded")
                .formParam("email", email)
                .formParam("password", password)
                .post(APIEndpoints.LOGIN)
                .then()
                .log().all()
                .extract().response();
    }

    public Response register(String name, String email, String password) {
        return spec()
                .contentType("application/x-www-form-urlencoded")
                .formParam("name", name)
                .formParam("email", email)
                .formParam("password", password)
                .post(APIEndpoints.REGISTER)
                .then()
                .log().all()
                .extract().response();
    }

    // New: register with full payload (data-driven)
    public Response register(Map<String, Object> data) {
        RequestSpecification req = buildFormSpec(data);
        return req.post(APIEndpoints.REGISTER).then().log().all().extract().response();
    }

    // Strongly typed registration
    public Response register(UserRegistrationData data) {
        RequestSpecification req = buildFormSpec(data);
        return req.post(APIEndpoints.REGISTER).then().log().all().extract().response();
    }
}
