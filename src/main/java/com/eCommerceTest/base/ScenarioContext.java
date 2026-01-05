package com.eCommerceTest.base;

/**
 * Provides scenario-scoped context using ThreadLocal.
 * Stores a per-thread TestContext instance so parallel runs are isolated.
 */
public final class ScenarioContext {
    private static final ThreadLocal<TestContext> TL = ThreadLocal.withInitial(TestContext::new);

    private ScenarioContext() { }

    public static TestContext get() {
        return TL.get();
    }

    public static void clear() {
        TL.remove();
    }
}
