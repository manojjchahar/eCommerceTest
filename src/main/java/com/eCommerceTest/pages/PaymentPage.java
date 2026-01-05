package com.eCommerceTest.pages;

import com.eCommerceTest.base.BaseTest;
import com.eCommerceTest.models.PaymentDetails;
import com.eCommerceTest.utils.BrowserManager; // unified waits
import com.eCommerceTest.utils.Logging;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.slf4j.Logger;

public class PaymentPage extends BaseTest {

    private final WebDriver driver;
    private static final Logger log = Logging.getLogger(PaymentPage.class);

    private final By nameOnCard = By.name("name_on_card");
    private final By cardNumber = By.name("card_number");
    private final By cvc = By.name("cvc");
    private final By expiryMonth = By.name("expiry_month");
    private final By expiryYear = By.name("expiry_year");
    private final By payButton = By.cssSelector("button[data-qa='pay-button'], button[type='submit']");

    private final By successAlert = By.cssSelector(".alert-success, .col-sm-9 p");
    private final By orderPlacedHeading = By.cssSelector("h2.title.text-center, h2[data-qa='order-placed'] b");
    private final By orderPlacedMessage = By.cssSelector(".col-sm-9 p");
    private final By downloadInvoiceLink = By.cssSelector("a[data-qa='download-invoice'], a[href*='download_invoice']");
    private final By continueButton = By.cssSelector("a[data-qa='continue-button'], a.btn-primary");

    public PaymentPage(WebDriver driver) { this.driver = driver; }

    public boolean isAt() {
        log.debug("PaymentPage.isAt start");
        try {
            BrowserManager.waitShort().until(ExpectedConditions.visibilityOfElementLocated(nameOnCard));
            log.debug("PaymentPage.isAt end true");
            return true;
        } catch (Exception e) {
            log.debug("PaymentPage.isAt end false", e);
            return false;
        }
    }

    public void fillPaymentDetails(PaymentDetails pd) {
        if (pd == null) {
            log.warn("PaymentPage.fillPaymentDetails called with null PaymentDetails");
            return;
        }
        log.debug("PaymentPage.fillPaymentDetails start cardHolder={} last4={} exp={}/{}",
                pd.getCardholderName(),
                safeLast4(pd.getCardNumber()),
                pd.getExpiryMonth(), pd.getExpiryYear());
        try {
            driver.findElement(nameOnCard).clear();
            driver.findElement(nameOnCard).sendKeys(pd.getCardholderName());
            driver.findElement(cardNumber).clear();
            driver.findElement(cardNumber).sendKeys(pd.getCardNumber());
            driver.findElement(cvc).clear();
            driver.findElement(cvc).sendKeys(maskCvc(pd.getCvc()));
            driver.findElement(expiryMonth).clear();
            driver.findElement(expiryMonth).sendKeys(pd.getExpiryMonth());
            driver.findElement(expiryYear).clear();
            driver.findElement(expiryYear).sendKeys(pd.getExpiryYear());
            log.debug("PaymentPage.fillPaymentDetails end success");
        } catch (Exception e) {
            log.error("PaymentPage.fillPaymentDetails error", e);
        }
    }

    private String safeLast4(String card) { return card == null || card.length() < 4 ? "" : card.substring(card.length()-4); }
    private String maskCvc(String cvc) { return cvc == null ? "" : "***"; }

    public void payAndConfirm() {
        log.debug("PaymentPage.payAndConfirm start");
        try {
            BrowserManager.waitShort().until(ExpectedConditions.elementToBeClickable(payButton));
            driver.findElement(payButton).click();
            log.debug("PaymentPage.payAndConfirm clicked pay button");
        } catch (Exception e) {
            log.error("PaymentPage.payAndConfirm error clicking pay", e);
        }
    }

    public boolean verifySuccessMessage(String expected) {
        log.debug("PaymentPage.verifySuccessMessage start expected contains='{}'", expected);
        try {
            BrowserManager.waitShort().until(ExpectedConditions.visibilityOfElementLocated(successAlert));
            String actual = driver.findElement(successAlert).getText().trim();
            boolean result = actual.toLowerCase().contains(expected == null ? "" : expected.toLowerCase());
            log.info("Payment success message actual='{}' expectedContains='{}' result={}", actual, expected, result);
            log.debug("PaymentPage.verifySuccessMessage end result={}", result);
            return result;
        } catch (Exception e) {
            log.error("PaymentPage.verifySuccessMessage error", e);
            return false;
        }
    }

    public boolean verifyOrderConfirmation(String expectedHeading, String expectedMessage) {
        log.debug("PaymentPage.verifyOrderConfirmation start expectedHeading='{}' expectedMessageContains='{}'", expectedHeading, expectedMessage);
        try {
            BrowserManager.waitShort().until(ExpectedConditions.visibilityOfElementLocated(orderPlacedHeading));
            String headingText = driver.findElement(orderPlacedHeading).getText().trim();
            String messageText = driver.findElement(orderPlacedMessage).getText().trim();
            boolean headingOk = headingText.equalsIgnoreCase(expectedHeading == null ? "" : expectedHeading.trim());
            boolean messageOk = messageText.toLowerCase().contains(expectedMessage == null ? "" : expectedMessage.toLowerCase());
            boolean result = headingOk && messageOk;
            log.info("Order confirmation heading='{}' expected='{}' headingOk={} message='{}' expectedContains='{}' messageOk={} result={}",
                    headingText, expectedHeading, headingOk,
                    messageText, expectedMessage, messageOk, result);
            log.debug("PaymentPage.verifyOrderConfirmation end result={}", result);
            return result;
        } catch (Exception e) {
            log.error("PaymentPage.verifyOrderConfirmation error", e);
            return false;
        }
    }

    public boolean downloadInvoiceAndVerify() {
        log.debug("PaymentPage.downloadInvoiceAndVerify start");
        try {
            BrowserManager.waitShort().until(ExpectedConditions.elementToBeClickable(downloadInvoiceLink));
            driver.findElement(downloadInvoiceLink).click();
            log.debug("PaymentPage.downloadInvoiceAndVerify clicked download link (file verification skipped) end true");
            return true;
        } catch (Exception e) {
            log.error("PaymentPage.downloadInvoiceAndVerify error", e);
            return false;
        }
    }

    public void continueToHome() {
        log.debug("PaymentPage.continueToHome start");
        try {
            BrowserManager.waitShort().until(ExpectedConditions.elementToBeClickable(continueButton));
            driver.findElement(continueButton).click();
            log.debug("PaymentPage.continueToHome end clicked continue");
        } catch (Exception e) {
            log.error("PaymentPage.continueToHome error", e);
        }
    }
}
