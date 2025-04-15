package org.sashiba.toolshop.catalog;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.sashiba.fixtures.PlaywrightTestCase;
import org.sashiba.toolshop.catalog.pageobjects.ProductList;
import org.sashiba.toolshop.catalog.pageobjects.SearchComponent;

import java.util.List;

@Execution(ExecutionMode.SAME_THREAD)
@DisplayName("Searching for products")
@Feature("Product Catalog")
@Story("Searching for products")
public class SearchForProductsTest extends PlaywrightTestCase {
    @BeforeEach
    void openHomePage() {
        page.navigate("https://practicesoftwaretesting.com");
    }

    @DisplayName("Without Page Objects")
    @Test
    void withoutPageObjects() {
        page.waitForResponse("**/products/search?q=tape", () -> {
            page.getByPlaceholder("Search").fill("tape");
            page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Search")).click();
        });
        List<String> matchingProducts = page.getByTestId("product-name").allInnerTexts();
        org.assertj.core.api.Assertions.assertThat(matchingProducts)
                .contains("Tape Measure 7.5m", "Measuring Tape", "Tape Measure 5m");
    }

    @DisplayName("With Page Objects")
    @Test
    void whenSearchingByKeyword() {
        SearchComponent searchComponent = new SearchComponent(page);
        ProductList productList = new ProductList(page);

        searchComponent.searchBy("tape");
        List<String> matchingProducts = productList.getProductNames();

        Assertions.assertThat(matchingProducts)
                .contains("Tape Measure 7.5m", "Measuring Tape", "Tape Measure 5m");
    }

    @Test
    @DisplayName("When there are no matching results")
    void whenThereIsNoMatchingProduct() {
        SearchComponent searchComponent = new SearchComponent(page);
        ProductList productList = new ProductList(page);

        searchComponent.searchBy("tape123");
        List<String> matchingProducts = productList.getProductNames();
        Assertions.assertThat(matchingProducts).isEmpty();

        Assertions.assertThat(productList.getSearchCompletedMessage()).contains("There are no products found.");
    }

    @Test
    @DisplayName("When the user clears a previous search results")
    void clearingTheSearchResults() {
        SearchComponent searchComponent = new SearchComponent(page);
        ProductList productList = new ProductList(page);

        searchComponent.searchBy("saw");

        var matchingFilteredProducts = productList.getProductNames();
        Assertions.assertThat(matchingFilteredProducts).hasSize(2);

        searchComponent.clearSearch();
        var matchingProducts = productList.getProductNames();
        Assertions.assertThat(matchingProducts).hasSize(9);
    }
}

