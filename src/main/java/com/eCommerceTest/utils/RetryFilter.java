package com.eCommerceTest.utils;

import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;

/**
 * Retry filter applying a RetryPolicy (FIXED or EXPONENTIAL) to Rest-Assured requests.
 */
public class RetryFilter implements Filter {
    private final RetryPolicy policy;
    private static final ThreadLocal<Integer> LAST_ATTEMPTS = ThreadLocal.withInitial(() -> 1);

    public RetryFilter(RetryPolicy policy) { this.policy = policy; }

    @Override
    public Response filter(FilterableRequestSpecification requestSpec,
                           FilterableResponseSpecification responseSpec,
                           FilterContext ctx) {
        int attempt = 1;
        Response response = ctx.next(requestSpec, responseSpec);
        while (policy.shouldRetry(response, attempt)) {
            sleep(policy.nextDelayMillis(attempt));
            attempt++;
            response = ctx.next(requestSpec, responseSpec);
        }
        LAST_ATTEMPTS.set(attempt);
        return response;
    }

    private void sleep(long ms) { try { Thread.sleep(ms); } catch (InterruptedException ignored) { Thread.currentThread().interrupt(); } }

    /** Returns the attempt count (including the successful final attempt) for the last executed request on this thread. */
    public static int getLastAttemptCount() { return LAST_ATTEMPTS.get(); }
}
