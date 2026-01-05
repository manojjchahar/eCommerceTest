package com.eCommerceTest.stepDefinitions;

import com.eCommerceTest.base.BaseAPI;
import com.eCommerceTest.base.ScenarioContext;
import com.eCommerceTest.api.AuthAPI;
import com.eCommerceTest.api.ProductAPI;
import com.eCommerceTest.api.AccountAPI;
import com.eCommerceTest.utils.RetryFilter;
import com.eCommerceTest.utils.TestDataManager;
import com.eCommerceTest.models.UserRegistrationData;
import com.eCommerceTest.utils.Constants;

import io.cucumber.java.en.*;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.testng.Assert;

import java.util.Arrays;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

public class APISteps extends BaseAPI {

    // API clients and response holder
    private final AuthAPI authAPI = new AuthAPI();
    private final ProductAPI productAPI = new ProductAPI();
    private final AccountAPI accountAPI = new AccountAPI();
    private Response response;
    private long startTime;
    private long endTime;

    @Given("products exist in the catalog")
    public void products_exist_in_the_catalog() {
        Response res = productAPI.productsList();
        ScenarioContext.get().set("lastApiResponse", res);
        Assert.assertEquals(res.statusCode(), 200);
        // Optional schema validation for products list
        if (Boolean.parseBoolean(System.getProperty("api.schema.validate", "false"))) {
            res.then().body(matchesJsonSchemaInClasspath("schemas/productSchema.json"));
        }
    }

    // Products
    @When("I request the products list")
    public void i_request_products_list() {
        response = productAPI.productsList();
        ScenarioContext.get().set("lastApiResponse", response);
    }

    @When("I search products by keyword {string}")
    public void i_search_products_by_keyword(String keyword) {
        ScenarioContext.get().set("lastSearchKeyword", keyword);
        response = productAPI.searchProduct(keyword);
        ScenarioContext.get().set("lastApiResponse", response);
    }

    @Then("search results should return status {int}")
    public void search_results_should_return_status(Integer code) {
        Assert.assertEquals(response.statusCode(), code.intValue());
    }

    // Auth
    @When("I login via API with email {string} and password {string}")
    public void i_login_via_api(String email, String password) {
        response = authAPI.login(email, password);
        ScenarioContext.get().set("lastApiResponse", response);
        ScenarioContext.get().set("lastLoginEmail", email);
        ScenarioContext.get().set("lastLoginPassword", password);
    }

    @Then("API response code should be {int}")
    public void api_response_code_should_be(Integer code) {
        Assert.assertEquals(response.statusCode(), code.intValue());
    }

    // Account fetch
    @When("I fetch user by email {string}")
    public void i_fetch_user_by_email(String email) {
        response = accountAPI.getUserByEmail(email);
        ScenarioContext.get().set("lastApiResponse", response);
        ScenarioContext.get().set("lastFetchedEmail", email);
    }

    @When("I fetch user by the registered email")
    public void i_fetch_user_by_registered_email() {
        String email = ScenarioContext.get().get("registeredEmail");
        response = accountAPI.getUserByEmail(email);
        ScenarioContext.get().set("lastApiResponse", response);
    }

    // Brands
    @When("I request the brands list")
    public void i_request_the_brands_list() {
        response = productAPI.brandsList();
        ScenarioContext.get().set("lastApiResponse", response);
    }

