package org.sashiba.toolshop.catalog.pageobjects;

import com.microsoft.playwright.Page;
import io.qameta.allure.Step;

public class NavBar {
    private final Page page;

    public NavBar(Page page) {
        this.page = page;
    }

    @Step("Open Cart Page")
    public void openCart() {
        page.getByTestId("nav-cart").click();
    }

    @Step("Open Home Page")
    public void openHomePage() {
        page.navigate("https://practicesoftwaretesting.com");
    }

    @Step("Open Contact Page")
    public void openContactPage() {
        page.navigate("https://practicesoftwaretesting.com/contact");
    }
}
