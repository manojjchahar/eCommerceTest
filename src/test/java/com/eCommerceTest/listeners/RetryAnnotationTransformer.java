package com.eCommerceTest.listeners;

import org.testng.IAnnotationTransformer;
import org.testng.IRetryAnalyzer;
import org.testng.annotations.ITestAnnotation;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * Automatically attaches {@link RetryAnalyzer} to every TestNG @Test method
 * unless it already has a retry analyzer defined. This keeps the codebase clean
 * and allows controlling retries globally via -Dretry.count.
 */
public class RetryAnnotationTransformer implements IAnnotationTransformer {

    @Override
    public void transform(ITestAnnotation annotation, Class testClass, Constructor testConstructor, Method testMethod) {
        Class<? extends IRetryAnalyzer> current = annotation.getRetryAnalyzerClass();
        if (current == null || current == IRetryAnalyzer.class) {
            annotation.setRetryAnalyzer(RetryAnalyzer.class);
        }
    }
}
