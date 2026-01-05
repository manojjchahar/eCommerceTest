package com.eCommerceTest.unit;

import com.eCommerceTest.utils.RetryPolicy;
import org.testng.Assert;
import org.testng.annotations.Test;
import java.util.HashSet;
import java.util.Set;

public class RetryPolicyTest {

    @Test
    public void testExponentialDelayProgressionWithoutJitter() {
        RetryPolicy policy = RetryPolicy.builder()
                .exponential()
                .attempts(5)
                .baseDelay(100)
                .maxDelay(800)
                .jitter(false)
                .build();
        long d1 = policy.nextDelayMillis(1);
        long d2 = policy.nextDelayMillis(2);
        long d3 = policy.nextDelayMillis(3);
        long d4 = policy.nextDelayMillis(4);
        long d5 = policy.nextDelayMillis(5);
        Assert.assertEquals(d1, 100L, "Attempt1 delay");
        Assert.assertEquals(d2, 200L, "Attempt2 delay");
        Assert.assertEquals(d3, 400L, "Attempt3 delay");
        Assert.assertEquals(d4, 800L, "Attempt4 delay (capped)");
        Assert.assertEquals(d5, 800L, "Attempt5 delay remains capped");
    }

    @Test
    public void testFixedDelayIsConstant() {
        RetryPolicy policy = RetryPolicy.builder()
                .fixed()
                .attempts(4)
                .fixedDelay(250)
                .build();
        for (int i=1;i<=4;i++) {
            Assert.assertEquals(policy.nextDelayMillis(i), 250L, "Fixed delay mismatch at attempt="+i);
        }
    }

    @Test
    public void testExponentialDelayWithJitterWithinBounds() {
        RetryPolicy policy = RetryPolicy.builder()
                .exponential()
                .attempts(5)
                .baseDelay(100)
                .maxDelay(800)
                .jitter(true)
                .build();
        // Collect multiple samples for attempt 3 to ensure variability and bounds
        Set<Long> samples = new HashSet<>();
        for(int i=0;i<10;i++) {
            samples.add(policy.nextDelayMillis(3));
        }
        // All samples should be within half to capped (attempt3 base 400 capped 800 -> half=200)
        samples.forEach(v -> {
            Assert.assertTrue(v >= 200 && v <= 800, "Jitter sample out of bounds: "+v);
        });
        // Expect some variability (not all identical)
        Assert.assertTrue(samples.size() > 1, "Expected jitter to produce variable delays");
    }
}
