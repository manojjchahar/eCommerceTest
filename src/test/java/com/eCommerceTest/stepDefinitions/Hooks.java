package com.eCommerceTest.stepDefinitions;

import com.eCommerceTest.base.BaseTest;
import com.eCommerceTest.base.ScenarioContext;
import com.eCommerceTest.utils.BrowserManager;
import com.eCommerceTest.utils.ConfigReader;
import com.eCommerceTest.utils.UiNavigator;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.BeforeAll;
import io.cucumber.java.Scenario;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Properties;

public class Hooks extends BaseTest {

    private static final Logger log = LoggerFactory.getLogger(Hooks.class);

    /**
     * Runs once before all scenarios — writes Allure environment info, categories,
     * executor info, and copies history from the previous report for trend tracking.
     */
    @BeforeAll
    public static void setupAllureEnvironment() {
        try {
            Path resultsDir = Path.of("target/allure-results");
            Files.createDirectories(resultsDir);

            // ── Copy history from last report so Trends section works ──
            // Priority: Check persistent backup (survives mvn clean), then target directory
            Path persistentHistory = Path.of(System.getProperty("user.dir"), ".allure", "history");
            Path targetHistory = Path.of("target", "allure-report", "history");
            Path srcHistoryDir = Files.exists(persistentHistory) ? persistentHistory
                    : (Files.exists(targetHistory) ? targetHistory : null);

            Path historyDest = resultsDir.resolve("history");
            if (srcHistoryDir != null) {
                Files.createDirectories(historyDest);
                try (var files = Files.list(srcHistoryDir)) {
                    files.forEach(src -> {
                        try {
                            Files.copy(src, historyDest.resolve(src.getFileName()),
                                    StandardCopyOption.REPLACE_EXISTING);
                        } catch (IOException ignored) { }
                    });
                }
                log.info("Copied Allure history from {} for trend tracking", srcHistoryDir);
            }

            // ── Environment info shown in Allure "Environment" widget ──
            Properties env = new Properties();
            env.setProperty("Browser", System.getProperty("browser", "chrome"));
            env.setProperty("Headless", System.getProperty("headless", "true"));
            env.setProperty("OS", System.getProperty("os.name", "unknown"));
            env.setProperty("Java Version", System.getProperty("java.version", "unknown"));
            env.setProperty("Base URL",
                    ConfigReader.getUiConfig("base_url", "https://www.automationexercise.com"));
            String gridUrl = System.getProperty("gridUrl", "");
            if (!gridUrl.isEmpty()) {
                env.setProperty("Grid URL", gridUrl);
            }
            try (OutputStream out = Files.newOutputStream(resultsDir.resolve("environment.properties"))) {
                env.store(out, "Allure Environment");
            }

            // ── Executor info shown in Allure "Executors" widget ──
            Files.writeString(resultsDir.resolve("executor.json"), """
                    {
                      "name": "Local Machine",
                      "type": "local",
                      "buildName": "mvn test @ %s",
                      "buildOrder": %d
                    }
                    """.formatted(
                    java.time.LocalDateTime.now().format(
                            java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                    System.currentTimeMillis() / 1000));

            // ── Categories for grouping failures in the "Categories" tab ──
            Files.writeString(resultsDir.resolve("categories.json"), """
                    [
                      {
                        "name": "Assertion Failures",
                        "matchedStatuses": ["failed"],
                        "messageRegex": ".*AssertionError.*|.*AssertionFailedError.*"
                      },
                      {
                        "name": "Element Not Found",
                        "matchedStatuses": ["broken"],
                        "messageRegex": ".*NoSuchElementException.*"
                      },
                      {
                        "name": "Timeout Issues",
                        "matchedStatuses": ["broken"],
                        "messageRegex": ".*TimeoutException.*|.*Wait timed out.*"
                      },
                      {
                        "name": "Infrastructure / Other",
                        "matchedStatuses": ["broken"]
                      }
                    ]
                    """);

            log.info("Allure environment, executor, and categories written to {}", resultsDir);
        } catch (Exception e) {
            log.warn("Failed to write Allure metadata: {}", e.getMessage());
        }
    }

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
