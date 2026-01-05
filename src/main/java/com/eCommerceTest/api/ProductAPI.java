package com.eCommerceTest.api;

import com.eCommerceTest.base.BaseAPI;
import io.restassured.response.Response;

public class ProductAPI extends BaseAPI {

    public Response productsList() {
        return spec().get(APIEndpoints.PRODUCTS_LIST)
                .then().log().all().extract().response();
    }

    public Response searchProduct(String search) {
        return spec()
                .contentType("application/x-www-form-urlencoded")
                .formParam("search_product", search)
                .post(APIEndpoints.SEARCH_PRODUCT)
                .then().log().all().extract().response();
    }

    public Response brandsList() {
        return spec().get(APIEndpoints.BRANDS_LIST)
                .then().log().all().extract().response();
    }
}
