package com.ecommercefull.base;

import com.ecommercefull.utils.ConfigReader;
import com.ecommercefull.utils.DriverFactory;
import com.ecommercefull.utils.ExtentReportManager;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

public class BaseTest {

    protected WebDriver driver;

    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        String browser = System.getProperty("browser", "chrome");
        DriverFactory.init(browser);
        driver = DriverFactory.getDriver();
        driver.manage().window().maximize();
        String baseUrl = ConfigReader.getUiConfig("base_url", "https://www.automationexercise.com");
        driver.get(baseUrl);
        ExtentReportManager.startTest(getClass().getSimpleName());
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
        ExtentReportManager.flush();
    }
}
