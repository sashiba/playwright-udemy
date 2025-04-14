package org.sashiba.toolshop.catalog.pageobjects;

import com.microsoft.playwright.Page;

import java.util.List;

public class CheckoutCart {
    private final Page page;

    public CheckoutCart(Page page) {
        this.page = page;
    }

    public List<CartLineItem> getLineItems() {
        page.locator("app-cart tbody tr").first().waitFor();
        return page.locator("app-cart tbody tr")
                .all()
                .stream()
                .map(row -> {
                    String title = row.getByTestId("product-title").innerText().strip().replaceAll("\u00A0", "");
                    int quantity = Integer.parseInt(row.getByTestId("product-quantity").inputValue());
                    Double price = price(row.getByTestId("product-price").innerText());
                    Double total = price(row.getByTestId("line-price").innerText());
                    return new CartLineItem(title, quantity, price, total);
                })
                .toList();
    }

    private static Double price(String value) {
        return Double.parseDouble(value.replace("$", ""));
    }
}
