package org.sashiba.toolshop.cucumber.stepdefenitions;

import com.microsoft.playwright.*;
import io.cucumber.java.After;
import io.cucumber.java.AfterAll;
import io.cucumber.java.Before;

import java.util.Arrays;

public class PlaywrightCucumberFixtures {
    private static final ThreadLocal<Playwright> playwright =
            ThreadLocal.withInitial(() -> {
                Playwright playwright = Playwright.create();
                playwright.selectors().setTestIdAttribute("data-test");
                return playwright;
            });
    private static final ThreadLocal<Browser> browser = ThreadLocal.withInitial(() ->
            playwright.get().chromium().launch(new BrowserType.LaunchOptions()
                    .setHeadless(true)
                    .setArgs(Arrays.asList("--no-sandbox", "--disable-extensions", "--disable-gpu")))
    );

    private static final ThreadLocal<BrowserContext> browserContext = new ThreadLocal<>();
    private static final ThreadLocal<Page> page = new ThreadLocal<>();

    @Before(order = 100)
    public void setUpBrowserContext() {
        System.out.println("setup browser context");
        browserContext.set(browser.get().newContext());
        page.set(browserContext.get().newPage());
    }

    @After
    public void closeContext() {
        System.out.println("closeContext");
        browserContext.get().close();
    }

    @AfterAll
    public static void tearDown() {
        System.out.println("tearDown");
        browser.get().close();
        browser.remove();

        playwright.get().close();
        playwright.remove();
    }

    public static Page getPage() {
        return page.get();
    }

    public static BrowserContext getBrowserContext() {
        return browserContext.get();
    }
}
