Feature: Product Catalog
  As a customer,
  I want to easily search, filter and sort products in the catalog
  So that I can find what I need quickly.

  Sally is an online customer

  Rule: Customers should be able to search for products by name
    Example: The one where Sally searches for an Adjustable Wrench
      Given Sally is on the home page
      When she searches for "Adjustable Wrench"
      Then the "Adjustable Wrench" product should be displayed

    Example: The one where Sally searches for a more general term1
      Given Sally is on the home page
      When she searches for "saw"
      Then the following products should be displayed as list:
        | Wood Saw     |
        | Circular Saw |

    Example: The one where Sally searches for a more general term2
      Given Sally is on the home page
      When she searches for "saw"
      Then the following products should be displayed:
        | Product      | Price  |
        | Wood Saw     | $12.18 |
        | Circular Saw | $80.19 |

    Example: The one where Sally searches for a product that doesn't exist
      Given Sally is on the home page
      When she searches for "Product does not exist"
      Then no products should be displayed
      And the message "There are no products found." should be displayed

  Rule: Customers should be able to narrow downs their search by category
    Example:The one where Sally only wants to see Hand Saws
      Given Sally is on the home page
      When she searches for "saw"
      And she filters by "Hand Saw"
      Then the following products should be displayed:
        | Product  | Price  |
        | Wood Saw | $12.18 |
