package com.eCommerceTest.base;

import com.eCommerceTest.pages.*;
import com.eCommerceTest.utils.BrowserManager;
import com.eCommerceTest.utils.ConfigReader;
import com.eCommerceTest.utils.ExtentReportManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.util.List;

public class BaseTest {

    protected WebDriver driver; // , made protected to allow subclass access
    protected List<WebElement> descriptions; // used by some legacy steps
    protected LoginPage loginPage; // 
    protected SignupPage signupPage; // 
    protected ProductPage productPage; // 
    protected String cartConfirmMessage; // 
    protected CartPage cartPage; // 
    protected double cartTotal; // 
    protected double checkoutTotal; // 
    protected CheckoutPage checkoutPage; // 
    protected PaymentPage paymentPage; // 
    public static final String CTX_SCENARIO_START = "_scenarioStartNanos";

    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        String browser = System.getProperty("browser", "chrome");
        BrowserManager.init(browser);
        driver = BrowserManager.getDriver();
        driver.manage().window().maximize();
        String baseUrl = ConfigReader.getUiConfig("base_url", "https://www.automationexercise.com");
        driver.get(baseUrl);
        ExtentReportManager.startTest(getClass().getSimpleName());
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        if (driver != null) {
            BrowserManager.quit();
        }
        ExtentReportManager.flush();
    }

    // Getters remain available; setters unnecessary because subclasses directly assign protected fields
    public WebDriver getDriver() { return driver; }
    public ProductPage getProductPage() { return productPage; }
    public String getCartConfirmMessage() { return cartConfirmMessage; }
    public double getCartTotal() { return cartTotal; }
    public double getCheckoutTotal() { return checkoutTotal; }
}
