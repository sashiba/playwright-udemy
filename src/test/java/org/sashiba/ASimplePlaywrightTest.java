package org.sashiba;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

public class ASimplePlaywrightTest {
    private static final String URL = "https://practicesoftwaretesting.com";

    private Playwright playwright;
    private Browser browser;
    private Page page;

    @BeforeEach
    void setup() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
                .setHeadless(false)
                .setArgs(List.of("--no-sandbox", "--disable-gpu", "--disable-extensions")));
        page = browser.newPage();
    }

    @AfterEach
    void tearDown() {
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
