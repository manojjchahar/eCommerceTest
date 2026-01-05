package com.ecommercefull.utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

public final class ExtentReportManager {

    private static ExtentReports extent;
    private static final ThreadLocal<ExtentTest> CURRENT = new ThreadLocal<>();

    private ExtentReportManager() {}

    private static synchronized ExtentReports getExtent() {
        if (extent == null) {
            ExtentSparkReporter spark = new ExtentSparkReporter("target/extent/ExtentReport.html");
            extent = new ExtentReports();
            extent.attachReporter(spark);
        }
        return extent;
    }

    public static void startTest(String name) {
        CURRENT.set(getExtent().createTest(name));
    }

    public static ExtentTest getTest() {
        return CURRENT.get();
    }

    public static void flush() {
        try {
            if (extent != null) {
                extent.flush();
            }
        } finally {
            CURRENT.remove();
        }
    }
}
