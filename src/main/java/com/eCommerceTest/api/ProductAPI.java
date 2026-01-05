package com.ecommercefull.api;

import com.ecommercefull.base.BaseAPI;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class ProductAPI extends BaseAPI {

    public Response productsList() {
        return given()
                .get(APIEndpoints.PRODUCTS_LIST)
                .then().extract().response();
    }

    public Response searchProduct(String search) {
        return given()
                .contentType("application/x-www-form-urlencoded")
                .formParam("search_product", search)
                .post(APIEndpoints.SEARCH_PRODUCT)
                .then().extract().response();
    }

    public Response brandsList() {
        return given()
                .get(APIEndpoints.BRANDS_LIST)
                .then().extract().response();
    }
}