    // Registration lifecycle (data-driven)
    @When("I register a new user using test data file {string} with record {string}")
    public void i_register_new_user_using_test_data(String file, String profileKey) {
        Map<String, Object> selected = TestDataManager.selectRecord(file, profileKey);
        Map<String, Object> payload = new HashMap<>(selected);

        // ensure unique email for idempotency
        String email = String.valueOf(payload.getOrDefault("email", "test@example.com"));
        if (email.contains("@")) {
            String[] parts = email.split("@", 2);
            String uniqueLocal = parts[0] + "+" + java.util.UUID.randomUUID().toString().substring(0, 8);
            email = uniqueLocal + "@" + parts[1];
        } else {
            email = "test_" + java.util.UUID.randomUUID().toString().substring(0, 8) + "@example.com";
        }
        payload.put("email", email);

        // typed registration
        UserRegistrationData regData = UserRegistrationData.fromMap(payload);
        response = authAPI.register(regData);
        ScenarioContext.get().set("lastApiResponse", response);

        // store for later steps (both generic and profile-scoped)
        ScenarioContext.get().set("registeredEmail:" + profileKey, email);
        ScenarioContext.get().set("registeredPassword:" + profileKey,
                String.valueOf(payload.getOrDefault("password", "Pass123!")));
        ScenarioContext.get().set("registeredName:" + profileKey, String.valueOf(payload.getOrDefault("name", "User")));
        ScenarioContext.get().set("registeredEmail", email);
        ScenarioContext.get().set("registeredPassword", String.valueOf(payload.getOrDefault("password", "Pass123!")));
        ScenarioContext.get().set("registeredName", String.valueOf(payload.getOrDefault("name", "User")));
        ScenarioContext.get().set("registrationPayload", payload);
    }

    @When("I attempt duplicate registration using test data file {string} with record {string}")
    public void i_attempt_duplicate_registration_using_test_data(String file, String profileKey) {
        String email = ScenarioContext.get().get("registeredEmail:" + profileKey);
        String password = ScenarioContext.get().get("registeredPassword:" + profileKey);
        String name = ScenarioContext.get().get("registeredName:" + profileKey);

        if (email == null) {
            // Perform original registration if not present (first pass scenario)
            i_register_new_user_using_test_data(file, profileKey);
            email = ScenarioContext.get().get("registeredEmail:" + profileKey);
            password = ScenarioContext.get().get("registeredPassword:" + profileKey);
            name = ScenarioContext.get().get("registeredName:" + profileKey);
        }

        // Reuse full original payload so all mandatory fields (firstname, lastname,
        // etc.) are present
        Map<String, Object> original = ScenarioContext.get().get("registrationPayload");
        UserRegistrationData.Builder b = new UserRegistrationData.Builder()
                .name(name)
                .email(email)
                .password(password);

        // Prefer original detailed fields; derive minimal defaults if absent
        if (original != null) {
            Object title = original.getOrDefault("title", "Mr");
            Object first = original.getOrDefault("firstname", original.getOrDefault("firstName", deriveFirst(name)));
            Object last = original.getOrDefault("lastname", original.getOrDefault("lastName", deriveLast(name)));
            Object company = original.getOrDefault("company", "Test Company");
            Object address1 = original.getOrDefault("address1", "123 Main Street");
            Object address2 = original.getOrDefault("address2", "Suite 1");
            Object country = original.getOrDefault("country", "Canada");
            Object state = original.getOrDefault("state", "Ontario");
            Object city = original.getOrDefault("city", "Toronto");
            Object zipcode = original.getOrDefault("zipcode", "A1B2C3");
            Object mobile = original.getOrDefault("mobile_number",
                    original.getOrDefault("mobileNumber", "+1111111111"));
            int day = intOrDefault(original.getOrDefault("birth_date", original.get("dayOfBirth")), 15);
            int month = intOrDefault(original.getOrDefault("birth_month", original.get("monthOfBirth")), 6);
            int year = intOrDefault(original.getOrDefault("birth_year", original.get("yearOfBirth")), 1990);
            b.title(String.valueOf(title))
                    .firstName(String.valueOf(first))
                    .lastName(String.valueOf(last))
                    .company(String.valueOf(company))
                    .address1(String.valueOf(address1))
                    .address2(String.valueOf(address2))
                    .country(String.valueOf(country))
                    .state(String.valueOf(state))
                    .city(String.valueOf(city))
                    .zipcode(String.valueOf(zipcode))
                    .mobileNumber(String.valueOf(mobile))
                    .dayOfBirth(day)
                    .monthOfBirth(month)
                    .yearOfBirth(year);
        } else {
            // Fallback minimal required fields
            b.title("Mr")
                    .firstName(deriveFirst(name))
                    .lastName(deriveLast(name))
                    .address1("123 Main Street")
                    .country("Canada")
                    .state("Ontario")
                    .city("Toronto")
                    .zipcode("A1B2C3")
                    .mobileNumber("+1111111111");
        }

        UserRegistrationData dupData = b.build();
        response = authAPI.register(dupData);
        ScenarioContext.get().set("lastApiResponse", response);
    }

