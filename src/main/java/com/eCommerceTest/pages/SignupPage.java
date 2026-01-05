package com.eCommerceTest.pages;

import com.eCommerceTest.base.BaseTest;
import com.eCommerceTest.models.UserRegistrationData;
import com.eCommerceTest.utils.BrowserManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SignupPage extends BaseTest {
    private final WebDriver driver;
    private static final Logger logger = LoggerFactory.getLogger(SignupPage.class);

    private final By nameInput = By.cssSelector("input[data-qa='signup-name']");
    private final By emailInput = By.cssSelector("input[data-qa='signup-email']");
    private final By signupButton = By.cssSelector("button[data-qa='signup-button']");
    private final By accountInfoHeading = By.xpath("//h2[normalize-space(translate(., 'abcdefghijklmnopqrstuvwxyz', 'ABCDEFGHIJKLMNOPQRSTUVWXYZ'))='ENTER ACCOUNT INFORMATION']");
    private final By signUpForm = By.cssSelector(".login-form");
    private final By signUpSuccess = By.cssSelector("h2[data-qa='account-created'] b");
    private final By loggedInAs = By.cssSelector("a[href*='logout']");

    // Gender/title radio buttons (example locators)
    private final By titleMr = By.id("id_gender1");
    private final By titleMrs = By.id("id_gender2");

    private final By passwordInput = By.id("password");

    // Date of birth selects
    private final By daysSelect = By.id("days");
    private final By monthsSelect = By.id("months");
    private final By yearsSelect = By.id("years");

    // Address/company inputs
    private final By firstNameInput = By.id("first_name");
    private final By lastNameInput = By.id("last_name");
    private final By companyInput = By.id("company");
    private final By address1Input = By.id("address1");
    private final By address2Input = By.id("address2");
    private final By countrySelect = By.id("country");
    private final By stateInput = By.id("state");
    private final By cityInput = By.id("city");
    private final By zipcodeInput = By.id("zipcode");
    private final By mobileInput = By.id("mobile_number");

    private final By createAccountButton = By.cssSelector("button[data-qa='create-account']");
    private final By continueButton = By.cssSelector("a[data-qa='continue-button']");

    private final By duplicateEmailError = By.xpath("//*[contains(normalize-space(.), 'Email Address already exist')]|"
                                                    + "//p[contains(normalize-space(.), 'Email Address already exist')]");

    public SignupPage(WebDriver driver) {
        this.driver = driver;
    }

    public SignupPage signup(String name, String email) {
        logger.debug("SignupPage.signup start name={} email={}", name, email);
        driver.findElement(nameInput).clear();
        driver.findElement(nameInput).sendKeys(name);
        driver.findElement(emailInput).clear();
        driver.findElement(emailInput).sendKeys(email);
        driver.findElement(signupButton).click();
        logger.debug("SignupPage.signup end name={} email={}", name, email);
        return this; // no need to create a new instance
    }

    public boolean verifySignupDetailsPage(String expectedHeading) {
        logger.debug("SignupPage.verifySignupDetailsPage start expectedHeading={} ", expectedHeading);
        try {
            BrowserManager.waitShort().until(d -> !d.findElements(accountInfoHeading).isEmpty() || !d.findElements(signUpForm).isEmpty());
            if (driver.findElements(accountInfoHeading).isEmpty()) {
                logger.debug("SignupPage.verifySignupDetailsPage heading not found");
                return false;
            }
            String actual = driver.findElement(accountInfoHeading).getText().trim();
            logger.info("Actual sign up page heading is: {}",actual);
            boolean result = actual.equalsIgnoreCase(expectedHeading.trim());
            logger.debug("SignupPage.verifySignupDetailsPage end result={}", result);
            return result;
        } catch (Exception e) {
            logger.error("SignupPage.verifySignupDetailsPage error", e);
            return false; // timeout or stale element
        }
    }

    public boolean verifySignUpFormExist() {
        logger.debug("SignupPage.verifySignUpFormExist start");
        try {
            BrowserManager.waitShort().until(ExpectedConditions.visibilityOfElementLocated(signUpForm));
            logger.debug("SignupPage.verifySignUpFormExist end true");
            return true;
        } catch (Exception e) {
            logger.debug("SignupPage.verifySignUpFormExist end false", e);
            return false;
        }
    }

    public void fillForm(UserRegistrationData data) {
        logger.debug("SignupPage.fillForm start name={} email={} title={} dob={}/{}/{}", data.getName(), data.getEmail(), data.getTitle(), data.getDayOfBirth(), data.getMonthOfBirth(), data.getYearOfBirth());
        selectTitle(data.getTitle());
        // do not log password value
        type(passwordInput, mask("***"));
        type(firstNameInput, data.getFirstName());
        type(lastNameInput, data.getLastName());
        selectDob(data.getDayOfBirth(), data.getMonthOfBirth(), data.getYearOfBirth());
        type(companyInput, data.getCompany());
        type(address1Input, data.getAddress1());
        type(address2Input, data.getAddress2());
        selectCountry(data.getCountry());
        type(stateInput, data.getState());
        type(cityInput, data.getCity());
        type(zipcodeInput, data.getZipcode());
        type(mobileInput, data.getMobileNumber());
        logger.debug("SignupPage.fillForm end");
    }

    public void submit() {
        logger.debug("SignupPage.submit start");
        driver.findElement(createAccountButton).click();
        logger.debug("SignupPage.submit end");
    }

    private void selectTitle(String title) {
        logger.debug("SignupPage.selectTitle title={}", title);
        if (title == null) return;
        if (title.equalsIgnoreCase("Mr")) driver.findElement(titleMr).click();
        else driver.findElement(titleMrs).click();
    }

    private void selectDob(int day, int month, int year) {
        logger.debug("SignupPage.selectDob day={} month={} year={}", day, month, year);
        new Select(driver.findElement(daysSelect)).selectByValue(String.valueOf(day));
        new Select(driver.findElement(monthsSelect)).selectByValue(String.valueOf(month));
        new Select(driver.findElement(yearsSelect)).selectByValue(String.valueOf(year));
    }

    private void selectCountry(String country) {
        logger.debug("SignupPage.selectCountry country={}", country);
        new Select(driver.findElement(countrySelect)).selectByVisibleText(country);
    }

    private void type(By locator, String value) {
        // avoid logging the exact text for sensitive inputs; we can log locator only
        logger.debug("SignupPage.type locator={} valueLen={}", locator, value==null?0:value.length());
        if (value == null) return;
        driver.findElement(locator).clear();
        driver.findElement(locator).sendKeys(value);
    }

    private String mask(String s){ return s; }

    public boolean verifySignUpSuccess(String message) {
        logger.debug("SignupPage.verifySignUpSuccess start expected={}", message);
        try {
            BrowserManager.waitShort().until(ExpectedConditions.visibilityOfElementLocated(signUpSuccess));
            String actual = driver.findElement(signUpSuccess).getText().trim();
            logger.info("Actual signup success message: {}", actual);
            boolean result = actual.equalsIgnoreCase(message.trim());
            logger.debug("SignupPage.verifySignUpSuccess end result={}", result);
            return result;
        } catch (Exception e) {
            logger.error("SignupPage.verifySignUpSuccess error", e);
            return false;
        }
    }

    public LoginPage clickContinue() {
        logger.debug("SignupPage.clickContinue start");
        BrowserManager.waitShort().until(ExpectedConditions.visibilityOfElementLocated(continueButton));
        driver.findElement(continueButton).click();
        BrowserManager.waitShort().until(d -> !d.findElements(loggedInAs).isEmpty());
        logger.debug("SignupPage.clickContinue end");
        return new LoginPage(driver);

    }

    public boolean isDuplicateEmailErrorVisible() {
        logger.debug("SignupPage.isDuplicateEmailErrorVisible start");
        try {
            BrowserManager.waitShort()
                .until(d -> !d.findElements(accountInfoHeading).isEmpty() || !d.findElements(duplicateEmailError).isEmpty());
            boolean visible = !driver.findElements(duplicateEmailError).isEmpty();
            logger.debug("SignupPage.isDuplicateEmailErrorVisible end visible={}", visible);
            return visible;
        } catch (Exception e) {
            logger.debug("SignupPage.isDuplicateEmailErrorVisible end false", e);
            return false;
        }
    }

    public String getDuplicateEmailErrorText() {
        logger.debug("SignupPage.getDuplicateEmailErrorText start");
        try {
            String txt = driver.findElement(duplicateEmailError).getText().trim();
            logger.debug("SignupPage.getDuplicateEmailErrorText end text='{}'", txt);
            return txt;
        } catch (Exception e) {
            logger.debug("SignupPage.getDuplicateEmailErrorText end empty", e);
            return "";
        }
    }

    public boolean onAccountInfoPage() {
        boolean result = !driver.findElements(accountInfoHeading).isEmpty();
        logger.debug("SignupPage.onAccountInfoPage result={}", result);
        return result;
    }
}
