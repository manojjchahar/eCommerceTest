package com.ecommercefull.apiTests;

import com.ecommercefull.api.AccountAPI;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

public class AccountAPITest {

    private final AccountAPI accountAPI = new AccountAPI();

    @Test(description = "Get user by email returns 200 for valid/known email")
    public void get_user_by_email_should_return_200() {
        Response res = accountAPI.getUserByEmail("testuser@example.com");
        Assert.assertEquals(res.statusCode(), 200);
    }
}
