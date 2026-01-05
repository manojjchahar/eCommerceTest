package com.ecommercefull.apiTests;

import com.ecommercefull.api.CartAPI;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

public class CartAPITest {

    private final CartAPI cartAPI = new CartAPI();

    @Test(enabled = false, description = "Placeholder: site may not expose cart API; enable if endpoint exists")
    public void get_cart_should_return_200() {
        Response res = cartAPI.getCart();
        Assert.assertEquals(res.statusCode(), 200);
    }
}
