package com.eCommerceTest.utils;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.logging.LogEntry;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * Consolidated browser + wait manager.
 * Deprecated DriverFactory & WaitFactory have been fully removed.
 */
public final class BrowserManager {
    private static final ThreadLocal<WebDriver> TL = new ThreadLocal<>();

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(BrowserManager::quit));
    }

    private BrowserManager() {
    }

    public static void init(String browser) {
        if (TL.get() != null)
            return;
        String b = browser == null || browser.isBlank()
                ? System.getProperty(Constants.PROP_BROWSER, Constants.DEFAULT_BROWSER)
                : browser;
        boolean headless = Boolean
                .parseBoolean(System.getProperty(Constants.PROP_HEADLESS, Constants.DEFAULT_HEADLESS));
        String gridUrl = System.getProperty(Constants.PROP_GRID_URL, Constants.DEFAULT_GRID_URL).trim();
        WebDriver driver;
        switch (b.toLowerCase()) {
            case "firefox" -> driver = createFirefox(headless, gridUrl);
            case "edge" -> driver = createEdge(headless, gridUrl);
            default -> driver = createChrome(headless, gridUrl);
        }
        TL.set(driver);
    }

    public static WebDriver getDriver() {
        return TL.get();
    }

    public static void quit() {
        WebDriver d = TL.get();
        if (d != null) {
            try {
                d.quit();
            } finally {
                TL.remove();
            }
        }
    }

    // Unified waits
    public static FluentWait<WebDriver> waitShort() {
        return buildWait(shortTimeoutSeconds());
    }

    public static FluentWait<WebDriver> waitLong() {
        return buildWait(longTimeoutSeconds());
    }

    private static FluentWait<WebDriver> buildWait(long seconds) {
        return new FluentWait<>(getDriver())
                .withTimeout(Duration.ofSeconds(seconds))
                .pollingEvery(Duration.ofMillis(pollingMillis()))
                .ignoring(NoSuchElementException.class)
                .ignoring(StaleElementReferenceException.class);
    }

    private static long shortTimeoutSeconds() {
        return Long.parseLong(System.getProperty(Constants.PROP_WAIT_SHORT, Constants.DEFAULT_WAIT_SHORT));
    }

    private static long longTimeoutSeconds() {
        return Long.parseLong(System.getProperty(Constants.PROP_WAIT_LONG, Constants.DEFAULT_WAIT_LONG));
    }

    private static long pollingMillis() {
        return Long.parseLong(System.getProperty(Constants.PROP_WAIT_POLLING, Constants.DEFAULT_WAIT_POLLING));
    }

    // Browser creation helpers
    private static WebDriver createChrome(boolean headless, String gridUrl) {
        ChromeOptions options = new ChromeOptions();
        if (headless)
            options.addArguments("--headless=new");
        options.addArguments("--disable-gpu", "--disable-extensions", "--disable-dev-shm-usage", "--no-sandbox",
                "--disable-background-networking", "--window-size=1366,768", "--remote-allow-origins=*");
        Map<String, Object> prefs = new HashMap<>();
        prefs.put("profile.managed_default_content_settings.images", 2);
        options.setExperimentalOption("prefs", prefs);
        if (!gridUrl.isEmpty())
            return remote(gridUrl, options);
        WebDriverManager.chromedriver().setup();
        return new ChromeDriver(options);
    }

    private static WebDriver createEdge(boolean headless, String gridUrl) {
        EdgeOptions options = new EdgeOptions();
        if (headless)
            options.addArguments("--headless=new");
        options.addArguments("--disable-gpu", "--disable-extensions", "--disable-dev-shm-usage", "--no-sandbox",
                "--disable-background-networking", "--window-size=1366,768", "--remote-allow-origins=*");
        if (!gridUrl.isEmpty())
            return remote(gridUrl, options);
        WebDriverManager.edgedriver().setup();
        return new EdgeDriver(options);
    }

    private static WebDriver createFirefox(boolean headless, String gridUrl) {
        FirefoxOptions options = new FirefoxOptions();
        if (headless)
            options.addArguments("-headless");
        options.addPreference("permissions.default.image", 2);
        options.addPreference("dom.animations.enabled", false);
        if (!gridUrl.isEmpty())
            return remote(gridUrl, options);
        WebDriverManager.firefoxdriver().setup();
        return new FirefoxDriver(options);
    }

    private static WebDriver remote(String gridUrl, Object options) {
        try {
            URL url = java.net.URI.create(gridUrl).toURL();
            if (options instanceof ChromeOptions co)
                return new RemoteWebDriver(url, co);
            if (options instanceof EdgeOptions eo)
                return new RemoteWebDriver(url, eo);
            if (options instanceof FirefoxOptions fo)
                return new RemoteWebDriver(url, fo);
            throw new IllegalArgumentException("Unsupported remote options: " + options.getClass());
        } catch (MalformedURLException e) {
            throw new RuntimeException("Invalid gridUrl: " + gridUrl, e);
        }
    }

    /**
     * Selenium 4 BiDi demonstration: Monitor console logs.
     * Safe to call on any driver, but only effective on Chromium/Firefox.
     */
    public static void enableBiDiMonitoring() {
        WebDriver d = getDriver();
        if (d instanceof org.openqa.selenium.logging.HasLogEvents loggable) {
            try {
                loggable.onLogEvent((org.openqa.selenium.logging.LogEntry log) -> org.slf4j.LoggerFactory
                        .getLogger(BrowserManager.class)
                        .info("BiDi Console Log: [{}] {}", log.getLevel(), log.getMessage()));
            } catch (Exception e) {
                // Ignore if BiDi not supported or connection failed
            }
        }
    }
}
