package com.eCommerceTest.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

/**
 * Simple smoke test to verify SLF4J + Logback configuration.
 * Logs one line per level; should appear colored in console and stored in logs/framework.log.
 */
public class LoggingSmokeTest {
    private static final Logger log = LoggerFactory.getLogger(LoggingSmokeTest.class);

    @Test
    public void verifyLoggingPipeline() {
        log.trace("TRACE level active test message");
        log.debug("DEBUG level active test message");
        log.info("INFO level active test message");
        log.warn("WARN level active test message");
        log.error("ERROR level active test message");
    }
}

