package org.sashiba;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.AriaRole;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;

import java.util.Arrays;
import java.util.List;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class PlaywrightPageObjectTest {

    protected static Playwright playwright;
    protected static Browser browser;
    protected static BrowserContext browserContext;
    protected Page page;

    @BeforeAll
    static void setupBrowser() {
        playwright = Playwright.create();
        playwright.selectors().setTestIdAttribute("data-test");
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
                .setHeadless(false)
                .setArgs(Arrays.asList("--no-sandbox", "--disable-extensions", "--disable-gpu")));
    }

    @BeforeEach
    void setup() {
        browserContext = browser.newContext();
        page = browserContext.newPage();
    }

    @AfterEach
    void closeContext() {
        browserContext.close();
    }

    @AfterAll
    static void tearDown() {
        browser.close();
        playwright.close();
    }

    @BeforeEach
    void openHomePage() {
        page.navigate("https://practicesoftwaretesting.com");
    }

    @Nested
    class WhenSearchingProductsByKeyword {
        @DisplayName("Without Page Objects")
        @Test
        void withoutPageObjects() {
            page.waitForResponse("**/products/search?q=tape", () -> {
                page.getByPlaceholder("Search").fill("tape");
                page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Search")).click();
            });
            List<String> matchingProducts = page.getByTestId("product-name").allInnerTexts();
            Assertions.assertThat(matchingProducts)
                    .contains("Tape Measure 7.5m", "Measuring Tape", "Tape Measure 5m");
        }

        @DisplayName("With Page Objects")
        @Test
        void withPageObjects() {
            SearchComponent searchComponent = new SearchComponent(page);
            ProductList productList = new ProductList(page);

            searchComponent.searchBy("tape");
            List<String> matchingProducts = productList.getProductNames();

            Assertions.assertThat(matchingProducts)
                    .contains("Tape Measure 7.5m", "Measuring Tape", "Tape Measure 5m");
        }
    }

    @Nested
    class WhenAddingItemsToTheCart {
        protected SearchComponent searchComponent;
        protected ProductList productList;
        protected ProductDetails productDetails;
        protected NavBar navBar;
        protected CheckoutCart checkoutCart;

        @BeforeEach
        void setup() {
            searchComponent = new SearchComponent(page);
            productList = new ProductList(page);
            productDetails = new ProductDetails(page);
            navBar = new NavBar(page);
            checkoutCart = new CheckoutCart(page);
        }

        @DisplayName("Without Page Objects")
        @Test
        void withoutPageObjects() {
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
            assertThat(page.locator(".product-title").getByText("Combination Pliers")).isVisible();
            assertThat(page.getByTestId("cart-quantity").getByText("3")).isVisible();
        }

        @Test
        void withPageObjects() {
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
            Assertions.assertThat(cartLineItems)
                    .hasSize(1)
                    .first()
                    .satisfies(item -> {
                        Assertions.assertThat(item.title()).contains("Combination Pliers");
                        Assertions.assertThat(item.quantity()).isEqualTo(3);
                        Assertions.assertThat(item.total()).isEqualTo(item.quantity() * item.price());
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
            Assertions.assertThat(cartLineItems).hasSize(2);

            List<String> productNames = cartLineItems.stream().map(CartLineItem::title).toList();
            Assertions.assertThat(productNames).contains("Bolt Cutters", "Slip Joint Pliers");

            Assertions.assertThat(cartLineItems)
                    .allSatisfy(item -> {
                        Assertions.assertThat(item.quantity()).isGreaterThanOrEqualTo(1);
                        Assertions.assertThat(item.price()).isGreaterThan(0.0);
                        Assertions.assertThat(item.total()).isGreaterThan(0.0);
                        Assertions.assertThat(item.total()).isEqualTo(item.quantity() * item.price());
                    });
        }
    }

    static class SearchComponent {
        private final Page page;

        public SearchComponent(Page page) {
            this.page = page;
        }

        public void searchBy(String keyword) {
            page.waitForResponse("**/products/search?q=" + keyword, () -> {
                page.getByPlaceholder("Search").fill(keyword);
                page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Search")).click();
            });
        }
    }

    static class ProductList {
        private final Page page;

        public ProductList(Page page) {
            this.page = page;
        }

        public List<String> getProductNames() {
            return page.getByTestId("product-name").allInnerTexts();
        }

        public void viewProductDetails(String product) {
            page.locator(".card").getByText(product).click();
        }
    }

    static class ProductDetails {
        private final Page page;

        public ProductDetails(Page page) {
            this.page = page;
        }

        public void increaseQuantityBy(int increment) {
            for (int i = 1; i <= increment; i++) {
                page.getByTestId("increase-quantity").click();
            }
        }

        public void addToCart() {
            page.waitForResponse(
                    response -> response.url().contains("/carts") && response.request().method().equals("POST"), //wait for POST to /carts
                    () -> {
                        page.getByText("Add to cart").click();
                        page.getByRole(AriaRole.ALERT).click();
                    } //callback - code that triggers the request
            );
        }
    }

    static class NavBar {
        private final Page page;

        public NavBar(Page page) {
            this.page = page;
        }

        public void openCart() {
            page.getByTestId("nav-cart").click();
        }

        public void openHomePage() {
            page.navigate("https://practicesoftwaretesting.com");
        }
    }

    static class CheckoutCart {
        private final Page page;

        public CheckoutCart(Page page) {
            this.page = page;
        }

        public List<CartLineItem> getLineItems() {
            page.locator("app-cart tbody tr").first().waitFor();
            return page.locator("app-cart tbody tr")
                    .all()
                    .stream()
                    .map(row -> {
                        String title = row.getByTestId("product-title").innerText().strip().replaceAll("\u00A0", "");
                        int quantity = Integer.parseInt(row.getByTestId("product-quantity").inputValue());
                        Double price = price(row.getByTestId("product-price").innerText());
                        Double total = price(row.getByTestId("line-price").innerText());
                        return new CartLineItem(title, quantity, price, total);
                    })
                    .toList();
        }
    }

    record CartLineItem(String title, int quantity, Double price, Double total) {
    }

    public static Double price(String value) {
        return Double.parseDouble(value.replace("$", ""));
    }
}
