package org.sashiba;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.SelectOption;
import com.microsoft.playwright.options.WaitForSelectorState;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class PlaywrightWaitsTest {

    protected static Playwright playwright;
    protected static Browser browser;
    protected static BrowserContext browserContext;
    protected Page page;


    @BeforeAll
    static void setupBrowser() {
        playwright = Playwright.create();
        playwright.selectors().setTestIdAttribute("data-test");
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
                .setHeadless(true)
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

    @Nested
    class WaitingForState {
        @BeforeEach
        void openHomePage() {
            page.navigate("https://practicesoftwaretesting.com");
            page.waitForSelector(".card-img-top");
        }

        @Test
        void shouldShowAllProductNames() {
            List<String> productNames = page.getByTestId("product-name").allInnerTexts();

            Assertions.assertThat(productNames).contains("Pliers", "Bolt Cutters", "Hammer");
        }

        @Test
        void shouldShowAllProductImages() {
            List<String> productImageTitles = page.locator(".card-img-top").all()
                    .stream().map(i -> i.getAttribute("alt"))
                    .toList();
            Assertions.assertThat(productImageTitles).contains("Pliers", "Bolt Cutters", "Hammer");
        }
    }

    @Nested
    class AutomaticWaits {
        @BeforeEach
        void openHomePage() {
            page.navigate("https://practicesoftwaretesting.com");
        }

        // Automatic wait
        @Test
        @DisplayName("Should wait for the filter checkbox options to appear before clicking")
        void shouldWaitForTheFilterCheckboxes() {
            Locator screwdriver = page.getByLabel("Screwdriver");
            screwdriver.click();
            assertThat(screwdriver).isChecked();
        }

        @Test
        @DisplayName("Should filter products by category")
        void shouldFilterProductsByCategory() {
            page.getByRole(AriaRole.MENUBAR).getByText("Categories").click();
            page.getByRole(AriaRole.MENUBAR).getByText("Power Tools").click();

            page.waitForSelector(".card",
                    new Page.WaitForSelectorOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(2000)
            );

            List<String> filteredProducts = page.getByTestId("product-name").allInnerTexts();
            Assertions.assertThat(filteredProducts).contains("Sheet Sander", "Belt Sander", "Random Orbit Sander");
        }
    }

    @Nested
    class WaitingForElementsToAppearAndDisappear {
        @BeforeEach
        void openHomePage() {
            page.navigate("https://practicesoftwaretesting.com");
        }

        @Test
        @DisplayName("It should display a toaster message when an item is added to the cart")
        void shouldDisplayToasterMessage() {
            page.getByText("Bolt Cutters").click();
            page.getByText("Add to cart").click();

            //wait for toast to appear
            assertThat(page.getByRole(AriaRole.ALERT)).isVisible();
            assertThat(page.getByRole(AriaRole.ALERT)).hasText("Product added to shopping cart.");

            page.waitForCondition(() -> page.getByRole(AriaRole.ALERT).isHidden());
        }

        @Test
        @DisplayName("Should update the cart item count")
        void shouldUpdateCartItemCount() {
            page.getByText("Bolt Cutters").click();
            page.getByText("Add to cart").click();

            page.waitForCondition(() -> page.getByTestId("cart-quantity").textContent().equals("1"));
            page.waitForSelector("[data-test=cart-quantity]:has-text('1')");
        }
    }

    @Nested
    class WaitingForAPICalls {
        @Test
        void sortByDescendingPrice() {
            page.navigate("https://practicesoftwaretesting.com");
            // Sort by descending price
            //page.getByTestId("sort").selectOption(new SelectOption().setValue("price,desc"));
            //page.getByTestId("product-price").first().waitFor(); //wait for first to appear
//            page.waitForResponse("https://api.practicesoftwaretesting.com/products?sort=price,desc&between=price,1,100&page=0",
//                    () -> {});

            page.waitForResponse("**/products?sort=**", () -> {
                page.getByTestId("sort").selectOption(new SelectOption().setValue("price,desc"));
            });

            // Find all the prices on the page
            List<Double> productPrices = page.getByTestId("product-price").allTextContents()
                    .stream().map(PlaywrightWaitsTest::extractPrice).toList();

            // Are the prices in the correct order
            System.out.println("product prices: " + productPrices);
            Assertions.assertThat(productPrices)
                    .isNotEmpty()
                    .isSortedAccordingTo(Comparator.reverseOrder());
        }
    }

    private static double extractPrice(String price) {
        return Double.parseDouble(price.replace("$", ""));
    }
}