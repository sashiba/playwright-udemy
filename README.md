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

mvn io.qameta.allure:allure-maven:report

Allure - Step, Story, Feature annotations

Screenshot - 
```
 var screenshot = page.screenshot(
                new Page.ScreenshotOptions()
                        .setFullPage(true)
        );
Allure.addAttachment(name, new ByteArrayInputStream(screenshot));
```
maven-surefire-plugin - for unit test - will fail with no report

maven-failsafe-plugin - integration-test phase - will generate report

<phase>post-integration-test</phase> - allure

## Section 16:

BDD Cucumber

Defining business needs, examples.

Gherkin - Business-readable specification + Automated test (Readable by business ppl, PO, QA, Dev)

Anatomy of Gherkin test:
- Initial state
- Action under test
- Expected outcome

```
Feature: Ordering smoothies
    Background:
        Given Mark is a member
    Scenario: Mark orders a cappucino
        Given Mark is a Premium member
        When he orders an "Almont milk cappucino"
        When he places the following order:
            | Product | Quantity |
            | Capp    | 2        |
            | Test    | 1        |
        Then his order should be placed in the prep queue
        
     Scenario: Mark gets daily health updates
        Given Mark is a Premium member
        And he is interested in the following areas:
            | Running |
            | Gym     |
        And he is interested in Dieting
        When he receives his morning health updates
        Then the articles should only include things about:
            | Running |
            | Gym     |
            | Dieting |
``` 

- Given - describes precondition
- When - describes the action under test
- Then - describes the expected outcome
- And - multiple Given/When/Then
- Scenario 
- Feature - combines Scenarios
  - Background - code run for every scenario
- Tables - can be input, expected, Data table
- Scenario Outline - using Examples table (like DataProvider)

```
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("/features")
@ConfigurationParameter(
        key = "cucumber.plugin",
        value = "io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm," +
                "pretty"
)
public class CucumberTestSuite {
}
```
@DataTableType

## Section 17:

Github Actions

https://github.com/sashiba/playwright-udemy/actions/runs/14488891820/job/40640545194

## Section 18:

npm run dev

https://github.com/sashiba/playwright-in-java-sample-code/tree/todomvccompleted

## Section 19:

docker run

docker build -t my-demo .

docker pull

docker run -d -p 8080:80 --name web-demo my-demo

docker ps - what processes are running

docker stop <name>

docker-compose build

docker-compose up

docker-compose up --exit-code-from tests

```declarative
version: '3.8'

services:
  webapp:
    build:
      context: ./react-todomvc
    ports:
      - "7002:7002"
    networks:
      - test-network
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:7002"]
      interval: 10s
      timeout: 5s
      retries: 5
    # Ensure the webapp service doesn't restart automatically
    restart: "no"

  tests:
    build:
      context: ./todomvc-acceptance-tests
    depends_on:
      webapp:
        condition: service_healthy
    networks:
      - test-network
    volumes:
      - ./todomvc-acceptance-tests/target:/tests/target
    # Ensure the webapp service doesn't restart automatically
    environment:
      - APP_HOST_URL=http://webapp:7002
    user: "${UID}:${GID}"
    restart: "no"

networks:
  test-network:
    driver: bridge

```