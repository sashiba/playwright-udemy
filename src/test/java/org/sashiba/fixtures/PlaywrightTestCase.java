package org.sashiba.fixtures;

import com.microsoft.playwright.*;
import io.qameta.allure.Allure;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.io.ByteArrayInputStream;
import java.util.Arrays;

public abstract class PlaywrightTestCase {

    protected BrowserContext browserContext;
    protected Page page;

    protected static ThreadLocal<Playwright> playwright =
            ThreadLocal.withInitial(() -> {
                Playwright playwright = Playwright.create();
                playwright.selectors().setTestIdAttribute("data-test");
                return playwright;
            });
    protected static ThreadLocal<Browser> browser = ThreadLocal.withInitial(() ->
            playwright.get().chromium().launch(new BrowserType.LaunchOptions()
                    .setHeadless(true)
                    .setArgs(Arrays.asList("--no-sandbox", "--disable-extensions", "--disable-gpu")))
    );

    @BeforeEach
    void setUpBrowserContext() {
        browserContext = browser.get().newContext();
        page = browserContext.newPage();
    }

    @AfterEach
    void closeContext() {
        takeScreenshot("End of test");
        browserContext.close();
    }

    @AfterAll
    static void tearDown() {
        browser.get().close();
        browser.remove();

        playwright.get().close();
        playwright.remove();
    }

    protected void takeScreenshot(String name) {
        var screenshot = page.screenshot(
                new Page.ScreenshotOptions()
                        .setFullPage(true)
        );
        Allure.addAttachment(name, new ByteArrayInputStream(screenshot));
    }

}
