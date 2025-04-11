package org.sashiba;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.microsoft.playwright.*;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.Stream;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class PlaywrightRestAPITest {
    protected static Playwright playwright;
    protected static Browser browser;
    protected static BrowserContext browserContext;

    protected Page page;

    @BeforeAll
    static void setUpBrowser() {
        playwright = Playwright.create();
        playwright.selectors().setTestIdAttribute("data-test");
        browser = playwright.chromium().launch(
                new BrowserType.LaunchOptions().setHeadless(false)
                        .setArgs(Arrays.asList("--no-sandbox", "--disable-extensions", "--disable-gpu"))
        );
    }

    @BeforeEach
    void setup() {
        browserContext = browser.newContext();
        page = browserContext.newPage();

        page.navigate("https://practicesoftwaretesting.com");
        page.getByPlaceholder("Search").waitFor();
    }

    @AfterEach
    void close() {
        browserContext.close();
    }

    @AfterAll
    static void tearDown() {
        browser.close();
        playwright.close();
    }

    @DisplayName("Playwright allows us to mock out API responses")
    @Nested
    class MockingAPIResponses {
        @Test
        @DisplayName("When a search returns a single product")
        void whenASingleItemIsFound() {
            // /products/search?q=Pliers
            page.route("**/products/search?q=Pliers", route -> {
                route.fulfill(new Route.FulfillOptions()
                        .setBody(MockSearchResponses.RESPONSE_WITH_A_SINGLE_ENTRY)
                        .setStatus(200));
            });

            Locator search = page.getByPlaceholder("Search");
            search.fill("Pliers");
            search.press("Enter");

            //if real data - assertions will fail
            assertThat(page.getByTestId("product-name")).hasCount(1);
            assertThat(page.getByTestId("product-name")).hasText("Super Pliers");
        }

        @Test
        @DisplayName("When a search returns no products")
        void whenNoItemsAreFound() {
            page.route("**/products/search?q=Test123", route -> {
                route.fulfill(new Route.FulfillOptions()
                        .setBody(MockSearchResponses.RESPONSE_WITH_NO_ENTRIES)
                        .setStatus(200));
            });

            Locator search = page.getByPlaceholder("Search");
            search.fill("Test123");
            search.press("Enter");

            assertThat(page.getByTestId("no-results")).isVisible();
            assertThat(page.getByTestId("no-results")).hasText("There are no products found.");
            assertThat(page.getByTestId("product-name")).hasCount(0);
        }
    }

    @Nested
    class MakingAPICalls {
        record Product(String name, Double price) {
        }

        private static APIRequestContext apiRequestContext;

        @BeforeAll
        static void setupRequestContext() {
            apiRequestContext = playwright.request().newContext(
                    new APIRequest.NewContextOptions()
                            .setBaseURL("https://api.practicesoftwaretesting.com")
                            .setExtraHTTPHeaders(new HashMap<>() {{
                                put("Accept", "application/json");
                            }})
            );
        }

        @DisplayName("Check presence of known products")
        @ParameterizedTest(name = "Checking product {0}")
        @MethodSource("products")
        void checkKnownProducts(Product product) {
            page.fill("[placeholder='Search']", product.name);
            page.click("button:has-text('Search')");

            //Check that the product appears with the correct name and price

            Locator productCard = page.locator(".card").filter(new Locator.FilterOptions()
                    .setHasText(product.name)
                    .setHasText(Double.toString(product.price)));

            assertThat(productCard).isVisible();
        }

        static Stream<Product> products() {
            APIResponse response = apiRequestContext.get("/products?page=2");
            Assertions.assertThat(response.status()).isEqualTo(200);

            JsonObject jsonObject = new Gson().fromJson(response.text(), JsonObject.class);
            JsonArray data = jsonObject.getAsJsonArray("data");

            return data.asList().stream()
                    .map(element -> {
                        JsonObject product = element.getAsJsonObject();
                        return new Product(product.get("name").getAsString(),
                                product.get("price").getAsDouble());
                    });
        }
    }
}
