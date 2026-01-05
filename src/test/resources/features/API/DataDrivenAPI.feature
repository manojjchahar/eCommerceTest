@eCommFull @API @DataDrivenAPI

Feature: Data-driven API tests
  Use JSON inputs to run multiple API validations


@testit
  Scenario: Login all users from JSON file
    When I login users from data file "users.json" with record "mxk"
    Then response status should be 200


