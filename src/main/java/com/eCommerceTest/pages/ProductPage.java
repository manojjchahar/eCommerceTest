package com.ecommercefull.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

public class ProductPage {

    private final WebDriver driver;

    private final By searchInput = By.id("search_product");
    private final By searchButton = By.id("submit_search");
    private final By productCards = By.cssSelector(".features_items .product-image-wrapper");
    private final By addToCartButtons = By.cssSelector(".product-overlay a.add-to-cart");
    private final By continueShopping = By.cssSelector("button.close-modal");

    public ProductPage(WebDriver driver) {
        this.driver = driver;
    }

    public void search(String query) {
        driver.findElement(searchInput).clear();
        driver.findElement(searchInput).sendKeys(query);
        driver.findElement(searchButton).click();
    }

    public int getProductCount() {
        return driver.findElements(productCards).size();
    }

    public void addFirstProductToCart() {
        List<WebElement> products = driver.findElements(productCards);
        if (!products.isEmpty()) {
            products.get(0).findElement(addToCartButtons).click();
            List<WebElement> close = driver.findElements(continueShopping);
            if (!close.isEmpty()) close.get(0).click();
        }
    }
}
