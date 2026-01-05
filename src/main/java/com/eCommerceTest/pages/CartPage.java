package com.eCommerceTest.pages;

import com.eCommerceTest.base.BaseTest;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;

import java.time.Duration;
import java.util.List;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CartPage extends BaseTest {

    private static final Logger log = LoggerFactory.getLogger(CartPage.class);

    private final WebDriver driver;
    private final By cartItems = By.cssSelector("#cart_info_table >tbody >tr");
    private final By cartItemDescriptions = By.cssSelector(".cart_description >h4 >a");
    private final By lineTotals = By.cssSelector("#cart_info_table tbody tr td:nth-child(6) p");
    private final By proceedToCheckoutBtn = By.cssSelector("a.check_out, a[href*='checkout']");

    public CartPage(WebDriver driver) {
        this.driver = driver;
    }

    public int getItemsCount() {
        log.debug("CartPage.getItemsCount start");
        try {
            // Do not wait for "visibility of all" because an empty cart has no rows.
            List<WebElement> rows = driver.findElements(cartItems);
            int count = rows == null ? 0 : rows.size();
            log.debug("CartPage.getItemsCount end count={}", count);
            return count;
        } catch (Exception e) {
            // If DOM re-renders or table disappears, treat as empty cart.
            log.debug("CartPage.getItemsCount encountered exception, treating as empty: {}", e.getMessage());
            return 0;
        }
    }

    public boolean verifyAddedProduct(String itemDesc) {
        log.debug("CartPage.verifyAddedProduct start itemDesc='{}'", itemDesc);
        FluentWait<WebDriver> wait = new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(15))
                .pollingEvery(Duration.ofSeconds(1));
        wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(cartItemDescriptions));
        List<WebElement> itemDescList = driver.findElements(cartItemDescriptions);

        boolean result = itemDescList.stream()
                .anyMatch(item -> item.getText().equals(itemDesc));
        log.info("CartPage.verifyAddedProduct itemDesc='{}' result={}", itemDesc, result);
        log.debug("CartPage.verifyAddedProduct end result={}", result);
        return result;
    }

    public boolean removeAddedProduct(String description) {
        log.debug("CartPage.removeAddedProduct start description='{}'", description);
        FluentWait<WebDriver> wait = new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(15))
                .pollingEvery(Duration.ofSeconds(1));

        // Ensure cart item descriptions are visible once, then work with cached lists
        wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(cartItemDescriptions));
        List<WebElement> descriptionElements = driver.findElements(cartItemDescriptions);
        List<WebElement> itemRows = driver.findElements(cartItems);

        int index = IntStream.range(0, descriptionElements.size())
                .filter(i -> description.equals(descriptionElements.get(i).getText()))
                .findFirst()
                .orElse(-1);

        if (index == -1) {
            log.warn("Item with description '{}' not found in cart.", description);
            log.debug("CartPage.removeAddedProduct end false (not found)");
            return false;
        }

        int countBefore = getItemsCount();
        WebElement row = itemRows.get(index);
        WebElement deleteButton = row.findElement(By.cssSelector(".cart_delete"));

        // Bring target element into view before interacting
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", deleteButton);

        if (!clickDeleteWithFallbacks(deleteButton, wait)) {
            log.error("Failed to click delete button for '{}'", description);
            log.debug("CartPage.removeAddedProduct end false (click failed)");
            return false;
        }

        boolean removed = waitUntilRowRemovedOrCountDecreased(row, countBefore);
        if (!removed) {
            log.error("Timed out waiting for removal of item '{}'", description);
        }
        log.debug("CartPage.removeAddedProduct end removed={}", removed);
        return removed;
    }

    private boolean clickDeleteWithFallbacks(WebElement button, FluentWait<WebDriver> wait) {
        Actions actions = new Actions(driver);
        try {
            wait.until(ExpectedConditions.elementToBeClickable(button));
            actions.moveToElement(button).pause(Duration.ofMillis(250)).click(button).build().perform();
            return true;
        } catch (Exception primaryClickEx) {
            log.debug("Primary click failed, trying fallback click(): {}", primaryClickEx.getMessage());
            try {
                wait.until(ExpectedConditions.elementToBeClickable(button));
                button.click();
                return true;
            } catch (Exception normalClickEx) {
                log.debug("Normal click failed, trying JS click(): {}", normalClickEx.getMessage());
                try {
                    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", button);
                    return true;
                } catch (Exception jsEx) {
                    log.error("All click strategies failed for delete button.", jsEx);
                    return false;
                }
            }
        }
    }

    private boolean waitUntilRowRemovedOrCountDecreased(WebElement row, int countBefore) {
        try {
            return new FluentWait<>(driver)
                    .withTimeout(Duration.ofSeconds(10))
                    .pollingEvery(Duration.ofMillis(250))
                    .ignoring(Exception.class)
                    .until(d -> {
                        boolean rowGone = ExpectedConditions.stalenessOf(row).apply(d) == Boolean.TRUE;
                        int currentCount = getItemsCount();
                        boolean countDecreased = currentCount < countBefore;
                        boolean cartEmpty = currentCount == 0;
                        return rowGone || countDecreased || cartEmpty;

                    });
        } catch (Exception e) {
            log.error("Exception while waiting for row removal.", e);
            int current = getItemsCount();
            return current < countBefore || current == 0;

        }
    }

    public boolean verifyItemRemoval(String itemDesc) {
        log.debug("CartPage.verifyItemRemoval start itemDesc='{}'", itemDesc);
        try {
            // Poll until either the specific item is gone or the cart is empty.
            boolean removed = new FluentWait<>(driver)
                    .withTimeout(Duration.ofSeconds(10))
                    .pollingEvery(Duration.ofMillis(300))
                    .ignoring(Exception.class)
                    .until(d -> {
                        int countNow = getItemsCount();
                        if (countNow == 0)
                            return true;
                        List<WebElement> items = d.findElements(cartItemDescriptions);
                        return items.stream().map(WebElement::getText).noneMatch(t -> t.equals(itemDesc));
                    });
            log.info("CartPage.verifyItemRemoval itemDesc='{}' removed={}", itemDesc, removed);
            log.debug("CartPage.verifyItemRemoval end result={}", removed);
            return removed;
        } catch (Exception e) {
            log.error("verifyItemRemoval exception: {}", e.getMessage(), e);
            // Fall back to a last check
            int countNow = getItemsCount();
            if (countNow == 0)
                return true;
            List<WebElement> items = driver.findElements(cartItemDescriptions);
            boolean result = items.stream().map(WebElement::getText).noneMatch(t -> t.equals(itemDesc));
            log.info("CartPage.verifyItemRemoval (fallback) itemDesc='{}' removed={}", itemDesc, result);
            return result;
        }
    }

    public double getCartTotalAmount() {
        log.debug("CartPage.getCartTotalAmount start");
        List<WebElement> totals = driver.findElements(lineTotals);
        double sum = totals.stream()
                .map(WebElement::getText)
                .mapToDouble(this::parseAmount)
                .sum();
        log.info("CartPage.getCartTotalAmount total={}", sum);
        log.debug("CartPage.getCartTotalAmount end total={}", sum);
        return sum;
    }

    private double parseAmount(String raw) {
        if (raw == null)
            return 0.0;
        String digits = raw.replaceAll("[^0-9.]", "");
        if (digits.isEmpty())
            return 0.0;
        try {
            return Double.parseDouble(digits);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    public CheckoutPage proceedToCheckout() {
        log.debug("CartPage.proceedToCheckout start");
        WebElement btn = driver.findElement(proceedToCheckoutBtn);
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", btn);
        btn.click();
        log.debug("CartPage.proceedToCheckout end navigating to CheckoutPage");
        return new CheckoutPage(driver);
    }
}
