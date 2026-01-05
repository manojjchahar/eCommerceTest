package com.ecommercefull.stepDefinitions;

import com.ecommercefull.pages.ProductPage;
import com.ecommercefull.utils.ConfigReader;
import com.ecommercefull.utils.DriverFactory;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

public class ProductSteps {

    private WebDriver driver;
    private ProductPage productPage;

    @Given("user is on the products page")
    public void user_is_on_the_products_page() {
        driver = DriverFactory.getDriver();
        String baseUrl = ConfigReader.getUiConfig("base_url", "https://www.automationexercise.com");
        String productsPath = ConfigReader.getUiConfig("products_page", "/products");
        driver.get(baseUrl + productsPath);
        productPage = new ProductPage(driver);
    }

    @When("user searches for product {string}")
    public void user_searches_for_product(String query) {
        productPage.search(query);
    }

    @Then("products should be listed")
    public void products_should_be_listed() {
        Assert.assertTrue(productPage.getProductCount() > 0, "Expected some products in listing");
    }

    @When("user adds first product to cart")
    public void user_adds_first_product_to_cart() {
        productPage.addFirstProductToCart();
    }
}
