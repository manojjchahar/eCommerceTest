@eCommFull @Products @UI

Feature: Browse/Search products, filter products, brand navigation
  As a shopper
  I want to browse and search products and add them to cart
  I want to filter products by category, and brand


  @ui @regression
  Scenario Outline: Search product by keyword
    Given user is on the login page
    When user logs in with "<User>"
    Given user is on the products page
    When user searches for product with "<keyWord>"
    Then products should be listed related to "<keyWord>"
    Examples:
      |keyWord|User |
      |Dress|mxk    |

  @ui
  Scenario Outline: Add product to cart
    Given user is on the login page
    When user logs in with "<User>"
    Given user is on the products page
    When user adds first product to cart
    Then user must see confirmation "<Message>"
    Examples:
      |Message|User                     |
      |Your product has been added to cart.|mxk|

  @headerNavigation @test2
  Scenario Outline: Filter products by category
    Given user is on the login page
    When user logs in with "<User>"
    Given user is on the products page
    Then user clicks on "<subCategory>" from the dropdown of "<category>"
    Then user should see heading of products page as "<category> - <subCategory> PRODUCTS"
    Then all filtered products should be of same category and sub-category as above as seen on product details page
    Examples:
      |category|subCategory|User|
      |WOMEN   |TOPS       |mxk |
      |MEN     |JEANS      |mxk |


  Scenario Outline: Filter products by brand
    Given user is on the login page
    When user logs in with "<User>"
    Given user is on the products page
    Then user clicks on "<brand>" from the brands list
    Then user should see heading of products page as "BRAND - <brand> PRODUCTS"
    Then number of listed products after filtering with brand should be same number as shown in brands section
    Then all filtered products should be of same selected "<brand>" as seen on product details page
    Examples:
        |brand      |User|
        |Polo       |mxk |
        |H&M        |mxk |
        |Allen Solly|mxk |


