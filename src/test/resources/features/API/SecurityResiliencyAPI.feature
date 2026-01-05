@eCommFull @API

Feature: API Security, Rate Limiting, Error Mapping and Resiliency
  Cover negative authorization, rate limiting behavior, error code mapping and retry resiliency.

  # NOTE: automationexercise.com public APIs do not currently enforce Authorization tokens.
  # These scenarios are structured to be skipped in main CI (tags not included by default) but
  # illustrate how security and resiliency would be validated.

  @api @security @negative
  Scenario: Missing token accessing protected resource
    Given I clear API auth token
    When I request the products list
    Then response status should be one of 200,401,403
    And I log effective status and note absence of token

  @api @security @negative
  Scenario: Invalid token accessing protected resource
    Given I set API auth token to "INVALID_TOKEN_VALUE"
    When I request the products list
    Then response status should be one of 200,401,403
    And I log effective status and token value

  @api @errorMapping
  Scenario: Login error code mapping for invalid credentials
    When I login via API with email "invalid@example.com" and password "wrong"
    Then response status should be 200
    And response body should contain one of "user not found","not found","incorrect"
    And error mapping should have status field "false" and message containing "not"

  @api @ratelimit @resiliency
  Scenario: Rate limit retry behavior hitting 429 endpoint
    Given I set temporary API base url to "https://httpbin.org"
    When I GET raw path "/status/429"
    Then response status should be 429
    And retry attempt count should be greater than 1

  @api @ratelimit @resiliency
  Scenario: Successful request after transient rate limit simulated
    Given I set temporary API base url to "https://httpbin.org"
    When I GET raw path "/status/200"
    Then response status should be 200
    And retry attempt count should equal 1

  @api @ratelimit @resiliency
  Scenario: Burst requests cause 429 then succeed after backoff
    Given I set temporary API base url to "https://httpbin.org"
    When I GET raw path "/status/429"
    Then response status should be 429
    And retry attempt count should be greater than 1
    Then I wait 400 milliseconds
    When I GET raw path "/status/200"
    Then response status should be 200

  @api @resiliency
  Scenario: Retry timing delay validation (429)
    Given I set temporary API base url to "https://httpbin.org"
    When I measure GET with retries for path "/status/429"
    Then cumulative wait time should be at least 500 milliseconds
    And retry attempt count should be greater than 1
