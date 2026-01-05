package com.eCommerceTest.screenplay.abilities;

import com.eCommerceTest.screenplay.Ability;
import org.openqa.selenium.WebDriver;

public class BrowseTheWeb implements Ability {
    private final WebDriver driver;

    private BrowseTheWeb(WebDriver driver) {
        this.driver = driver;
    }

    public static BrowseTheWeb with(WebDriver driver) {
        return new BrowseTheWeb(driver);
    }

    public WebDriver getDriver() {
        return driver;
    }
}
