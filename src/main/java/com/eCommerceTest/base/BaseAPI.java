package com.ecommercefull.base;

import com.ecommercefull.utils.ConfigReader;
import io.restassured.RestAssured;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;

public abstract class BaseAPI {

    static {
        String baseUrl = ConfigReader.getApiConfig("base_url", "https://automationexercise.com");
        RestAssured.baseURI = baseUrl;
        RestAssured.filters(new RequestLoggingFilter(LogDetail.METHOD), new ResponseLoggingFilter(LogDetail.STATUS));
    }
}
