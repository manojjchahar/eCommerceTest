package com.eCommerceTest.stepDefinitions;

import com.eCommerceTest.base.BaseTest;
import com.eCommerceTest.base.ScenarioContext;
import com.eCommerceTest.models.PaymentDetails;
import com.eCommerceTest.pages.*;
import com.eCommerceTest.utils.BrowserManager;
import com.eCommerceTest.utils.TestDataManager;
import com.eCommerceTest.utils.UiNavigator;
import io.cucumber.java.en.*;
import org.openqa.selenium.WebDriver;
import org.slf4j.*;
import org.testng.Assert;

public class CartSteps extends BaseTest {

    private WebDriver driver;

    private static final Logger log = LoggerFactory.getLogger(CartSteps.class);
    private long stepStart(){ return System.nanoTime(); }
    private void stepEnd(String name,long start, boolean ok, String detail){ long ms=(System.nanoTime()-start)/1_000_000; if(ok) log.info("STEP OK [{}] {}ms {}",name,ms,detail); else log.warn("STEP WARN [{}] {}ms {}",name,ms,detail);}
    private void stepFail(String name,long start, Exception e){ long ms=(System.nanoTime()-start)/1_000_000; log.error("STEP FAIL [{}] {}ms {}",name,ms,e.getMessage(),e);}

    @Given("user adds an item to cart")
    public void user_adds_an_item_to_cart() {
        long t=stepStart();
        try {
            driver = BrowserManager.getDriver();
            UiNavigator.openProducts(driver);
            productPage = new ProductPage(driver);
            boolean added = productPage.addFirstProductToCart();
            Assert.assertTrue(added, "Product Addition returned false");
            ScenarioContext.get().set("lastAddedProductDesc", productPage.getItemDesc());
            stepEnd("addItemToCart", t, true, "desc="+productPage.getItemDesc());
        } catch(Exception e){ stepFail("addItemToCart", t, e); throw e; }
    }

    @Given("user navigates to cart page")
    public void user_navigates_to_cart_page() {
        long t=stepStart();
        try {
            if (driver == null) {
                driver = BrowserManager.getDriver();
            }
            UiNavigator.openCart(driver);
            cartPage = new CartPage(driver);
            stepEnd("navigateCart", t, true, "url="+driver.getCurrentUrl());
        } catch(Exception e){ stepFail("navigateCart", t, e); throw e; }
    }

    @Then("cart should have at least one item")
    public void cart_should_have_at_least_one_item() {
        long t=stepStart();
        try { Assert.assertTrue(cartPage.getItemsCount() > 0); stepEnd("verifyCartNotEmpty", t, true, "count="+cartPage.getItemsCount()); }
        catch(Exception e){ stepFail("verifyCartNotEmpty", t, e); throw e; }
    }

    @Then("cart should contain same product added")
    public void cartShouldContainSameProductAdded() {
        long t=stepStart();
        try {
            String desc = ScenarioContext.get().get("lastAddedProductDesc");
            if (desc == null && productPage != null) desc = productPage.getItemDesc();
            Assert.assertTrue(cartPage.verifyAddedProduct(desc));
            stepEnd("verifyCartContainsProduct", t, true, "desc="+desc);
        } catch(Exception e){ stepFail("verifyCartContainsProduct", t, e); throw e; }
    }

    @And("user removes the added item from cart")
    public void userRemovesTheAddedItemFromCart() {
        long t=stepStart();
        try {
            String desc = ScenarioContext.get().get("lastAddedProductDesc");
            if (desc == null && productPage != null) desc = productPage.getItemDesc();
            Assert.assertTrue(cartPage.removeAddedProduct(desc));
            stepEnd("removeItemFromCart", t, true, "desc="+desc);
        } catch(Exception e){ stepFail("removeItemFromCart", t, e); throw e; }
    }

    @Then("verify item removal")
    public void verifyItemRemoval() {
        long t=stepStart();
        try {
            String desc = ScenarioContext.get().get("lastAddedProductDesc");
            if (desc == null && productPage != null) desc = productPage.getItemDesc();
            Assert.assertTrue(cartPage.verifyItemRemoval(desc));
            stepEnd("verifyItemRemoval", t, true, "desc="+desc);
        } catch(Exception e){ stepFail("verifyItemRemoval", t, e); throw e; }
    }

    @Then("user calculates total cart amount")
    public void userCalculatesTotalCartAmount() {
        long t=stepStart();
        try { cartTotal = cartPage.getCartTotalAmount(); ScenarioContext.get().set("cartTotal", cartTotal); stepEnd("calcCartTotal", t, true, "total="+cartTotal); }
        catch(Exception e){ stepFail("calcCartTotal", t, e); throw e; }
    }

    @Then("user clicks proceed to checkout")
    public void userClicksProceedToCheckout() {
        long t=stepStart();
        try { checkoutPage = cartPage.proceedToCheckout(); Assert.assertNotNull(checkoutPage); stepEnd("proceedCheckout", t, true, "ok"); }
        catch(Exception e){ stepFail("proceedCheckout", t, e); throw e; }
    }

