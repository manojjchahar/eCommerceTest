package com.eCommerceTest.listeners;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

/**
 * Simple retry analyzer to mitigate flaky tests in CI.
 * Controlled by system property: -Dretry.count=N (default 0)
 */
public class RetryAnalyzer implements IRetryAnalyzer {

    private final int maxRetries;
    private int attempt = 0;

    public RetryAnalyzer() {
        this.maxRetries = parseInt(System.getProperty("retry.count", "0"), 0);
    }

    @Override
    public boolean retry(ITestResult result) {
        if (attempt < maxRetries) {
            attempt++;
            return true;
        }
        return false;
    }

    private static int parseInt(String s, int def) {
        try {
            return Integer.parseInt(s);
        } catch (Exception e) {
            return def;
        }
    }
}
