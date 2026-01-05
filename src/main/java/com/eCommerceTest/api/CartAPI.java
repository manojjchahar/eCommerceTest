package com.eCommerceTest.api;

import com.eCommerceTest.base.BaseAPI;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class CartAPI extends BaseAPI {

    // Note: AutomationExercise does not expose cart API; placeholder for structure completeness.
    public Response getCart() {
        return given().get("/api/cart").then().extract().response();
    }
}
