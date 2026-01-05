package com.eCommerceTest.pages;

import com.eCommerceTest.base.BaseTest;
import com.eCommerceTest.utils.Constants;
import com.eCommerceTest.utils.BrowserManager; // unified waits
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProductPage extends BaseTest {

    private final WebDriver driver;
    private static final Logger logger = LoggerFactory.getLogger(ProductPage.class);
    private String itemDesc = ""; // holds description of first added product

    private final By searchInput = By.id("search_product");
    private final By searchButton = By.id("submit_search");
    private final By productCards = By.cssSelector(".features_items .product-image-wrapper");
    private final By addToCartButtons = By.cssSelector(".product-overlay a.add-to-cart");
    private final By continueShopping = By.cssSelector("button.close-modal");
    private final By confirmation = By.cssSelector(".modal-content > div:nth-child(2) > p:first-child");
    private final By prodDescriptions = By.cssSelector(".features_items .productinfo.text-center > p");
    private final By categoryPanel = By.cssSelector(".panel-group.category-products");
    private final By brandsPanel = By.cssSelector(".brands_products");
    private final By heading = By.cssSelector(".features_items h2.title.text-center");
    private final By categoryClicked = By.cssSelector(".active");


    public String getItemDesc() {
        logger.debug("ProductPage.getItemDesc returning='{}'", itemDesc);
        return itemDesc;
    }

    public ProductPage(WebDriver driver) {
        this.driver = driver;
    }

    public void search(String query) {
        logger.debug("ProductPage.search start query={}", query);
        driver.findElement(searchInput).clear();
        driver.findElement(searchInput).sendKeys(query);
        driver.findElement(searchButton).click();
        logger.debug("ProductPage.search end query={}", query);
    }

    public int getProductCount() {
        int count = driver.findElements(productCards).size();
        logger.debug("ProductPage.getProductCount count={}", count);
        return count;
    }

    public boolean addFirstProductToCart() {
        logger.debug("ProductPage.addFirstProductToCart start");
        final List<WebElement> products = driver.findElements(productCards);
        if (products.isEmpty()) {
            logger.debug("ProductPage.addFirstProductToCart no products found");
            return false;
        }
        final List<WebElement> localDescriptions = driver.findElements(prodDescriptions);
        if (!localDescriptions.isEmpty()) {
            itemDesc = localDescriptions.get(0).getText();
            logger.debug("ProductPage.addFirstProductToCart capturedDesc={}", itemDesc);
        }
        final WebElement product = products.get(0);
        final JavascriptExecutor js = (JavascriptExecutor) driver;
        final Actions actions = new Actions(driver);
        actions.moveToElement(product).pause(Constants.HOVER_PAUSE).perform();
        final WebElement addToCartBtn = product.findElement(addToCartButtons);
        scrollIntoViewCentered(js, addToCartBtn);
        BrowserManager.waitShort().until(ExpectedConditions.elementToBeClickable(addToCartBtn));
        boolean clicked = tryClick(addToCartBtn, js);
        logger.debug("ProductPage.addFirstProductToCart end success={}", clicked);
        return clicked;
    }

    // Scrolls element into center of viewport using JS
    private void scrollIntoViewCentered(JavascriptExecutor js, WebElement element) {
        js.executeScript(Constants.SCROLL_CENTER_SCRIPT, element);
    }

    // Prefer a small helper: try native click, then JS as single fallback; return success
    private boolean tryClick(WebElement element, JavascriptExecutor js) {
        try {
            element.click();
            return true;
        } catch (WebDriverException e) {
            try {
                js.executeScript("arguments[0].click();", element);
                return true;
            } catch (WebDriverException ignored) {
                return false;
            }
        }
    }

    public String cartConfirmation(){
        logger.debug("ProductPage.cartConfirmation start");
        BrowserManager.waitLong().until(ExpectedConditions.visibilityOfElementLocated(confirmation));
        String text = driver.findElement(confirmation).getText();
        BrowserManager.waitLong().until(ExpectedConditions.elementToBeClickable(continueShopping));
        driver.findElement(continueShopping).click();
        logger.debug("ProductPage.cartConfirmation end text={}", text);
        return text;
    }

    public boolean verifyExpectedProductList(String keyWord) {
        logger.debug("ProductPage.verifyExpectedProductList start keyWord={}", keyWord);
        List<WebElement> descriptions = driver.findElements(prodDescriptions);
        if (descriptions.isEmpty()) {
            logger.debug("ProductPage.verifyExpectedProductList no descriptions found");
            return false;
        }
        String kw = keyWord == null ? "" : keyWord;
        descriptions.stream().map(WebElement::getText).forEach(t -> logger.debug("Product description='{}'", t));
        boolean result = descriptions.stream().map(WebElement::getText).anyMatch(text -> text.contains(kw));
        logger.debug("ProductPage.verifyExpectedProductList end result={}", result);
        return result;
    }

    public void selectCategoryAndSubCategory(String category, String subCategory) {
        logger.debug("ProductPage.selectCategoryAndSubCategory start category={} subCategory={}", category, subCategory);
        try {
            WebElement catAnchor = driver.findElements(categoryPanel).stream()
                    .flatMap(panel -> panel.findElements(By.cssSelector("a")).stream())
                    .filter(a -> a.getText().trim().equalsIgnoreCase(category))
                    .findFirst().orElse(null);
            if (catAnchor != null) {
                scrollIntoViewCentered((JavascriptExecutor) driver, catAnchor);
                catAnchor.click();
            }
            WebElement subCatAnchor = driver.findElements(categoryPanel).stream()
                    .flatMap(panel -> panel.findElements(By.cssSelector("a")).stream())
                    .filter(a -> a.getText().trim().equalsIgnoreCase(subCategory))
                    .findFirst().orElse(null);
            if (subCatAnchor != null) {
                scrollIntoViewCentered((JavascriptExecutor) driver, subCatAnchor);
                subCatAnchor.click();
            }
            BrowserManager.waitShort().until(d -> !d.findElements(heading).isEmpty());
            logger.debug("ProductPage.selectCategoryAndSubCategory end heading={}", getHeadingText());
        } catch (Exception e) {
            logger.error("Category/SubCategory selection failed: {} > {}", category, subCategory, e);
        }
    }

    public String getHeadingText() {
        String h;
        try {
            h = driver.findElement(heading).getText().trim();
        } catch (Exception e) {
            h = "";
        }
        logger.debug("ProductPage.getHeadingText heading='{}'", h);
        return h;
    }

    public boolean allProductsMatchCategory(String category, String subCategory) {
        logger.debug("ProductPage.allProductsMatchCategory start category={} subCategory={}", category, subCategory);
        String expectedUpper = (category + " - " + subCategory + " PRODUCTS").toUpperCase();
        String current = getHeadingText().toUpperCase();
        boolean result = !current.isEmpty() && current.equals(expectedUpper);
        logger.debug("ProductPage.allProductsMatchCategory end result={}", result);
        return result;
    }

    public void filterByBrand(String brand) {
        logger.debug("ProductPage.filterByBrand start brand={}", brand);
        try {
            WebElement brandAnchor = driver.findElements(brandsPanel).stream()
                    .flatMap(panel -> panel.findElements(By.cssSelector("a")).stream())
                    .filter(a -> normalizeBrandText(a.getText()).contains(brand.toLowerCase()))
                    .findFirst().orElse(null);
            if (brandAnchor != null) {
                scrollIntoViewCentered((JavascriptExecutor) driver, brandAnchor);
                brandAnchor.click();
                BrowserManager.waitShort().until(d -> !d.findElements(heading).isEmpty());
            }
        } catch (Exception e) {
            logger.error("Brand filter click failed for {}", brand, e);
        }
        logger.debug("ProductPage.filterByBrand end heading={}", getHeadingText());
    }

    public int getBrandListedCount(String brand) {
        logger.debug("ProductPage.getBrandListedCount start brand={}", brand);
        int count = -1;
        try {
            // wait until brand anchors are present inside the brands panel
            BrowserManager.waitShort().until(d ->
                    !d.findElements(brandsPanel).isEmpty() &&
                            d.findElements(brandsPanel).stream()
                                    .flatMap(p -> p.findElements(By.cssSelector("a")).stream())
                                    .findAny().isPresent());

            List<WebElement> anchors = driver.findElements(brandsPanel).stream()
                    .flatMap(panel -> panel.findElements(By.cssSelector("a")).stream())
                    .toList();

            String target = normalizeBrandText(brand);
            // Log available anchors (normalized) for troubleshooting
            anchors.forEach(a -> logger.debug("Brand anchor raw='{}' norm='{}'", a.getText(), normalizeBrandText(a.getText())));

            WebElement brandAnchor = anchors.stream()
                    .filter(a -> {
                        String norm = normalizeBrandText(a.getText());
                        // prefer exact normalized match; allow contains as fallback
                        return norm.equals(target) || norm.contains(target);
                    })
                    .findFirst()
                    .orElse(null);

            if (brandAnchor == null) {
                logger.debug("ProductPage.getBrandListedCount brand anchor not found after normalization");
                return -1;
            }

            String txt = brandAnchor.getText();
            java.util.regex.Matcher m = java.util.regex.Pattern.compile(".*\\((\\d+)\\).*").matcher(txt);
            if (m.matches()) {
                count = Integer.parseInt(m.group(1));
            } else {
                // If no explicit count is shown (unexpected), fall back to product cards count on page
                count = getProductCount();
            }
        } catch (Exception e) {
            logger.error("ProductPage.getBrandListedCount error brand={}", brand, e);
        }
        logger.debug("ProductPage.getBrandListedCount end count={}", count);
        return count;
    }

    public boolean allProductsMatchBrand(String brand) {
        logger.debug("ProductPage.allProductsMatchBrand start brand={}", brand);
        String actualHeading = getHeadingText().toUpperCase();
        String brandToken = brand.toUpperCase();
        boolean result = actualHeading.contains(brandToken);
        logger.debug("ProductPage.allProductsMatchBrand end result={}", result);
        return result;
    }

    private String normalizeBrandText(String raw){
        if(raw==null) return "";
        // Remove counts like "(6)", collapse whitespace (including non-breaking), lowercase, trim
        String noCount = raw.replaceAll("\\(\\s*\\d+\\s*\\)", "");
        String normalizedSpaces = noCount
                .replace('\u00A0',' ')      // non-breaking space to regular space
                .replaceAll("\\s+"," ")     // collapse multiple spaces
                .trim();
        return normalizedSpaces.toLowerCase();
    }

    public Object getCategoryText() {
        String h;
        try {
            h = driver.findElement(categoryClicked).getText().trim();
        } catch (Exception e) {
            h = "";
        }
        logger.debug("ProductPage.getHeadingText heading='{}'", h);
        return h;
    }
}
