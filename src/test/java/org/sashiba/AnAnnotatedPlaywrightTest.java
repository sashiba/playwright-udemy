package org.sashiba;

import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.junit.Options;
import com.microsoft.playwright.junit.OptionsFactory;
import com.microsoft.playwright.junit.UsePlaywright;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

@UsePlaywright(AnAnnotatedPlaywrightTest.CustomOptions.class)
public class AnAnnotatedPlaywrightTest {
    private static final String URL = "https://practicesoftwaretesting.com";

    public static class CustomOptions implements OptionsFactory {

        @Override
        public Options getOptions() {
            return new Options()
                    .setHeadless(false)
                    .setLaunchOptions(new BrowserType.LaunchOptions().setArgs(List.of("--no-sandbox", "--disable-gpu", "--disable-extensions")));
        }
    }

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
