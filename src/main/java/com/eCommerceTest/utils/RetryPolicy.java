package com.eCommerceTest.utils;

/**
 * Unified retry policies (enum). Provides FIXED and EXPONENTIAL strategies via
 * builder.
 */
public enum RetryPolicy {
    FIXED {
        @Override
        public boolean shouldRetry(io.restassured.response.Response response, int attemptNumber) {
            return internalShouldRetry(response, attemptNumber, maxAttempts, statusCodes);
        }

        @Override
        public long nextDelayMillis(int attemptNumber) {
            return fixedDelayMillis;
        }
    },
    EXPONENTIAL {
        @Override
        public boolean shouldRetry(io.restassured.response.Response response, int attemptNumber) {
            return internalShouldRetry(response, attemptNumber, maxAttempts, statusCodes);
        }

        @Override
        public long nextDelayMillis(int attemptNumber) {
            long base = (long) (baseDelayMillis * Math.pow(2, Math.max(0, attemptNumber - 1)));
            long capped = Math.min(base, maxDelayMillis);
            if (jitter) {
                long half = capped / 2L;
                return half + java.util.concurrent.ThreadLocalRandom.current().nextLong(Math.max(1, half));
            }
            return capped;
        }
    };

    protected int maxAttempts;
    protected long fixedDelayMillis;
    protected long baseDelayMillis;
    protected long maxDelayMillis;
    protected boolean jitter;
    protected java.util.Set<Integer> statusCodes;

    public abstract boolean shouldRetry(io.restassured.response.Response response, int attemptNumber);

    public abstract long nextDelayMillis(int attemptNumber);

    public int maxAttempts() {
        return maxAttempts;
    }

    private static boolean internalShouldRetry(io.restassured.response.Response response, int attemptNumber,
            int maxAttempts, java.util.Set<Integer> codes) {
        if (attemptNumber >= maxAttempts)
            return false;
        if (response == null)
            return true;
        return codes.contains(response.getStatusCode());
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private int maxAttempts = 3;
        private long fixedDelayMillis = 300;
        private long baseDelayMillis = 300;
        private long maxDelayMillis = 4000;
        private boolean jitter = true;
        private java.util.Set<Integer> codes = java.util.Set.of(429, 500, 502, 503, 504);
        private RetryPolicy type = RetryPolicy.FIXED;

        public Builder attempts(int v) {
            this.maxAttempts = Math.max(1, v);
            return this;
        }

        public Builder fixedDelay(long ms) {
            this.fixedDelayMillis = Math.max(0, ms);
            return this;
        }

        public Builder baseDelay(long ms) {
            this.baseDelayMillis = Math.max(1, ms);
            return this;
        }

        public Builder maxDelay(long ms) {
            this.maxDelayMillis = Math.max(baseDelayMillis, ms);
            return this;
        }

        public Builder jitter(boolean enabled) {
            this.jitter = enabled;
            return this;
        }

        public Builder codes(java.util.Set<Integer> status) {
            this.codes = status;
            return this;
        }

        public Builder exponential() {
            this.type = RetryPolicy.EXPONENTIAL;
            return this;
        }

        public Builder fixed() {
            this.type = RetryPolicy.FIXED;
            return this;
        }

        public RetryPolicy build() {
            type.maxAttempts = maxAttempts;
            type.fixedDelayMillis = fixedDelayMillis;
            type.baseDelayMillis = baseDelayMillis;
            type.maxDelayMillis = maxDelayMillis;
            type.jitter = jitter;
            type.statusCodes = codes;
            return type;
        }
    }
}
