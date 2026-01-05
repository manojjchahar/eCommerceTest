package com.eCommerceTest.base;

import io.restassured.specification.RequestSpecification;

/**
 * Simple contract for API clients allowing mocking and alternate implementations.
 */
public interface IApiClient {
    RequestSpecification spec();
}