    @Then("user verifies that totalAmount on checkout page matches with cart total amount")
    public void userVerifiesCheckoutTotalMatchesCartTotal() {
        long t=stepStart();
        try {
            checkoutTotal = checkoutPage.getCheckoutTotalAmount();
            ScenarioContext.get().set("checkoutTotal", checkoutTotal);
            double tolerance = 0.01;
            Assert.assertTrue(Math.abs(cartTotal - checkoutTotal) < tolerance,
                String.format("Mismatch cartTotal=%.2f checkoutTotal=%.2f", cartTotal, checkoutTotal));
            stepEnd("verifyTotalsMatch", t, true, String.format("cart=%.2f checkout=%.2f", cartTotal, checkoutTotal));
        } catch(Exception e){ stepFail("verifyTotalsMatch", t, e); throw e; }
    }

    @And("user clicks on place order button")
    public void userClicksOnPlaceOrderButton() {
        long t=stepStart();
        try { paymentPage = checkoutPage.placeOrder(); Assert.assertNotNull(paymentPage); stepEnd("placeOrder", t, true, "ok"); }
        catch(Exception e){ stepFail("placeOrder", t, e); throw e; }
    }

    @Then("user is redirected to payments page")
    public void userIsRedirectedToPaymentsPage() {
        long t=stepStart();
        try { Assert.assertTrue(paymentPage.isAt()); stepEnd("verifyOnPaymentPage", t, true, "atPayment"); }
        catch(Exception e){ stepFail("verifyOnPaymentPage", t, e); throw e; }
    }

    @When("user enters payment details with {string}, {string}, {string}, {string}, {string}")
    public void userEntersPaymentDetails(String name, String cardNumber, String cvc, String expMonth, String expYear) {
        long t=stepStart();
        try {
            PaymentDetails pd = new PaymentDetails(name, cardNumber, cvc, expMonth, expYear);
            ScenarioContext.get().set("paymentDetails", pd);
            paymentPage.fillPaymentDetails(pd);
            stepEnd("enterPaymentDetailsInline", t, true, "name="+name);
        } catch(Exception e){ stepFail("enterPaymentDetailsInline", t, e); throw e; }
    }

    @When("user enters payment details for {string}")
    public void userEntersPaymentDetailsForProfile(String profile) {
        long t=stepStart();
        try {
            PaymentDetails pd = "default".equalsIgnoreCase(profile) ? TestDataManager.getDefaultPayment() :
                    TestDataManager.getPaymentByCardholderName(profile)
                            .orElseThrow(() -> new RuntimeException("Payment profile not found: " + profile));
            ScenarioContext.get().set("paymentDetails", pd);
            paymentPage.fillPaymentDetails(pd);
            stepEnd("enterPaymentDetailsProfile", t, true, "profile="+profile);
        } catch(Exception e){ stepFail("enterPaymentDetailsProfile", t, e); throw e; }
    }

    @And("user clicks on pay and confirm order button")
    public void userClicksOnPayAndConfirmOrderButton() {
        long t=stepStart();
        try { paymentPage.payAndConfirm(); stepEnd("payAndConfirm", t, true, "clicked"); }
        catch(Exception e){ stepFail("payAndConfirm", t, e); throw e; }
    }

    @Then("user should see success {string}")
    public void userShouldSeeSuccessMessage(String expectedMessage) {
        long t=stepStart();
        try { Assert.assertTrue(paymentPage.verifySuccessMessage(expectedMessage)); stepEnd("verifyPaymentSuccess", t, true, "expected="+expectedMessage); }
        catch(Exception e){ stepFail("verifyPaymentSuccess", t, e); throw e; }
    }

    @And("user is navigated to order confirmation page with {string} and a {string}")
    public void userIsNavigatedToOrderConfirmationPage(String heading, String newMessage) {
        long t=stepStart();
        try { Assert.assertTrue(paymentPage.verifyOrderConfirmation(heading, newMessage)); stepEnd("verifyOrderConfirmation", t, true, "heading="+heading); }
        catch(Exception e){ stepFail("verifyOrderConfirmation", t, e); throw e; }
    }

    @Then("user can download the invoice for the order and verify user and amount.")
    public void userCanDownloadInvoiceAndVerifyUserAndAmount() {
        long t=stepStart();
        try { Assert.assertTrue(paymentPage.downloadInvoiceAndVerify()); stepEnd("downloadInvoice", t, true, "clicked"); }
        catch(Exception e){ stepFail("downloadInvoice", t, e); throw e; }
    }

    @And("user clicks on continue button to go to home page")
    public void userClicksOnContinueButtonToGoToHomePage() {
        long t=stepStart();
        try { paymentPage.continueToHome(); stepEnd("continueToHome", t, true, "homeUrl="+BrowserManager.getDriver().getCurrentUrl()); }
        catch(Exception e){ stepFail("continueToHome", t, e); throw e; }
    }
}
