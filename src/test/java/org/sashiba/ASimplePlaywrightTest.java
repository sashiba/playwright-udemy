package org.sashiba;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.*;

import java.util.List;

public class ASimplePlaywrightTest {
    private static final String URL = "https://practicesoftwaretesting.com";

    private static Playwright playwright;
    private static Browser browser;
    private static BrowserContext browserContext;
    private Page page;

    @BeforeAll
    public static void setupBrowser() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
                .setHeadless(false)
                .setArgs(List.of("--no-sandbox", "--disable-gpu", "--disable-extensions")));
        browserContext = browser.newContext();
    }

    @BeforeEach
    public void setup() {
        page = browserContext.newPage();
    }

    @AfterAll
    public static void tearDown() {
        browser.close();
        playwright.close();
    }

    @Test
    void pageTitleTest() {
        page.navigate(URL);

        String title = page.title();
        Assertions.assertTrue(title.contains("Practice Software Testing"));
    }

    @Test
    void searchFieldTest() {
        page.navigate(URL);

        page.locator("#search-query").fill("Pliers");
        page.locator("button:has-text('Search')").click();
        int matchingResults = page.locator(".card").count();

        Assertions.assertTrue(matchingResults > 0);
    }
}
