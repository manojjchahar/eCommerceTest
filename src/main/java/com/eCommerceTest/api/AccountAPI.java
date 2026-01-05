package com.ecommercefull.api;

import com.ecommercefull.base.BaseAPI;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class AccountAPI extends BaseAPI {

    public Response getUserByEmail(String email) {
        return given()
                .contentType("application/x-www-form-urlencoded")
                .formParam("email", email)
                .get(APIEndpoints.GET_USER_BY_EMAIL)
                .then().extract().response();
    }

    public Response updateAccount(String email, String name) {
        return given()
                .contentType("application/x-www-form-urlencoded")
                .formParam("email", email)
                .formParam("name", name)
                .put(APIEndpoints.UPDATE_ACCOUNT)
                .then().extract().response();
    }

    public Response deleteAccount(String email, String password) {
        return given()
                .contentType("application/x-www-form-urlencoded")
                .formParam("email", email)
                .formParam("password", password)
                .delete(APIEndpoints.DELETE_ACCOUNT)
                .then().extract().response();
    }
}
