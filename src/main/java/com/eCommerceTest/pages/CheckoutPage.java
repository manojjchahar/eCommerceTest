package com.eCommerceTest.pages;

import com.eCommerceTest.base.BaseTest;
import com.eCommerceTest.utils.BrowserManager;
import com.eCommerceTest.utils.Logging;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;
import org.slf4j.Logger;

public class CheckoutPage extends BaseTest {

    private final WebDriver driver;
    private static final Logger log = Logging.getLogger(CheckoutPage.class);

    // Reuse same table structure for totals on checkout review
    private final By reviewTableLineTotals = By.cssSelector("#cart_info_table tbody tr td:nth-child(6) p");
    private final By placeOrderBtn = By.cssSelector("a.check_out, a[href*='payment']");

    public CheckoutPage(WebDriver driver) {
        this.driver = driver;
    }

    public double getCheckoutTotalAmount() {
        log.debug("CheckoutPage.getCheckoutTotalAmount start");
        try {
            BrowserManager.waitShort()
                    .until(ExpectedConditions.visibilityOfAllElementsLocatedBy(reviewTableLineTotals));
            List<WebElement> totals = driver.findElements(reviewTableLineTotals);
            double sum = totals.stream()
                    .map(WebElement::getText)
                    .mapToDouble(CheckoutPage::parseAmount)
                    .sum();
            log.info("CheckoutPage.getCheckoutTotalAmount total={}", sum);
            log.debug("CheckoutPage.getCheckoutTotalAmount end total={}", sum);
            return sum;
        } catch (Exception e) {
            log.error("CheckoutPage.getCheckoutTotalAmount error", e);
            return 0.0;
        }
    }

    public PaymentPage placeOrder() {
        log.debug("CheckoutPage.placeOrder start");
        try {
            BrowserManager.waitShort().until(ExpectedConditions.elementToBeClickable(placeOrderBtn));
            driver.findElement(placeOrderBtn).click();
            log.debug("CheckoutPage.placeOrder end success navigating to PaymentPage");
        } catch (Exception e) {
            log.error("CheckoutPage.placeOrder error clicking place order", e);
        }
        return new PaymentPage(driver);
    }

    private static double parseAmount(String raw) {
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
}
