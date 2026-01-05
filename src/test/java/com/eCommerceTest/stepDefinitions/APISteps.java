package com.ecommercefull.stepDefinitions;

import com.ecommercefull.api.AccountAPI;
import com.ecommercefull.api.AuthAPI;
import com.ecommercefull.api.ProductAPI;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import org.testng.Assert;

public class APISteps {

    private final AuthAPI authAPI = new AuthAPI();
    private final ProductAPI productAPI = new ProductAPI();
    private final AccountAPI accountAPI = new AccountAPI();

    private Response response;

    @Given("products exist in the catalog")
    public void products_exist_in_the_catalog() {
        Response res = productAPI.productsList();
        Assert.assertEquals(res.statusCode(), 200);
    }

    @When("I search products by keyword {string}")
    public void i_search_products_by_keyword(String keyword) {
        response = productAPI.searchProduct(keyword);
    }

    @Then("search results should return status {int}")
    public void search_results_should_return_status(Integer code) {
        Assert.assertEquals(response.statusCode(), code.intValue());
    }

    @When("I login via API with email {string} and password {string}")
    public void i_login_via_api(String email, String password) {
        response = authAPI.login(email, password);
    }

    @Then("API response code should be {int}")
    public void api_response_code_should_be(Integer code) {
        Assert.assertEquals(response.statusCode(), code.intValue());
    }

    @When("I fetch user by email {string}")
    public void i_fetch_user_by_email(String email) {
        response = accountAPI.getUserByEmail(email);
    }
}
