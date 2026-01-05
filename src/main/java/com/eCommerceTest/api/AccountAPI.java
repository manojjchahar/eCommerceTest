package com.eCommerceTest.api;

import com.eCommerceTest.base.BaseAPI;
import com.eCommerceTest.models.UserRegistrationData;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.Map;

public class AccountAPI extends BaseAPI {

    public Response getUserByEmail(String email) {
        return spec()
                .contentType("application/x-www-form-urlencoded")
                .formParam("email", email)
                .get(APIEndpoints.GET_USER_BY_EMAIL)
                .then().log().all().extract().response();
    }

    public Response updateAccount(Map<String, Object> data) {
        RequestSpecification req = buildFormSpec(data);
        return req.put(APIEndpoints.UPDATE_ACCOUNT)
                .then().log().all().extract().response();
    }

    public Response updateAccount(UserRegistrationData data) {
        RequestSpecification req = buildFormSpec(data);
        return req.put(APIEndpoints.UPDATE_ACCOUNT)
                .then().log().all().extract().response();
    }

    public Response deleteAccount(String email, String password) {
        return spec()
                .contentType("application/x-www-form-urlencoded")
                .formParam("email", email)
                .formParam("password", password)
                .delete(APIEndpoints.DELETE_ACCOUNT)
                .then().log().all().extract().response();
    }
}
