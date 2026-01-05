package com.ecommercefull.api;

import com.ecommercefull.base.BaseAPI;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class AuthAPI extends BaseAPI {

    public Response login(String email, String password) {
        return given()
                .contentType("application/x-www-form-urlencoded")
                .formParam("email", email)
                .formParam("password", password)
                .post(APIEndpoints.LOGIN)
                .then()
                .extract().response();
    }

    public Response register(String name, String email, String password) {
        return given()
                .contentType("application/x-www-form-urlencoded")
                .formParam("name", name)
                .formParam("email", email)
                .formParam("password", password)
                .post(APIEndpoints.REGISTER)
                .then()
                .extract().response();
    }
}
