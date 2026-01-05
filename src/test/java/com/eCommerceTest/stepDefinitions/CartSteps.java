package com.ecommercefull.stepDefinitions;

import com.ecommercefull.pages.CartPage;
import com.ecommercefull.utils.ConfigReader;
import com.ecommercefull.utils.DriverFactory;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

public class CartSteps {

    private WebDriver driver;
    private CartPage cartPage;

    @Given("user is on the cart page")
    public void user_is_on_the_cart_page() {
        driver = DriverFactory.getDriver();
        String baseUrl = ConfigReader.getUiConfig("base_url", "https://www.automationexercise.com");
        String cartPath = ConfigReader.getUiConfig("cart_page", "/view_cart");
        driver.get(baseUrl + cartPath);
        cartPage = new CartPage(driver);
    }

    @Then("cart should have at least one item")
    public void cart_should_have_at_least_one_item() {
        Assert.assertTrue(cartPage.getItemsCount() > 0, "Expected at least one item in cart");
    }
}
