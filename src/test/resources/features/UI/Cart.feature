@eCommFull @Cart @UI

Feature: Cart operations
  As a shopper
  I want to manage my cart by adding/removing items
  I want to checkout successfully

  @ItemAdding @headerNavigation
  Scenario: Cart shows added product
    Given user is on the login page
    When user logs in with "mxk"
    Given user adds an item to cart
    When user navigates to cart page
    Then cart should have at least one item
    Then cart should contain same product added

  @ItemRemoval @uidraft
  Scenario: Cart is empty after removing item
    Given user is on the login page
    When user logs in with "mxk"
    Given user adds an item to cart
    When user navigates to cart page
    And user removes the added item from cart
    Then verify item removal

 @headerNavigation @test1
  Scenario Outline: Successful checkout process
    Given user is on the login page
    When user logs in with "mxk"
    And user adds an item to cart
    And user navigates to cart page
    Then user calculates total cart amount
    Then user clicks proceed to checkout
    Then user verifies that totalAmount on checkout page matches with cart total amount
    And user clicks on place order button
    Then user is redirected to payments page
    When user enters payment details for "<profile>"
    And user clicks on pay and confirm order button
    Then user should see success "<message>"
    And user is navigated to order confirmation page with "<heading>" and a "<newMessage>"
    Then user can download the invoice for the order and verify user and amount.
    And user clicks on continue button to go to home page

    Examples:
      | profile   | message                                   | heading       | newMessage                                         |
      | John Doe  | Your order has been placed successfully!   | Order Placed! | Congratulations! Your order has been confirmed!    |
