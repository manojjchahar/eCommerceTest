package com.eCommerceTest.utils;

import org.fusesource.jansi.AnsiConsole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Ensures Jansi is installed early so Logback's ANSI color codes render on Windows consoles.
 * Also provides a convenient way to obtain SLF4J loggers.
 */
public final class Logging {

    static {
        try {
            // Install Jansi to enable ANSI escape sequences on Windows consoles
            AnsiConsole.systemInstall();
        } catch (Throwable t) {
            // If Jansi isn't available or installation fails, skip silently
            // SLF4J will still work without colors
        }
    }

    private Logging() { /* utility */ }

    public static Logger getLogger(Class<?> cls) {
        return LoggerFactory.getLogger(cls);
    }
}

