package com.ecommercefull.stepDefinitions;

import com.ecommercefull.pages.LoginPage;
import com.ecommercefull.utils.ConfigReader;
import com.ecommercefull.utils.DriverFactory;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

public class LoginSteps {

    private WebDriver driver;
    private LoginPage loginPage;

    @Given("user is on the login page")
    public void user_is_on_the_login_page() {
        driver = DriverFactory.getDriver();
        String baseUrl = ConfigReader.getUiConfig("base_url", "https://www.automationexercise.com");
        String loginPath = ConfigReader.getUiConfig("login_page", "/login");
        driver.get(baseUrl + loginPath);
        loginPage = new LoginPage(driver);
    }

    @When("user logs in with email {string} and password {string}")
    public void user_logs_in_with_email_and_password(String email, String password) {
        loginPage.login(email, password);
    }

    @Then("user should be logged in successfully")
    public void user_should_be_logged_in_successfully() {
        Assert.assertTrue(loginPage.isLoggedIn(), "Expected user to be logged in");
    }
}