    @When("I login via API with registered credentials")
    public void i_login_with_registered_credentials() {
        String email = ScenarioContext.get().get("registeredEmail");
        String password = ScenarioContext.get().get("registeredPassword");
        response = authAPI.login(email, password);
        ScenarioContext.get().set("lastApiResponse", response);
    }

    @When("I update the account name to {string} and other details same in payload")
    public void i_update_the_account_name_to_and_other_details_same_in_payload(String newName) {
        Map<String, Object> existing = ScenarioContext.get().get("registrationPayload");
        Map<String, Object> updated = new HashMap<>(
                existing == null ? Map.of() : existing);
        updated.put("name", newName);
        // Ensure we use current registered email
        String email = ScenarioContext.get().get("registeredEmail");
        if (email != null)
            updated.put("email", email);
        UserRegistrationData data = UserRegistrationData.fromMap(updated);
        response = accountAPI.updateAccount(data);
        ScenarioContext.get().set("lastApiResponse", response);
        ScenarioContext.get().set("registeredName", newName);
        ScenarioContext.get().set("registrationPayload", updated);
    }

    @When("I delete the registered account")
    public void i_delete_the_registered_account() {
        String email = ScenarioContext.get().get("registeredEmail");
        String password = ScenarioContext.get().get("registeredPassword");
        response = accountAPI.deleteAccount(email, password);
        ScenarioContext.get().set("lastApiResponse", response);
    }

    // Generic assertions
    @Then("response status should be {int}")
    public void response_status_should_be(Integer code) {
        Assert.assertEquals(response.statusCode(), code.intValue());
    }

    @Then("response should match schema {string}")
    public void response_should_match_schema(String schemaFile) {
        response.then().body(matchesJsonSchemaInClasspath("schemas/" + schemaFile));
    }

    @Then("response body should contain {string}")
    public void response_body_should_contain(String text) {
        Assert.assertTrue(response.asString().toLowerCase().contains(text.toLowerCase()),
                "Expected body to contain: " + text + " Actual: " + response.asString());
    }

    @Then("response json path {string} should equal {string}")
    public void response_json_path_should_equal(String path, String expected) {
        if (response == null) {
            Assert.fail("No response available to assert json path " + path);
        }

        JsonPath jp = response.jsonPath();
        String actual = null;

        try {
            // preferred: safely get string value
            actual = jp.getString(path);
        } catch (Exception ignored) {
            // fallback: attempt a generic get and convert to string
            try {
                Object val = jp.get(path);
                actual = val == null ? null : String.valueOf(val);
            } catch (Exception e) {
                Assert.fail("Unable to read json path " + path + ": " + e.getMessage());
            }
        }

        Assert.assertEquals(actual, expected, "Mismatch at json path " + path);
    }

    @When("I login users from data file {string} with record {string}")
    public void iLoginUsersFromDataFileWithRecord(String file, String name) {
        Map<String, Object> selected = TestDataManager.selectRecord(file, name);
        Map<String, Object> payload = new HashMap<>(selected);

        response = authAPI.login(payload.get("email").toString(), payload.get("password").toString());
        ScenarioContext.get().set("lastApiResponse", response);
    }

    @And("products search result list size should be {int}")
    public void productsSearchResultListSizeShouldBe(int expectedSize) {
        if (response == null) {
            Assert.fail("No response available to assert products list size");
        }
        JsonPath jp = response.jsonPath();
        int actualSize = 0;
        try {
            List<?> products = jp.getList("products");
            if (products != null) {
                actualSize = products.size();
            }
        } catch (Exception ignored) {
            // ignore and try a fallback path
        }
        // Fallback: sometimes response can be a root array
        if (actualSize == 0) {
            try {
                List<?> root = jp.getList("$");
                if (root != null) {
                    actualSize = root.size();
                }
            } catch (Exception ignored) {
            }
        }
        Assert.assertEquals(actualSize, expectedSize,
                "Expected products size " + expectedSize + " but was " + actualSize + ". Body: " + response.asString());
    }

