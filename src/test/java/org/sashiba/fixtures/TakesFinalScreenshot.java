package org.sashiba.fixtures;

import com.microsoft.playwright.Page;
import org.junit.jupiter.api.AfterEach;

public interface TakesFinalScreenshot {

    @AfterEach
    default void takeFinalScreenshot(Page page) {
        System.out.println("taking final screenshot");
        ScreenshotManager.takeScreenshot(page, "Final screenshot");
    }
}
