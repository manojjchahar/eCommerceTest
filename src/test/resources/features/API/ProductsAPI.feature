@eCommFull @API @ProductsAPI

Feature: Products API
  Validate products listing and search capabilities

  Background:
    Given products exist in the catalog


  Scenario: Retrieve all products
    When I request the products list
    Then response status should be 200
    And response should match schema "productSchema.json"


  Scenario: Search products by keyword returns filtered results
    When I search products by keyword "shirt"
    Then response status should be 200
    And response body should contain "products"


  Scenario: Search products with no match returns empty array
    When I search products by keyword "__unlikely_search_token__"
    Then response status should be 200
    And products search result list size should be 0
