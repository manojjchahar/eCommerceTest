package com.eCommerceTest.stepDefinitions;

import com.eCommerceTest.base.BaseTest;
import com.eCommerceTest.base.ScenarioContext;
import com.eCommerceTest.utils.BrowserManager;
import com.eCommerceTest.utils.UiNavigator;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Hooks extends BaseTest {

    private static final Logger log = LoggerFactory.getLogger(Hooks.class);

    @Before(order = 0)
    public void beforeScenario(Scenario scenario) {
        long start = System.nanoTime();
        ScenarioContext.get().set(CTX_SCENARIO_START, start);
        String browser = System.getProperty("browser", "chrome");
        BrowserManager.init(browser);
        WebDriver driver = BrowserManager.getDriver();
        if (driver != null) {
            try {
                driver.manage().window().maximize();
            } catch (Exception ignored) {
            }
        }
        UiNavigator.openBase(driver);
        BrowserManager.enableBiDiMonitoring();
        ScenarioContext.get().setDriver(driver);
        if (driver != null) {
            log.info("SCENARIO START name='{}' tags={} browser={} url='{}'",
                    scenario.getName(), scenario.getSourceTagNames(), browser, driver.getCurrentUrl());
        } else {
            log.info("SCENARIO START name='{}' tags={} browser={} (driver init failed)",
                    scenario.getName(), scenario.getSourceTagNames(), browser);
        }
    }

    @After
    public void afterScenario(Scenario scenario) {
        Long start = ScenarioContext.get().get(CTX_SCENARIO_START);
        long durationMs = start == null ? -1 : (System.nanoTime() - start) / 1_000_000;
        WebDriver driver = BrowserManager.getDriver();
        if (driver != null) {
            try {
                if (scenario.isFailed()) {
                    try {
                        byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
                        scenario.attach(screenshot, "image/png", "screenshot-on-failure");
                        log.warn("SCENARIO FAILED name='{}' durationMs={} attachingScreenshot", scenario.getName(),
                                durationMs);
                    } catch (Exception scrEx) {
                        log.error("Screenshot capture failed for scenario '{}'", scenario.getName(), scrEx);
                    }
                } else {
                    log.info("SCENARIO END name='{}' status=PASSED durationMs={}", scenario.getName(), durationMs);
                }
            } finally {
                BrowserManager.quit();
                ScenarioContext.clear();
            }
        } else {
            log.info("SCENARIO END name='{}' status={} durationMs={} (no driver)", scenario.getName(),
                    scenario.getStatus(), durationMs);
            ScenarioContext.clear();
        }
    }
}
