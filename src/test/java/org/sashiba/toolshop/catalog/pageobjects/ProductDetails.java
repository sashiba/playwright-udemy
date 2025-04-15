package org.sashiba.toolshop.catalog.pageobjects;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import io.qameta.allure.Step;
import org.sashiba.fixtures.ScreenshotManager;

public class ProductDetails {
    private final Page page;

    public ProductDetails(Page page) {
        this.page = page;
    }

    @Step("Increase quantity")
    public void increaseQuantityBy(int increment) {
        for (int i = 1; i <= increment; i++) {
            page.getByTestId("increase-quantity").click();
        }
        ScreenshotManager.takeScreenshot(page, "Quantity increase by");
    }

    @Step("Add to cart")
    public void addToCart() {
        page.waitForResponse(
                response -> response.url().contains("/carts") && response.request().method().equals("POST"), //wait for POST to /carts
                () -> {
                    page.getByText("Add to cart").click();
                    page.getByRole(AriaRole.ALERT).click();
                } //callback - code that triggers the request
        );
        ScreenshotManager.takeScreenshot(page, "Added to cart");
    }
}
