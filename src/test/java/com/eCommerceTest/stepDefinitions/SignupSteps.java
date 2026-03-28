package com.eCommerceTest.stepDefinitions;

import com.eCommerceTest.base.BaseTest;
import com.eCommerceTest.base.ScenarioContext;
import com.eCommerceTest.models.User;
import com.eCommerceTest.models.UserRegistrationData;
import com.eCommerceTest.pages.SignupPage;
import com.eCommerceTest.utils.*;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

public class SignupSteps extends BaseTest {

    private WebDriver driver;

    private static final Logger log = LoggerFactory.getLogger(SignupSteps.class);

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

    @Given("user is on the signup page")
    public void user_is_on_the_signup_page() {
        long t = stepStart();
        try {
            driver = BrowserManager.getDriver();
            UiNavigator.openSignup(driver);
            signupPage = new SignupPage(driver);
            stepEnd("navigateSignup", t, true, "url=" + driver.getCurrentUrl());
        } catch (Exception e) {
            stepFail("navigateSignup", t, e);
            throw e;
        }
    }

    @When("user signs up with {string}")
    public void user_signs_up_with(String username) {
        long t = stepStart();
        try {
            User user = TestDataManager.getUserByName(username)
                    .orElseThrow(() -> new RuntimeException("User not found: " + username));
            
            signupPage.signup(user.getName(), user.getEmail());
            ScenarioContext.get().set("currentUser", user);
            boolean duplicate = signupPage.isDuplicateEmailErrorVisible();
            ScenarioContext.get().set(Constants.CTX_DUPLICATE, duplicate);
            if (duplicate) {
                String msg = signupPage.getDuplicateEmailErrorText();
                ScenarioContext.get().set(Constants.CTX_DUPLICATE_MSG, msg);
                ScenarioContext.get().set(Constants.CTX_ABORT, true);
                stepEnd("signupUser", t, true, "duplicateEmail msg=" + msg);
            } else {
                ScenarioContext.get().set(Constants.CTX_ABORT, false);
                stepEnd("signupUser", t, true, "email=" + user.getEmail());
            }
        } catch (Exception e) {
            stepFail("signupUser", t, e);
            throw e;
        }
    }

    @Then("user should be on signup details page with {string}")
    public void userShouldBeOnSignupDetailsPageWith(String message) {
        long t = stepStart();
        try {
            Boolean duplicate = ScenarioContext.get().get(Constants.CTX_DUPLICATE);
            if (Boolean.TRUE.equals(duplicate)) {
                String seen = ScenarioContext.get().get(Constants.CTX_DUPLICATE_MSG);
                Assert.assertTrue(seen != null && seen.toLowerCase().contains("email address already exist"));
                stepEnd("verifySignupDetailsDuplicate", t, true, "seenDuplicateMsg");
                return;
            }
            Assert.assertTrue(signupPage.verifySignupDetailsPage(message));
            stepEnd("verifySignupDetails", t, true, "expected=" + message);
        } catch (Exception e) {
            stepFail("verifySignupDetails", t, e);
            throw e;
        }
    }

    @Then("signup should proceed to account creation step")
    public void signup_should_proceed_to_account_creation_step() {
        long t = stepStart();
        try {
            Boolean abort = ScenarioContext.get().get(Constants.CTX_ABORT);
            if (Boolean.TRUE.equals(abort)) {
                stepEnd("proceedAccountCreationSkipped", t, true, "duplicateAbort");
                return;
            }
            Assert.assertTrue(signupPage.verifySignUpFormExist());
            stepEnd("proceedAccountCreation", t, true, "formVisible");
        } catch (Exception e) {
            stepFail("proceedAccountCreation", t, e);
            throw e;
        }
    }

    // ========== New steps using externalized JSON registration profiles ==========

    @And("user completes account creation with valid {string} type")
    public void userCompletesAccountCreationWithValidType(String profile) {
        long t = stepStart();
        try {
            Boolean abort = ScenarioContext.get().get(Constants.CTX_ABORT);
            if (Boolean.TRUE.equals(abort)) {
                stepEnd("completeAccountCreationSkipped", t, true, "duplicateAbort");
                return;
            }
            completeAccountCreationUsingProfile(profile);
            stepEnd("completeAccountCreation", t, true, "profile=" + profile);
        } catch (Exception e) {
            stepFail("completeAccountCreation", t, e);
            throw e;
        }
    }

