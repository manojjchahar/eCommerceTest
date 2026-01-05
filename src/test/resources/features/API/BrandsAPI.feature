@eCommFull @API @BrandsAPI

Feature: Brands API
  Validate brand listing


  Scenario: Retrieve brands list
    When I request the brands list
    Then response status should be 200
    And response should match schema "brandSchema.json"

