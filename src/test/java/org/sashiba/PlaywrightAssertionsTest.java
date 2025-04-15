package org.sashiba;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.LoadState;
import com.microsoft.playwright.options.SelectOption;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class PlaywrightAssertionsTest {
    protected static Playwright playwright;
    protected static Browser browser;
    protected static BrowserContext browserContext;
    protected Page page;

    @BeforeAll
    static void setupBrowser() {
        playwright = Playwright.create();
        playwright.selectors().setTestIdAttribute("data-test");

        browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
                .setHeadless(true)
                .setArgs(Arrays.asList("--no-sandbox", "--disable-extensions", "--disable-gpu")));
    }

    @BeforeEach
    void setup() {
        browserContext = browser.newContext();
        page = browserContext.newPage();
    }

    @AfterEach
    void closeContext() {
        browserContext.close();
    }

    @AfterAll
    static void tearDown() {
        browser.close();
        playwright.close();
    }

    @DisplayName("Making assertions about the contents of a field")
    @Nested
    class LocatingElementsUsingCSS {

        @BeforeEach
        void openContactPage() {
            page.navigate("https://practicesoftwaretesting.com/contact");
        }

        @DisplayName("Checking the value of a field")
        @Test
        void fieldValues() {
            Locator firstName = page.getByLabel("First name");
            firstName.fill("Sarrah");

            assertThat(firstName).hasValue("Sarrah");
            assertThat(firstName).not().isDisabled();
            assertThat(firstName).isVisible();
            assertThat(firstName).isEditable();
        }

        @DisplayName("Checking the value of a text field")
        @Test
        void textFieldValues() {
            var messageField = page.getByLabel("Message");
            messageField.fill("This is my message");

            assertThat(messageField).hasValue("This is my message");
        }

        @DisplayName("Checking the value of a dropdown field")
        @Test
        void dropdownFieldValues() {
            var subjectField = page.getByLabel("Subject");

            subjectField.selectOption("Warranty");
            assertThat(subjectField).hasValue("warranty");
        }
    }

    @DisplayName("Making assertions about data values")
    @Nested
    class MakingAssertionsAboutDataValues {

        @BeforeEach
        void openHomePage() {
            page.navigate("https://practicesoftwaretesting.com");
            page.waitForCondition(() -> page.getByTestId("product-name").count() > 0);
        }

        @Test
        void allProductPricesShouldBeCorrectValues() {
            List<Double> priceList = page.getByTestId("product-price").allTextContents()
                    .stream()
                    .map(p -> Double.parseDouble(p.replace("$", "")))
                    .toList();
            Assertions.assertThat(priceList)
                    .isNotEmpty()
                    .allMatch(p -> p > 0)
                    .doesNotContain(0.0)
                    .allMatch(p -> p < 1000)
                    .allSatisfy(p -> Assertions.assertThat(p).isGreaterThan(0).isLessThan(1000));
        }

        @Test
        void shouldSortInAlphabeticalOrder() {
            page.getByTestId("sort").selectOption(new SelectOption().setValue("name,asc"));
            page.waitForLoadState(LoadState.NETWORKIDLE);

            List<String> productNames = page.getByTestId("product-name").allInnerTexts();

            Assertions.assertThat(productNames).isSorted();
            Assertions.assertThat(productNames).isSortedAccordingTo(Comparator.naturalOrder());
            Assertions.assertThat(productNames).isSortedAccordingTo(String.CASE_INSENSITIVE_ORDER);
        }

        @Test
        void shouldSortInReverseAlphabeticalOrder() {
            page.getByTestId("sort").selectOption("Name (Z - A)");
            page.waitForLoadState(LoadState.NETWORKIDLE);

            List<String> productNames = page.getByTestId("product-name").allInnerTexts();

            Assertions.assertThat(productNames).isSortedAccordingTo(Comparator.reverseOrder());
        }
    }
}
