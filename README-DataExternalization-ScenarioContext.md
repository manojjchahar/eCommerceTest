# Effective utlization of data externalization

This project externalizes environment settings, test inputs, and validation contracts so you can change behavior without recompiling code.

What is externalized and where it lives:
- Environment and endpoints
  - File: src/main/resources/config/config.toml
  - Loader: com.eCommerceTest.utils.ConfigReader
  - Keys under [ui] and [api] drive base URLs and endpoint paths at runtime.
  - Any key can be overridden by system properties in CI/CD (e.g., -Dapi_base_url, -Dui_base_url, -Dbrowser, -Dheadless, -DgridUrl).
- Test data (users, products, payments, registration profiles)
  - Files: src/test/resources/testData/*.json (users.json, products.json, payments.json, registration-data.json)
  - Loaders:
    - DataRepository: cached JSON reader with file timestamp tracking to avoid stale data and repeated IO.
    - TestDataManager: typed POJO accessors (User, Product, PaymentDetails, UserRegistrationData) and safe record selection.
- API response schemas (validation contracts)
  - Files: src/test/resources/schemas/*.json
  - Used with Rest Assured json-schema-validator or SchemaValidator to assert response shape.

How it’s wired into tests:
- Runtime config precedence: system properties > config.toml. This enables environment switching and feature toggles via -D flags without code changes.
- API clients read base URL and endpoints from config (no hardcoded URLs in steps).
- Steps fetch domain data from TestDataManager (e.g., getDefaultUser(), getRegistrationDataByProfile("...")). This removes long step parameter lists and centralizes data.
- Schemas are referenced by path in tests; the JSON files define the contract, not Java code.

Benefits:
- Maintainability: update JSON/TOML to change test behavior.
- Reuse: the same data feeds UI and API tests.
- Safety: POJOs and schemas reduce runtime errors from stringly-typed data.
- CI-friendly: everything is overridable with -D flags.


# Context classes enhancing test framework

Context provides scenario-scoped state and lifecycle control to keep steps clean, deterministic, and parallel-safe.

Core components:
- ScenarioContext (com.eCommerceTest.base.ScenarioContext)
  - ThreadLocal holder that yields a unique TestContext per scenario/thread.
  - API: get() for access; clear() called at scenario end to avoid leaks.
- TestContext (com.eCommerceTest.base.TestContext)
  - Lightweight key-value store plus optional WebDriver slot.
  - Methods: set(key, value), <T> get(key), getAsString/getAsInt, getOrDefault, setDriver/getDriver.
  - Typical keys across steps: lastApiResponse, registeredEmail, registeredPassword, registeredName, registrationPayload, lastSearchKeyword, addedProductDesc, cartTotal.
- Hooks (src/test/java/com/eCommerceTest/stepDefinitions/Hooks.java)
  - Before: captures start time, initializes driver via BrowserManager (using -Dbrowser/-Dheadless/-DgridUrl), opens base URL, stores driver in context.
  - After: on failure attaches a screenshot, quits browser, and clears ScenarioContext.

Why it improves tests:
- Parallel isolation: each scenario’s state is thread-local, so runs don’t interfere.
- Cleaner step code: share intermediate data (e.g., responses, computed totals) via context instead of passing many arguments.
- Centralized lifecycle: driver/timing handled in Hooks so steps focus on behavior.
- Extensibility: add new context keys for advanced flows without changing step signatures.
