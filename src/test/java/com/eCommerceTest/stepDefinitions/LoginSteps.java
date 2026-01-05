package com.eCommerceTest.stepDefinitions;

import com.eCommerceTest.base.BaseTest;
import com.eCommerceTest.base.ScenarioContext;
import com.eCommerceTest.models.User;
import com.eCommerceTest.pages.LoginPage;
import com.eCommerceTest.screenplay.Actor;
import com.eCommerceTest.screenplay.abilities.BrowseTheWeb;
import com.eCommerceTest.screenplay.tasks.Login;
import com.eCommerceTest.utils.BrowserManager;
import com.eCommerceTest.utils.TestDataManager;
import com.eCommerceTest.utils.UiNavigator;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

import java.util.Optional;

public class LoginSteps extends BaseTest {

    private WebDriver driver;
    // Reusable fallback user to avoid magic empty strings
    private static final User FALLBACK_USER = new User(null, "anonymous@example.test", "");

    private static final Logger log = LoggerFactory.getLogger(LoginSteps.class);

    private long stepStart() {
        return System.nanoTime();
    }

    private void stepEnd(String name, long start, boolean ok, String detail) {
        long ms = (System.nanoTime() - start) / 1_000_000;
        if (ok)
            log.info("STEP OK [{}] {}ms {}", name, ms, detail);
        else
            log.warn("STEP WARN [{}] {}ms {}", name, ms, detail);
    }

    private void stepFail(String name, long start, Exception e) {
        long ms = (System.nanoTime() - start) / 1_000_000;
        log.error("STEP FAIL [{}] {}ms {}", name, ms, e.getMessage(), e);
    }

    @Given("user is on the login page")
    public void user_is_on_the_login_page() {
        long t = stepStart();
        try {
            driver = BrowserManager.getDriver();
            UiNavigator.openLogin(driver);
            loginPage = new LoginPage(driver);
            stepEnd("navigateLogin", t, true, "url=" + driver.getCurrentUrl());
        } catch (Exception e) {
            stepFail("navigateLogin", t, e);
            throw e;
        }
    }

    @Then("user should be logged in successfully")
    public void user_should_be_logged_in_successfully() {
        long t = stepStart();
        try {
            Assert.assertTrue(loginPage.isLoggedIn());
            stepEnd("verifyLoggedIn", t, true, "loggedIn");
        } catch (Exception e) {
            stepFail("verifyLoggedIn", t, e);
            throw e;
        }
    }

    @When("user logs in with {string}")
    public void userLogsInWithUser(String user) {
        long t = stepStart();
        try {
            Optional<User> ctxUser = Optional.ofNullable(TestDataManager.getUserByName(user)
                    .orElseThrow(() -> new RuntimeException("User not found in test data: " + user)));
            User deafultUser = ctxUser
                    .orElseThrow(() -> new RuntimeException("User not found in test data: " + ctxUser));
            // Screenplay Pattern integration
            Actor actor = Actor.named("User");
            actor.can(BrowseTheWeb.with(driver));

            actor.attemptsTo(Login.withCredentials(deafultUser.getEmail(),
                    deafultUser.getPassword()));

            // Sync POM state for subsequent steps that still rely on it
            loginPage = new LoginPage(driver);

            ScenarioContext.get().set("currentUser", deafultUser);
            stepEnd("loginWithUser", t, true, "email=" + deafultUser.getEmail());
        } catch (Exception e) {
            stepFail("loginWithUser", t, e);
            setFallbackCurrentUser();
            throw e;
        }
    }

    @When("user logs in with default user credentials")
    public void userLogsInWithDefaultUserCredentials() {
        long t = stepStart();
        try {
            User user = TestDataManager.getDefaultUser();
            if (user == null)
                throw new RuntimeException("Default user not found in test data");

            // Screenplay Pattern integration
            Actor actor = Actor.named("DefaultUser");
            actor.can(BrowseTheWeb.with(driver));
            actor.attemptsTo(
                    Login.withCredentials(user.getEmail(), user.getPassword()));

            // Sync POM state
            loginPage = new LoginPage(driver);

            ScenarioContext.get().set("currentUser", user);
            stepEnd("loginDefaultUser", t, true, "email=" + user.getEmail());
        } catch (Exception e) {
            stepFail("loginDefaultUser", t, e);
            setFallbackCurrentUser();
            throw e;
        }
    }

    private void setFallbackCurrentUser() {
        ScenarioContext.get().set("currentUser", FALLBACK_USER);
        log.warn("Using fallback currentUser: default user not available");
    }

    @Then("user perform {string} successfully")
    public void userPerformActionSuccessfully(String action) {
        long t = stepStart();
        try {
            if (loginPage == null)
                loginPage = new LoginPage(BrowserManager.getDriver());
            boolean result;
            if (action.contains("Delete")) {
                result = loginPage.deleteAccount();
            } else if (action.contains("Logout")) {
                result = loginPage.logOutAccount();
            } else {
                result = false;
            }
            Assert.assertTrue(result, "Action failed: " + action);
            stepEnd("performAction", t, true, "action=" + action);
        } catch (Exception e) {
            stepFail("performAction", t, e);
            throw e;
        }
    }
}
