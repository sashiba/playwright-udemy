package org.sashiba;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.junit.UsePlaywright;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@UsePlaywright
public class ASimplePlaywrightTest {
    private static final String URL = "https://practicesoftwaretesting.com";

//    @UsePlaywright is doing setup() and tearDown(), we need to pass Page as parameter in tests
//    private Playwright playwright;
//    private Browser browser;
//    private Page page;
//    @BeforeEach
//    void setup() {
//        playwright = Playwright.create();
//        browser = playwright.chromium().launch();
//        page = browser.newPage();
//    }
//
//    @AfterEach
//    void tearDown() {
//        browser.close();
//        playwright.close();
//    }

    @Test
    void pageTitleTest(Page page) {
        page.navigate(URL);

        String title = page.title();
        Assertions.assertTrue(title.contains("Practice Software Testing"));
    }

    @Test
    void searchFieldTest(Page page) {
        page.navigate(URL);

        page.locator("#search-query").fill("Pliers");
        page.locator("button:has-text('Search')").click();
        int matchingResults = page.locator(".card").count();

        Assertions.assertTrue(matchingResults > 0);
    }
}
