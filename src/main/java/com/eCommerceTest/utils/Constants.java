package com.eCommerceTest.utils;

import java.time.Duration;

public final class Constants {

    // Helper constants to avoid magic numbers/strings and to centralize behavior
    public static final Duration HOVER_PAUSE = Duration.ofMillis(300);
    public static final String SCROLL_CENTER_SCRIPT = "arguments[0].scrollIntoView({block: 'center'});";

    // Context keys
    public static final String CTX_DUPLICATE = "signupDuplicate";
    public static final String CTX_DUPLICATE_MSG = "duplicateMessage";
    public static final String CTX_ABORT = "abortDueToDuplicate";
    public static final String CTX_LAST_API_RESPONSE = "lastApiResponse";

    // HTTP headers
    public static final String HDR_AUTHORIZATION = "Authorization";

    // Content types
    public static final String CT_FORM_URLENCODED = "application/x-www-form-urlencoded";
    public static final String CT_JSON = "application/json";

    // System property keys
    public static final String PROP_API_BASE_URL = "api.base.url";
    public static final String PROP_API_TOKEN = "api.token";
    public static final String PROP_BROWSER = "browser";
    public static final String PROP_HEADLESS = "headless";
    public static final String PROP_GRID_URL = "gridUrl";
    public static final String PROP_WAIT_SHORT = "wait.short.timeout";
    public static final String PROP_WAIT_LONG = "wait.long.timeout";
    public static final String PROP_WAIT_POLLING = "wait.polling.millis";
    public static final String PROP_RETRY_COUNT = "retry.count";

    // Default values
    public static final String DEFAULT_API_BASE_URL = "https://automationexercise.com";
    public static final String DEFAULT_BROWSER = "chrome";
    public static final String DEFAULT_HEADLESS = "true";
    public static final String DEFAULT_GRID_URL = "";
    public static final String DEFAULT_WAIT_SHORT = "20";
    public static final String DEFAULT_WAIT_LONG = "30";
    public static final String DEFAULT_WAIT_POLLING = "750";

    // Resource roots
    public static final String RES_TESTDATA_ROOT = "/testData/";
    public static final String RES_SCHEMAS_ROOT = "/schemas/";

    // Common Cucumber tags
    public static final String TAG_UI = "@ui";
    public static final String TAG_API = "@api";
    public static final String TAG_SMOKE = "@smoke";
    public static final String TAG_REGRESSION = "@regression";

    private Constants() { }
}
