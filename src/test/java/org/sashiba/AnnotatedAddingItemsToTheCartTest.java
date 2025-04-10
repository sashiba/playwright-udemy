package org.sashiba;

import com.microsoft.playwright.*;
import com.microsoft.playwright.junit.UsePlaywright;
import com.microsoft.playwright.options.AriaRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@UsePlaywright(HeadlessChromeOptions.class)
public class AnnotatedAddingItemsToTheCartTest {
    private static final String URL = "https://practicesoftwaretesting.com";

    @DisplayName("Search for pliers")
    @Test
    void searchForPliers(Page page, Playwright playwright, Browser browser, BrowserContext browserContext) {
//        page.onConsoleMessage(msg -> System.out.println(msg.text()));
        page.navigate(URL);

        page.getByPlaceholder("Search").fill("Pliers");
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Search")).click();
        //page.getByPlaceholder("Search").press("Enter");

        assertThat(page.locator(".card")).hasCount(4);

        List<String> productNames = page.getByTestId("product-name").allTextContents();
        org.assertj.core.api.Assertions.assertThat(productNames).allMatch(n -> n.contains("Pliers"));

        Locator outOfStock = page.locator(".card")
                .filter(new Locator.FilterOptions().setHasText("Out of stock"))
                .getByTestId("product-name");

        assertThat(outOfStock).hasCount(1);
        assertThat(outOfStock).hasText("Long Nose Pliers");

//        page.waitForLoadState();
//        page.waitForCondition(() -> page.getByTestId("product-name").count() > 0);
//
//        List<String> products = page.getByTestId("product-name").allTextContents();
//        Assertions.assertThat(products.get(0)).containsIgnoringCase("Pliers");
//
//        assertThat(page.locator(".card")).hasCount(4);
//
//        List<String> productNames = page.getByTestId("product-name").allTextContents();
//        Assertions.assertThat(productNames).allMatch(name -> name.contains("Pliers"));
//
//        Locator outOfStockItem = page.locator(".card")
//                .filter(new Locator.FilterOptions().setHasText("Out of stock"))
//                .getByTestId("product-name");
//
//        assertThat(outOfStockItem).hasCount(1);
//        assertThat(outOfStockItem).hasText("Long Nose Pliers");
    }
}
