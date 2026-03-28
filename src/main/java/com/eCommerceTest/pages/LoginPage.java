package com.eCommerceTest.pages;

import com.eCommerceTest.base.BaseTest;
import com.eCommerceTest.utils.BrowserManager; // replaced WaitFactory
import com.eCommerceTest.utils.ConfigReader;
import com.eCommerceTest.utils.Logging;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.slf4j.Logger;

public class LoginPage extends BaseTest {

    private static final Logger log = Logging.getLogger(LoginPage.class);
    private final WebDriver driver;

    private final By emailInput = By.cssSelector("input[data-qa='login-email']");
    private final By passwordInput = By.cssSelector("input[data-qa='login-password']");
    private final By loginButton = By.cssSelector("button[data-qa='login-button']");
    private final By loggedInAs = By.cssSelector("a:has(i.fa-user)+b, a[href*='logout']");
    private final By deleteAcct = By.cssSelector(".fa-trash-o");
    private final By logoutAcct = By.cssSelector(".fa-lock");
    private final By deletedAcctMessg = By.cssSelector("h2[data-qa='account-deleted'] > b");

    public LoginPage(WebDriver driver) {
        this.driver = driver;
    }

    public void login(String email, String password) {
        log.debug("LoginPage.login start email={} pwdLen={}", email, password == null ? 0 : password.length());
        try {
            driver.findElement(emailInput).clear();
            driver.findElement(emailInput).sendKeys(email);

            // Selenium 4 Relative Locator demonstration
            // driver.findElement(passwordInput).clear();
            // driver.findElement(passwordInput).sendKeys(password);
            WebElement emailEl = driver.findElement(emailInput);
            driver.findElement(
                    org.openqa.selenium.support.locators.RelativeLocator.with(By.tagName("input")).below(emailEl))
                    .sendKeys(password);
            driver.findElement(loginButton).click();
            log.debug("LoginPage.login end email={}", email);
        } catch (Exception e) {
            log.error("LoginPage.login error email={}", email, e);
        }
    }

    public boolean isLoggedIn() {
        log.debug("LoginPage.isLoggedIn check start");
        boolean result = driver.findElements(loggedInAs)
                .stream()
                .anyMatch(WebElement::isDisplayed);
        log.debug("LoginPage.isLoggedIn end result={}", result);
        return result;
    }

    public boolean deleteAccount() {
        log.debug("LoginPage.deleteAccount start");
        try {
            BrowserManager.waitShort().until(ExpectedConditions.elementToBeClickable(deleteAcct));

            // Bypass potential ad overlap
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", driver.findElement(deleteAcct));

            try {
                BrowserManager.waitShort().until(ExpectedConditions.visibilityOfElementLocated(deletedAcctMessg));
            } catch (org.openqa.selenium.TimeoutException e) {
                // Workaround for automationexercise Google Ad interceptions
                if (driver.getCurrentUrl().contains("#google_vignette")) {
                    // Navigate directly to delete_account to bypass the trap
                    String baseUrl = ConfigReader.getUiConfig("base_url", "https://www.automationexercise.com");
                    driver.navigate().to(baseUrl + "/delete_account");
                    BrowserManager.waitShort().until(ExpectedConditions.visibilityOfElementLocated(deletedAcctMessg));
                } else {
                    throw e;
                }
            }

            String msg = driver.findElement(deletedAcctMessg).getText().trim();
            log.info("Account deleted message: {}", msg);
            log.debug("LoginPage.deleteAccount end success");
            return true;
        } catch (Exception e) {
            log.error("LoginPage.deleteAccount fail", e);
            throw new RuntimeException(e);
        }
    }

    public boolean logOutAccount() {
        log.debug("LoginPage.logOutAccount start");
        try {
            BrowserManager.waitShort().until(ExpectedConditions.elementToBeClickable(logoutAcct));
            driver.findElement(logoutAcct).click();
            log.debug("LoginPage.logOutAccount end success");
            return true;
        } catch (Exception e) {
            log.error("LoginPage.logOutAccount fail", e);
            throw new RuntimeException(e);
        }
    }
}
