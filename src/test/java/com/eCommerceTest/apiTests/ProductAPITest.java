package com.eCommerceTest.apiTests;

import com.eCommerceTest.api.ProductAPI;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

public class ProductAPITest {

    private final ProductAPI productAPI = new ProductAPI();

    @Test(description = "Products list should return 200 and basic schema structure")
    public void products_list_should_return_200() {
        Response res = productAPI.productsList();
        Assert.assertEquals(res.statusCode(), 200);
        // Weak schema check placeholder
        res.then().assertThat().body(matchesJsonSchemaInClasspath("schemas/productSchema.json"));
    }

    @Test(description = "Search product should return 200")
    public void search_product_should_return_200() {
        Response res = productAPI.searchProduct("shirt");
        Assert.assertEquals(res.statusCode(), 200);
    }
}
