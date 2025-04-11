Mastering Modern Test Automation With Playwright In Java - Udemy

## Section 3:

### Playwright - Creates browser instances

chromium(), firefox(), webkit() - safari, request() - API

### Browser - Manages a specific browser instance

launch()

LaunchOptions - setArgs(), setEnv(), setDownloadPath(), setHeadless(), setProxy(), setSlowMo()

### BrowserContext - Isolated sessions in a browser instance. Manages cookies, storage and session data

Isolation - Each context runs independently, with separate cookies, storage and sessions

Efficiency - Multiple contexts share one browser instance, minimizing resource use

Multi-user Testing - Simulate different users or sessions withing a single test.

### Page - Interact with a single tab withing a browser instance.

newPage() - new BrowserContext (new browser)

browserContext = browser.newContext();
page = browserContext.newPage(); - same browser

## Section 4:

### Locators

By Role, By Text Content, By Labels and Placeholders, By Test id, By CSS, By Xpath

ARIA - Accessible Rich Internet Applications

AriaRole.BUTTON

page.locator("locator").nth(1);

page.locator("locator").first(); .last()

List<String> list = page.locator("locator").allTextContents();

## Section 5:

## Section 6:

## Section 7:
page.waitForLoadState(LoadState.NETWORKIDLE);

page.waitForCondition(() -> );

page.waitForResponse("**/products?sort=**", () -> {
page.getByTestId("sort").selectOption(new SelectOption().setValue("price,desc"));
});

## Section 8:
