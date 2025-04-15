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

`page.route("**/products/search?q=Test123", route -> {
route.fulfill(new Route.FulfillOptions()
.setBody(MockSearchResponses.RESPONSE_WITH_NO_ENTRIES)
.setStatus(200));
});`

## Section 9:

` APIRequestContext apiRequestContext = playwright.request().newContext(
                    new APIRequest.NewContextOptions()
                            .setBaseURL("https://api.practicesoftwaretesting.com")
                            .setExtraHTTPHeaders(new HashMap<>() {{
                                put("Accept", "application/json");
                            }})
            );`

## Section 10:

## Section 11:

Page object:

- Design pattern that creates an organized abstraction of web app
- Represents UI elements and interactions as reausable components
- Separates HOW to interact with elements from WHAT the tests verify
- Models user interactions rather than pages or screens

## Section 12:

## Section 13:

Tracing

```
@BeforeEach
void setupTrace(BrowserContext browserContext) {
browserContext.tracing().start(
                new Tracing.StartOptions()
                        .setScreenshots(true)
                        .setSnapshots(true)
                        .setSources(true)
        );
}
@AfterEach
void recordTrace(BrowserContext browserContext) {
    browserContext.tracing().stop(
            new Tracing.StopOptions()
                    .setPath(Paths.get("trace.zip"))//should be unique everytime
    );
}

npx playwright show-trace trace.zip
```

## Section 14:

junit-platform.properties

ThreadLocal<Playwright> - variable unique in a thread. Will create new for every thread

junit.jupiter.execution.parallel.mode.default=same_thread - for @UsePlaywright
classes are running parallel, tests sequential

## Section 15:

https://allurereport.org/

mvn clean verify

mvn io.qameta.allure:allure-maven:serve - spin web server