    @Given("I clear API auth token")
    public void i_clear_api_auth_token() {
        System.setProperty("api.token", "");
    }

    @Given("I set API auth token to {string}")
    public void i_set_api_auth_token(String token) {
        System.setProperty("api.token", token);
    }

    @Given("I set temporary API base url to {string}")
    public void i_set_temporary_api_base_url(String base) {
        System.setProperty("api.base.url", base);
    }

    @When("I GET raw path {string}")
    public void i_get_raw_path(String path) {
        // Try using configured spec(); fallback to direct RestAssured.get if filters or
        // host cause failures
        try {
            try {
                response = spec().get(path);
            } catch (Exception e) {
                response = null;
            }
            if (response == null) {
                String base = System.getProperty(Constants.PROP_API_BASE_URL, Constants.DEFAULT_API_BASE_URL);
                String url = base.endsWith("/") ? base.substring(0, base.length() - 1) + path : base + path;
                try {
                    response = RestAssured.get(url);
                } catch (Exception e) {
                    response = null;
                }
            }
        } finally {
            // ensure we always set context (may be null) so later steps can inspect
            ScenarioContext.get().set("lastApiResponse", response);
        }
    }

    @When("I measure GET with retries for path {string}")
    public void i_measure_get_with_retries(String path) {
        startTime = System.nanoTime();
        try {
            try {
                response = spec().get(path);
            } catch (Exception e) {
                response = null;
            }
            if (response == null) {
                String base = System.getProperty(Constants.PROP_API_BASE_URL, Constants.DEFAULT_API_BASE_URL);
                String url = base.endsWith("/") ? base.substring(0, base.length() - 1) + path : base + path;
                try {
                    response = RestAssured.get(url);
                } catch (Exception e) {
                    response = null;
                }
            }
        } finally {
            endTime = System.nanoTime();
            ScenarioContext.get().set("lastApiResponse", response);
        }
    }

    // Helper to ensure response variable is populated from scenario context if
    // possible
    private void ensureResponseFromContext() {
        if (response == null) {
            Object o = ScenarioContext.get().get(Constants.CTX_LAST_API_RESPONSE);
            if (o instanceof Response)
                response = (Response) o;
        }
    }

    @Then("response status should be one of {int},{int},{int}")
    public void response_status_should_be_one_of(Integer c1, Integer c2, Integer c3) {
        ensureResponseFromContext();
        if (response == null) {
            Assert.fail("No response available to assert status. lastApiResponse is null.");
        }
        int actual = response.statusCode();
        List<Integer> allowed = Arrays.asList(c1, c2, c3);
        Assert.assertTrue(allowed.contains(actual), "Status " + actual + " not in allowed list " + allowed);
    }

    @Then("I log effective status and note absence of token")
    public void i_log_effective_status_note_absence_token() {
        ensureResponseFromContext();
        if (response == null) {
            Assert.fail("No response available to assert Authorization header absence");
        }
        // No-op: logging would be handled by filters; assertion ensures header absent
        String authHeader = response.getHeader("Authorization");
        Assert.assertNull(authHeader, "Authorization header unexpectedly present in response context");
    }

    @Then("I log effective status and token value")
    public void i_log_effective_status_and_token_value() {
        Assert.assertFalse(System.getProperty("api.token", "").isEmpty(), "Token not set");
    }

    @Then("response body should contain one of {string},{string},{string}")
    public void response_body_should_contain_one_of(String t1, String t2, String t3) {
        ensureResponseFromContext();
        if (response == null) {
            Assert.fail("No response available to assert body contents");
        }
        String body = response.asString().toLowerCase();
        Assert.assertTrue(
                body.contains(t1.toLowerCase()) || body.contains(t2.toLowerCase()) || body.contains(t3.toLowerCase()),
                "Body did not contain any expected token (" + t1 + "," + t2 + "," + t3 + ") Body: " + body);
    }

