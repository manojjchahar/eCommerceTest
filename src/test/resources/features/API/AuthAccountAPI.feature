@eCommFull @API @AuthenticationAPI

Feature: Authentication and Account lifecycle APIs
  Cover register, login, update, fetch, and delete flows

  @api @account @lifecycle @DataDrivenAPI
  Scenario: Register new account, login, update, fetch and delete
    # Use the full registration payload from test data file which contains all required fields
    When I register a new user using test data file "registration-data.json" with record "default"
    Then response status should be 200
    And response body should contain "User created"
    When I login via API with registered credentials
    Then response status should be 200
    When I update the account name to "Updated Test User" and other details same in payload
    Then response status should be 200
    When I fetch user by the registered email
    Then response status should be 200
    And response json path "user.name" should equal "Updated Test User"
    When I delete the registered account
    Then response status should be 200

  @api @account @negative @DataDrivenAPI
  Scenario: Duplicate registration should show already exists message
    # Register first user using a dedicated record in the same registration data file
    When I register a new user using test data file "registration-data.json" with record "default"
    And I attempt duplicate registration using test data file "registration-data.json" with record "default"
    Then response status should be 200
    And response body should contain "already exist"

  @api @auth @negative
  Scenario: Login with invalid credentials returns failure message
    When I login via API with email "invalid@example.com" and password "wrong"
    Then response status should be 200
    And response body should contain "not found"