    private void completeAccountCreationUsingProfile(String profile) {
        UserRegistrationData data = TestDataManager.getRegistrationDataByProfile(profile)
                .orElseGet(TestDataManager::getDefaultRegistrationData);

        // Fill and submit the registration form
        signupPage = new SignupPage(driver);
        signupPage.fillForm(data);
        signupPage.submit();

        // Keep email consistency with initial signup if provided earlier
        User prior = ScenarioContext.get().get("currentUser");
        String emailToUse = prior != null && prior.getEmail() != null ? prior.getEmail() : data.getEmail();
        String nameToUse = prior != null && prior.getName() != null ? prior.getName() : data.getName();

        // Store finalized user (now includes password) for later steps
        ScenarioContext.get().set("currentUser", new User(nameToUse, emailToUse, data.getPassword()));
        log.info("Account creation submitted for profile '{}' with email {}", profile, emailToUse);
    }

    @Then("user should be registered with {string}")
    public void userShouldBeRegisteredWith(String message) {
        long t = stepStart();
        try {
            Boolean abort = ScenarioContext.get().get(Constants.CTX_ABORT);
            if (Boolean.TRUE.equals(abort)) {
                stepEnd("verifyRegistrationSkipped", t, true, "duplicateAbort");
                return;
            }
            Assert.assertTrue(signupPage.verifySignUpSuccess(message));
            stepEnd("verifyRegistration", t, true, "message=" + message);
        } catch (Exception e) {
            stepFail("verifyRegistration", t, e);
            throw e;
        }
    }

    @And("user should be logged in after continue button")
    public void userShouldBeLoggedInAfterContinueButton() {
        long t = stepStart();
        try {
            Boolean abort = ScenarioContext.get().get(Constants.CTX_ABORT);
            if (Boolean.TRUE.equals(abort)) {
                stepEnd("loginAfterContinueSkipped", t, true, "duplicateAbort");
                return;
            }
            loginPage = signupPage.clickContinue();
            Assert.assertTrue(loginPage.isLoggedIn());
            stepEnd("loginAfterContinue", t, true, "loggedIn");
        } catch (Exception e) {
            stepFail("loginAfterContinue", t, e);
            throw e;
        }
    }

    @When("user attempts to sign up with existing user {string}")
    public void userAttemptsToSignUpWithExistingUser(String existingUserName) {
        long t = stepStart();
        try {
            driver = BrowserManager.getDriver();
            if (signupPage == null)
                signupPage = new SignupPage(driver);
            User user = TestDataManager.getUserByName(existingUserName)
                    .orElseThrow(() -> new RuntimeException("User not found: " + existingUserName));
            signupPage.signup(user.getName(), user.getEmail());
            boolean duplicate = signupPage.isDuplicateEmailErrorVisible();
            ScenarioContext.get().set(Constants.CTX_DUPLICATE, duplicate);
            ScenarioContext.get().set(Constants.CTX_DUPLICATE_MSG, signupPage.getDuplicateEmailErrorText());
            ScenarioContext.get().set(Constants.CTX_ABORT, duplicate);
            stepEnd("attemptDuplicateSignup", t, true, "duplicate=" + duplicate);
        } catch (Exception e) {
            stepFail("attemptDuplicateSignup", t, e);
            throw e;
        }
    }

    @Then("duplicate signup should be rejected with {string}")
    public void duplicateSignupShouldBeRejectedWith(String expectedMessage) {
        long t = stepStart();
        try {
            Boolean duplicateVisible = ScenarioContext.get().get(Constants.CTX_DUPLICATE);
            String actual = ScenarioContext.get().get(Constants.CTX_DUPLICATE_MSG);
            Assert.assertEquals(duplicateVisible, Boolean.TRUE);
            Assert.assertNotNull(actual);
            Assert.assertTrue(actual.toLowerCase().contains(expectedMessage.toLowerCase().trim()));
            stepEnd("verifyDuplicateSignup", t, true, "actualMsg=" + actual);
        } catch (Exception e) {
            stepFail("verifyDuplicateSignup", t, e);
            throw e;
        }
    }
}
