package org.sashiba;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.AriaRole;
import org.junit.jupiter.api.*;

import java.util.Arrays;
import java.util.List;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class AddingItemsToTheCartTest {

    private static final String URL = "https://practicesoftwaretesting.com";
    protected static Playwright playwright;
    protected static Browser browser;
    protected static BrowserContext browserContext;
    private Page page;

    @BeforeAll
    static void setUpBrowser() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(
                new BrowserType.LaunchOptions().setHeadless(true)
                        .setArgs(Arrays.asList("--no-sandbox", "--disable-extensions", "--disable-gpu"))
        );
        playwright.selectors().setTestIdAttribute("data-test");
    }

    @BeforeEach
    void setUp() {
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

    @DisplayName("Search for pliers")
    @Test
    void searchForPliers() {
        page.navigate(URL);

        page.getByPlaceholder("Search").fill("Pliers");
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Search")).click();

        assertThat(page.locator(".card")).hasCount(4);

        List<String> productNames = page.getByTestId("product-name").allTextContents();
        org.assertj.core.api.Assertions.assertThat(productNames).allMatch(n -> n.contains("Pliers"));

        Locator outOfStock = page.locator(".card")
                .filter(new Locator.FilterOptions().setHasText("Out of stock"))
                .getByTestId("product-name");

        assertThat(outOfStock).hasCount(1);
        assertThat(outOfStock).hasText("Long Nose Pliers");
    }
}
