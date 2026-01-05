package com.ecommercefull.apiTests;

import com.ecommercefull.api.AuthAPI;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

public class LoginAPITest {

    private final AuthAPI authAPI = new AuthAPI();

    @Test(enabled = false, description = "Disabled by default: provide valid credentials and enable to run")
    public void login_should_return_200() {
        Response res = authAPI.login("testuser@example.com", "testpass");
        Assert.assertEquals(res.statusCode(), 200);
    }
}