    @Then("retry attempt count should be greater than 1")
    public void retry_attempt_count_should_be_greater_than_1() {
        int attempts = RetryFilter.getLastAttemptCount();
        Assert.assertTrue(attempts > 1, "Expected retries, attempts=" + attempts);
    }

    @Then("retry attempt count should equal {int}")
    public void retry_attempt_count_should_equal(Integer expected) {
        int attempts = RetryFilter.getLastAttemptCount();
        Assert.assertEquals(attempts, expected.intValue(), "Unexpected attempt count");
    }

    @Then("cumulative wait time should be at least {int} milliseconds")
    public void cumulative_wait_time_should_be_at_least(Integer ms) {
        long elapsedMs = (endTime - startTime) / 1_000_000L;
        Assert.assertTrue(elapsedMs >= ms, "Elapsed " + elapsedMs + "ms was less than expected minimum " + ms + "ms");
    }

    @Then("error mapping indicates failure and message containing {string}")
    public void error_mapping_indicates_failure_and_message(String messageContains) {
        ensureResponseFromContext();
        if (response == null) {
            Assert.fail("No response available to assert error mapping");
        }
        String body = response.asString();
        try {
            JsonPath jp = response.jsonPath();
            Object status = jp.get("status");
            Object responseCode = jp.get("responseCode");
            boolean failureByStatus = status != null && String.valueOf(status).equalsIgnoreCase("false");
            boolean failureByCode = false;
            if (responseCode instanceof Number) {
                failureByCode = ((Number) responseCode).intValue() >= 400;
            } else if (responseCode != null) {
                try {
                    failureByCode = Integer.parseInt(String.valueOf(responseCode)) >= 400;
                } catch (NumberFormatException ignored) {
                }
            }
            boolean failureHeuristic = body.toLowerCase().contains("error") || body.toLowerCase().contains("failed");
            Assert.assertTrue(failureByStatus || failureByCode || failureHeuristic,
                    "Response did not indicate failure via status=false or responseCode>=400. Body: " + body);

            Object message = jp.get("message");
            boolean messageOk = message != null
                    && String.valueOf(message).toLowerCase().contains(messageContains.toLowerCase());
            if (!messageOk) {
                messageOk = body.toLowerCase().contains(messageContains.toLowerCase());
            }
            Assert.assertTrue(messageOk,
                    "Message did not contain expected token '" + messageContains + "'. Body: " + body);
        } catch (Exception e) {
            Assert.fail("Failed to parse/validate error mapping: " + e.getMessage());
        }
    }

