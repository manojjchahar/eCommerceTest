package com.ecommercefull.stepDefinitions;

import com.ecommercefull.utils.ConfigReader;
import com.ecommercefull.utils.DriverFactory;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

public class Hooks {

    @Before(order = 0)
    public void beforeScenario() {
        String browser = System.getProperty("browser", "chrome");
        DriverFactory.init(browser);
        WebDriver driver = DriverFactory.getDriver();
        driver.manage().window().maximize();
        String baseUrl = ConfigReader.getUiConfig("base_url", "https://www.automationexercise.com");
        driver.get(baseUrl);
    }

    @After(order = 0)
    public void afterScenario(Scenario scenario) {
        if (scenario.isFailed()) {
            try {
                WebDriver driver = DriverFactory.getDriver();
                if (driver != null) {
                    byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
                    scenario.attach(screenshot, "image/png", "failure_screenshot");
                }
            } catch (Exception ignored) {}
        }
        DriverFactory.quit();
    }
}
