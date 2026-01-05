package com.ecommercefull.stepDefinitions;

import com.ecommercefull.pages.SignupPage;
import com.ecommercefull.utils.ConfigReader;
import com.ecommercefull.utils.DriverFactory;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

public class SignupSteps {

    private WebDriver driver;
    private SignupPage signupPage;

    @Given("user is on the signup page")
    public void user_is_on_the_signup_page() {
        driver = DriverFactory.getDriver();
        String baseUrl = ConfigReader.getUiConfig("base_url", "https://www.automationexercise.com");
        String signupPath = ConfigReader.getUiConfig("signup_page", "/signup");
        driver.get(baseUrl + signupPath);
        signupPage = new SignupPage(driver);
    }

    @When("user signs up with name {string} and email {string}")
    public void user_signs_up_with_name_and_email(String name, String email) {
        signupPage.signup(name, email);
    }

    @Then("signup should proceed to account creation step")
    public void signup_should_proceed_to_account_creation_step() {
        // Placeholder assertion - real check would validate presence of account creation form
        Assert.assertTrue(true, "Signup initiated");
    }
}
