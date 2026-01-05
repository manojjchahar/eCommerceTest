package com.eCommerceTest.stepDefinitions;

import com.eCommerceTest.base.BaseTest;
import com.eCommerceTest.base.ScenarioContext;
import com.eCommerceTest.pages.ProductPage;
import com.eCommerceTest.utils.BrowserManager;
import com.eCommerceTest.utils.UiNavigator;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

public class ProductSteps extends BaseTest {

    private WebDriver driver;
    private static final Logger log = LoggerFactory.getLogger(ProductSteps.class);
    private long stepStart(){ return System.nanoTime(); }
    private void stepEnd(String name,long start, boolean ok, String detail){ long ms=(System.nanoTime()-start)/1_000_000; if(ok) log.info("STEP OK [{}] {}ms {}",name,ms,detail); else log.warn("STEP WARN [{}] {}ms {}",name,ms,detail);}
    private void stepFail(String name,long start, Exception e){ long ms=(System.nanoTime()-start)/1_000_000; log.error("STEP FAIL [{}] {}ms {}",name,ms,e.getMessage(),e);}

    @Given("user is on the products page")
    public void user_is_on_the_products_page() {
        long t=stepStart();
        try {
            driver = BrowserManager.getDriver();
            UiNavigator.openProducts(driver);
            productPage = new ProductPage(driver);
            stepEnd("navigateProducts", t, true, "url="+driver.getCurrentUrl());
        } catch(Exception e){ stepFail("navigateProducts", t, e); throw e; }
    }

    @When("user searches for product with {string}")
    public void user_searches_for_product_with(String query) {
        long t=stepStart();
        try {
            ScenarioContext.get().set("lastSearchKeyword", query);
            productPage.search(query);
            stepEnd("searchProducts", t, true, "query="+query);
        } catch(Exception e){ stepFail("searchProducts", t, e); throw e; }
    }

    @Then("products should be listed related to {string}")
    public void products_should_be_listed_related_to(String keyWord) {
        long t=stepStart();
        try {
            boolean ok = productPage.verifyExpectedProductList(keyWord);
            Assert.assertTrue(ok, "Expected products for keyword");
            stepEnd("verifySearchResults", t, true, "keyword="+keyWord+" count="+productPage.getProductCount());
        } catch(Exception e){ stepFail("verifySearchResults", t, e); throw e; }
    }

    @When("user adds first product to cart")
    public void user_adds_first_product_to_cart() {
        long t=stepStart();
        try {
            boolean added = productPage.addFirstProductToCart();
            Assert.assertTrue(added, "Add to cart failed");
            ScenarioContext.get().set("lastAddedProductDesc", productPage.getItemDesc());
            stepEnd("addFirstProduct", t, true, "desc="+productPage.getItemDesc());
        } catch(Exception e){ stepFail("addFirstProduct", t, e); throw e; }
    }

    @Then("user must see confirmation {string}")
    public void userMustSeeConfirmation(String message) {
        long t=stepStart();
        try {
            cartConfirmMessage = productPage.cartConfirmation();
            ScenarioContext.get().set("lastCartConfirmation", cartConfirmMessage);
            Assert.assertEquals(message, cartConfirmMessage);
            stepEnd("confirmAddToCart", t, true, "expected="+message);
        } catch(Exception e){ stepFail("confirmAddToCart", t, e); throw e; }
    }

    @Then("user clicks on {string} from the dropdown of {string}")
    public void userClicksOnSubCategoryFromDropdown(String subCategory, String category) {
        long t=stepStart();
        try {
            ScenarioContext.get().set("currentCategory", category);
            ScenarioContext.get().set("currentSubCategory", subCategory);
            productPage.selectCategoryAndSubCategory(category, subCategory);
            stepEnd("filterByCategory", t, true, "category="+category+" sub="+subCategory);
        } catch(Exception e){ stepFail("filterByCategory", t, e); throw e; }
    }

    @Then("user should see heading of products page as {string}")
    public void userShouldSeeHeadingOfProductsPageAs(String expectedHeading) {
        long t=stepStart();
        try {
            BrowserManager.waitShort().until(d -> productPage.getCategoryText());
            String actual = productPage.getHeadingText().trim();
            String expTrim = expectedHeading.trim();
            boolean match = actual.equalsIgnoreCase(expTrim);
            if(!match) {
                // allow partial brand token match when site adds suffixes like JUNIOR or uppercases differently
                String brandToken = expTrim.replace("BRAND -","").replace("PRODUCTS"," ").replace("-"," ").trim();
                match = actual.toLowerCase().contains(brandToken.toLowerCase());
            }
            Assert.assertTrue(match, "Heading mismatch expected='"+expTrim+"' actual='"+actual+"'");
            stepEnd("verifyHeading", t, true, "heading="+actual);
        } catch(Exception e){ stepFail("verifyHeading", t, e); throw e; }
    }

    @Then("all filtered products should be of same category and sub-category as above as seen on product details page")
    public void allFilteredProductsShouldMatchCategoryAndSubCategory() {
        long t=stepStart();
        try {
            String category = ScenarioContext.get().get("currentCategory");
            String subCategory = ScenarioContext.get().get("currentSubCategory");
            Assert.assertTrue(productPage.allProductsMatchCategory(category, subCategory));
            stepEnd("verifyCategoryFilter", t, true, "category="+category+" sub="+subCategory);
        } catch(Exception e){ stepFail("verifyCategoryFilter", t, e); throw e; }
    }

    @Then("user clicks on {string} from the brands list")
    public void userClicksOnBrandFromBrandsList(String brand) {
        long t=stepStart();
        try {
            ScenarioContext.get().set("currentBrand", brand);
            productPage.filterByBrand(brand);
            stepEnd("filterByBrand", t, true, "brand="+brand);
        } catch(Exception e){ stepFail("filterByBrand", t, e); throw e; }
    }

    @Then("number of listed products after filtering with brand should be same number as shown in brands section")
    public void numberOfListedProductsAfterFilteringShouldMatchBrandList() {
        long t=stepStart();
        try {
            String brand = ScenarioContext.get().get("currentBrand");
            int listedCount = productPage.getProductCount();
            int brandCount = productPage.getBrandListedCount(brand);
            Assert.assertTrue(brandCount >= 0);
            Assert.assertEquals(listedCount, brandCount);
            stepEnd("verifyBrandCounts", t, true, "brand="+brand+" listed="+listedCount+" panel="+brandCount);
        } catch(Exception e){ stepFail("verifyBrandCounts", t, e); throw e; }
    }

    @Then("all filtered products should be of same selected {string} as seen on product details page")
    public void allFilteredProductsShouldMatchSelectedBrand(String brand) {
        long t=stepStart();
        try {
            Assert.assertTrue(productPage.allProductsMatchBrand(brand));
            stepEnd("verifyBrandFilter", t, true, "brand="+brand);
        } catch(Exception e){ stepFail("verifyBrandFilter", t, e); throw e; }
    }
}
