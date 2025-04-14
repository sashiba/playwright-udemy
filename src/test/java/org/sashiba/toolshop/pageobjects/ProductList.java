package org.sashiba.toolshop.pageobjects;

import com.microsoft.playwright.Page;

import java.util.List;

public class ProductList {
    private final Page page;

    public ProductList(Page page) {
        this.page = page;
    }

    public List<String> getProductNames() {
        return page.getByTestId("product-name").allInnerTexts();
    }

    public void viewProductDetails(String product) {
        page.locator(".card").getByText(product).click();
    }

    public String getSearchCompletedMessage() {
        return page.getByTestId("no-results").innerText();
    }
}
