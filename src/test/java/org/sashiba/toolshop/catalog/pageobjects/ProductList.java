package org.sashiba.toolshop.catalog.pageobjects;

import com.microsoft.playwright.Page;
import io.qameta.allure.Step;
import org.sashiba.fixtures.ScreenshotManager;
import org.sashiba.toolshop.catalog.ProductSummary;

import java.util.List;

public class ProductList {
    private final Page page;

    public ProductList(Page page) {
        this.page = page;
    }

    public List<String> getProductNames() {
        return page.getByTestId("product-name").allInnerTexts();
    }

    public List<ProductSummary> getProductSummaries() {
        return page.locator(".card").all()
                .stream().map(productCard -> {
                    String productName = productCard.getByTestId("product-name").textContent().strip();
                    String productPrice = productCard.getByTestId("product-price").textContent().strip();
                    return new ProductSummary(productName, productPrice);
                }).toList();
    }

    @Step("View product details")
    public void viewProductDetails(String product) {
        ScreenshotManager.takeScreenshot(page, "View product details for " + product);
        page.locator(".card").getByText(product).click();
    }

    public String getSearchCompletedMessage() {
        return page.getByTestId("no-results").innerText();
    }
}
