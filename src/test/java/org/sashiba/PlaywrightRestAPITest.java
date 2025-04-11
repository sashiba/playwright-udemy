package org.sashiba;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.HashMap;

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

    }
}
