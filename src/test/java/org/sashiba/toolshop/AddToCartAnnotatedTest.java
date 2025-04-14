package org.sashiba.toolshop;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.assertions.PlaywrightAssertions;
import com.microsoft.playwright.junit.UsePlaywright;
import com.microsoft.playwright.options.AriaRole;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sashiba.HeadlessChromeOptions;
import org.sashiba.toolshop.pageobjects.*;

import java.util.List;

@UsePlaywright(HeadlessChromeOptions.class)
public class AddToCartAnnotatedTest {
    protected SearchComponent searchComponent;
    protected ProductList productList;
    protected ProductDetails productDetails;
    protected NavBar navBar;
    protected CheckoutCart checkoutCart;

    @BeforeEach
    void setupTests(Page page) {
        searchComponent = new SearchComponent(page);
        productList = new ProductList(page);
        productDetails = new ProductDetails(page);
        navBar = new NavBar(page);
        checkoutCart = new CheckoutCart(page);
    }

    @BeforeEach
    void openHomePage(Page page) {
        page.navigate("https://practicesoftwaretesting.com");
    }

    @DisplayName("Without Page Objects")
    @Test
    void withoutPageObjects(Page page) {
        // Search for pliers
        page.waitForResponse("**/products/search?q=pliers", () -> {
            page.getByPlaceholder("Search").fill("pliers");
            page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Search")).click();
        });
        // Show details page
        page.locator(".card").getByText("Combination Pliers").click();

        // Increase cart quanity
        page.getByTestId("increase-quantity").click();
        page.getByTestId("increase-quantity").click();
        // Add to cart
        page.getByText("Add to cart").click();
        page.waitForCondition(() -> page.getByTestId("cart-quantity").textContent().equals("3"));

        // Open the cart
        page.getByTestId("nav-cart").click();

        // check cart contents
        PlaywrightAssertions.assertThat(page.locator(".product-title").getByText("Combination Pliers")).isVisible();
        PlaywrightAssertions.assertThat(page.getByTestId("cart-quantity").getByText("3")).isVisible();
    }

    @Test
    void whenCheckingOutASingleItem() {
        // Search for pliers
        searchComponent.searchBy("pliers");
        // Show details page
        productList.viewProductDetails("Combination Pliers");
        // Increase cart quanity
        productDetails.increaseQuantityBy(2);
        // Add to cart
        productDetails.addToCart();

        // Open the cart
        navBar.openCart();

        // check cart contents
        List<CartLineItem> cartLineItems = checkoutCart.getLineItems();
        org.assertj.core.api.Assertions.assertThat(cartLineItems)
                .hasSize(1)
                .first()
                .satisfies(item -> {
                    org.assertj.core.api.Assertions.assertThat(item.title()).contains("Combination Pliers");
                    org.assertj.core.api.Assertions.assertThat(item.quantity()).isEqualTo(3);
                    org.assertj.core.api.Assertions.assertThat(item.total()).isEqualTo(item.quantity() * item.price());
                });
    }

    @Test
    void whenCheckingOutMultipleItems() {
        navBar.openHomePage();

        productList.viewProductDetails("Bolt Cutters");
        productDetails.increaseQuantityBy(2);
        productDetails.addToCart();

        navBar.openHomePage();
        productList.viewProductDetails("Slip Joint Pliers");
        productDetails.addToCart();

        navBar.openCart();
        List<CartLineItem> cartLineItems = checkoutCart.getLineItems();
        org.assertj.core.api.Assertions.assertThat(cartLineItems).hasSize(2);

        List<String> productNames = cartLineItems.stream().map(CartLineItem::title).toList();
        org.assertj.core.api.Assertions.assertThat(productNames).contains("Bolt Cutters", "Slip Joint Pliers");

        org.assertj.core.api.Assertions.assertThat(cartLineItems)
                .allSatisfy(item -> {
                    org.assertj.core.api.Assertions.assertThat(item.quantity()).isGreaterThanOrEqualTo(1);
                    org.assertj.core.api.Assertions.assertThat(item.price()).isGreaterThan(0.0);
                    org.assertj.core.api.Assertions.assertThat(item.total()).isGreaterThan(0.0);
                    Assertions.assertThat(item.total()).isEqualTo(item.quantity() * item.price());
                });
    }
}
