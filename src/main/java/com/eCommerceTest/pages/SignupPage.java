package com.ecommercefull.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class SignupPage {
    private final WebDriver driver;

    private final By nameInput = By.cssSelector("input[data-qa='signup-name']");
    private final By emailInput = By.cssSelector("input[data-qa='signup-email']");
    private final By signupButton = By.cssSelector("button[data-qa='signup-button']");

    public SignupPage(WebDriver driver) {
        this.driver = driver;
    }

    public void signup(String name, String email) {
        driver.findElement(nameInput).sendKeys(name);
        driver.findElement(emailInput).sendKeys(email);
        driver.findElement(signupButton).click();
    }
}