    @Then("I wait {int} milliseconds")
    public void i_wait_milliseconds(Integer ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @And("error mapping should have status field {string} and message containing {string}")
    public void error_mapping_should_have_status_field_and_message_containing(String expectedStatus, String needle) {
        ensureResponseFromContext();
        if (response == null) {
            Assert.fail("No response available to assert error mapping");
        }
        JsonPath jp = response.jsonPath();

        // Resolve status from common paths
        String[] statusPaths = { "status", "response.status", "data.status", "success", "ok" };
        Object statusVal = null;
        for (String p : statusPaths) {
            try {
                Object v = jp.get(p);
                if (v != null) {
                    statusVal = v;
                    break;
                }
            } catch (Exception ignored) {
            }
        }
        // If no explicit status, derive it from typical code fields (>=400 => false;
        // else true)
        if (statusVal == null) {
            Object codeVal = null;
            String[] codePaths = { "responseCode", "code", "statusCode", "response.code", "data.code" };
            for (String p : codePaths) {
                try {
                    Object v = jp.get(p);
                    if (v != null) {
                        codeVal = v;
                        break;
                    }
                } catch (Exception ignored) {
                }
            }
            Integer codeInt = null;
            if (codeVal instanceof Number n) {
                codeInt = n.intValue();
            } else if (codeVal != null) {
                try {
                    codeInt = Integer.parseInt(String.valueOf(codeVal));
                } catch (NumberFormatException ignored) {
                }
            }
            if (codeInt != null) {
                statusVal = codeInt >= 400 ? Boolean.FALSE : Boolean.TRUE;
            } else {
                // Heuristic fallback based on body content if neither status nor code found
                String bodyLower = response.asString().toLowerCase();
                if (bodyLower.contains("error") || bodyLower.contains("fail")) {
                    statusVal = Boolean.FALSE;
                }
            }
        }
        String actualStatus = normalizeStatus(statusVal);
        String exp = expectedStatus == null ? "" : expectedStatus.trim().toLowerCase();
        Assert.assertEquals(actualStatus, exp,
                "Expected status '" + exp + "' but was '" + actualStatus + "'. Body: " + response.asString());

        // Resolve message/error text from common paths
        String[] msgPaths = { "message", "error", "error_message", "errorMessage", "response.message", "data.message" };
        String message = null;
        for (String p : msgPaths) {
            try {
                String v = jp.getString(p);
                if (v != null && !v.isBlank()) {
                    message = v;
                    break;
                }
            } catch (Exception ignored) {
            }
        }
        // Fallback: if body is HTML-wrapped, try to extract inner JSON and parse
        // message (and potentially code)
        if (message == null) {
            String raw = response.asString();
            int start = raw.indexOf('{');
            int end = raw.lastIndexOf('}');
            if (start >= 0 && end > start) {
                String jsonChunk = raw.substring(start, end + 1);
                try {
                    JsonPath inner = new JsonPath(jsonChunk);
                    String v = inner.getString("message");
                    if (v != null && !v.isBlank()) {
                        message = v;
                    }
                    // also derive status from code if still unresolved
                    if (statusVal == null) {
                        Object innerCode = inner.get("responseCode");
                        Integer codeInt = null;
                        if (innerCode instanceof Number n)
                            codeInt = n.intValue();
                        else if (innerCode != null) {
                            try {
                                codeInt = Integer.parseInt(String.valueOf(innerCode));
                            } catch (NumberFormatException ignored) {
                            }
                        }
                        if (codeInt != null) {
                            statusVal = codeInt >= 400 ? Boolean.FALSE : Boolean.TRUE;
                        }
                    }
                } catch (Exception ignored) {
                }
            }
        }
        Assert.assertNotNull(message, "No error message field found in response. Body: " + response.asString());
        if (message == null) {
            Assert.fail("Message was null, expected to contain: " + needle);
            return;
        }
        Assert.assertTrue(message.toLowerCase().contains((needle == null ? "" : needle).toLowerCase()),
                "Expected message to contain '" + needle + "' but was '" + message + "'");
    }

    // Small normalizer to unify bool/number/text status to "true"/"false"
    private String normalizeStatus(Object val) {
        if (val == null)
            return "";
        if (val instanceof Boolean b)
            return b ? "true" : "false";
        if (val instanceof Number n)
            return n.intValue() == 0 ? "false" : "true";
        String s = String.valueOf(val).trim().toLowerCase();
        if ("true".equals(s) || "false".equals(s))
            return s;
        if ("success".equals(s) || "ok".equals(s))
            return "true";
        if ("fail".equals(s) || "failed".equals(s) || "error".equals(s))
            return "false";
        return s; // fallback: compare raw lower-cased text
    }

    private static String deriveFirst(String name) {
        if (name == null || name.isBlank())
            return "User";
        String[] p = name.trim().split(" ");
        return p[0];
    }

    private static String deriveLast(String name) {
        if (name == null || name.isBlank())
            return "Test";
        String[] p = name.trim().split(" ");
        return p.length > 1 ? p[p.length - 1] : "Test";
    }

    private static int intOrDefault(Object o, int def) {
        try {
            return o == null ? def : Integer.parseInt(String.valueOf(o));
        } catch (Exception e) {
            return def;
        }
    }
}
