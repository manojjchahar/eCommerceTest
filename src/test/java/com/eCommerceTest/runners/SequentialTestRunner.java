package com.eCommerceTest.runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

@CucumberOptions(features = "src/test/resources/features", glue = { "com.eCommerceTest.stepDefinitions" }, plugin = {
                "pretty",
                "html:target/cucumber-reports/sequential.html",
                "json:target/cucumber-reports/sequential.json",
                "com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter:",
                "io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm"
}, monochrome = true, tags = "@UI" // override via -Dcucumber.filter.tags
)
public class SequentialTestRunner extends AbstractTestNGCucumberTests {
        @Override
        @DataProvider(parallel = false)
        public Object[][] scenarios() {
                return super.scenarios();
        }

        @Override
        @Test(groups = "cucumber", description = "Runs Cucumber Scenarios", dataProvider = "scenarios")
        public void runScenario(io.cucumber.testng.PickleWrapper pickleWrapper,
                        io.cucumber.testng.FeatureWrapper featureWrapper) {
                super.runScenario(pickleWrapper, featureWrapper);
        }
}
