package org.sashiba.toolshop.catalog.pageobjects;

import com.microsoft.playwright.Page;
import io.qameta.allure.Step;
import org.sashiba.fixtures.ScreenshotManager;

public class NavBar {
    private final Page page;

    public NavBar(Page page) {
        this.page = page;
    }

    @Step("Open Cart Page")
    public void openCart() {
        page.getByTestId("nav-cart").click();
        ScreenshotManager.takeScreenshot(page, "Shopping cart");
    }

    @Step("Open Home Page")
    public void openHomePage() {
        page.navigate("https://practicesoftwaretesting.com");
        ScreenshotManager.takeScreenshot(page, "Home page");
    }

    @Step("Open Contact Page")
    public void openContactPage() {
        page.navigate("https://practicesoftwaretesting.com/contact");
        ScreenshotManager.takeScreenshot(page, "Contact page");
    }
}
