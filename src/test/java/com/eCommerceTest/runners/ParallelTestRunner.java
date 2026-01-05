package com.eCommerceTest.runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.DataProvider;

@CucumberOptions(
        features = "src/test/resources/features",
        glue = {"com.eCommerceTest.stepDefinitions"},
        plugin = {
                "pretty",
                "html:target/cucumber-reports/parallel.html",
                "json:target/cucumber-reports/parallel.json"
        },
        monochrome = true,
        tags = "@ui"
)
public class ParallelTestRunner extends AbstractTestNGCucumberTests {
    @Override
    @DataProvider(parallel = true)
    public Object[][] scenarios() {
        // Thread count is controlled by TestNG's data-provider-thread-count (e.g., -Ddataproviderthreadcount=2)
        return super.scenarios();
    }
}
