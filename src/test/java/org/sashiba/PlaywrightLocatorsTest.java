package org.sashiba;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.LoadState;
import org.junit.jupiter.api.*;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class PlaywrightLocatorsTest {
    private static final String URL = "https://practicesoftwaretesting.com";
    protected static Playwright playwright;
    protected static Browser browser;
    protected static BrowserContext browserContext;
    private Page page;

    @BeforeAll
    static void setUpBrowser() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(
                new BrowserType.LaunchOptions().setHeadless(false)
                        .setArgs(Arrays.asList("--no-sandbox", "--disable-extensions", "--disable-gpu"))
        );
    }

    @BeforeEach
    void setUp() {
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

    @DisplayName("Locating elements by Text")
    @Nested
    class LocatingElementsByText {
        @BeforeEach
        void openTheCatalogPage() {
            openPage();
        }

        @DisplayName("Locating an element by text contents")
        @Test
        void byText() {
            page.getByText("Bolt Cutters").click();

            assertThat(page.getByText("MightyCraft Hardware")).isVisible();
        }

        @DisplayName("Using alt text")
        @Test
        void byAltText() {
            page.getByAltText("Combination Pliers").click();

            assertThat(page.getByText("ForgeFlex Tools")).isVisible();
        }

        @DisplayName("Using title")
        @Test
        void byTitle() {
            page.getByAltText("Combination Pliers").click();
            page.getByTitle("Practice Software Testing - Toolshop").click();
        }
    }

    @DisplayName("Locating elements by Labels and Placeholders")
    @Nested
    class LocatingElementsByLabelsPlaceHolders {
        @DisplayName("Using a label")
        @Test
        void byLabel() {
            page.navigate("https://practicesoftwaretesting.com/contact");
            page.getByLabel("First name").fill("Obi-Want");
            page.getByLabel("Last name").fill("Kenobi");
            page.getByLabel("Email address").fill("test@test.com");
            page.getByLabel("Subject").selectOption("Webmaster");
            page.getByLabel("Message *").fill("Hello world");
            page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Send"));

            assertThat(page.getByLabel("First name")).hasValue("Obi-Want");
            assertThat(page.getByLabel("Last name")).hasValue("Kenobi");
            assertThat(page.getByLabel("Email address")).hasValue("test@test.com");
            assertThat(page.getByLabel("Message *")).hasValue("Hello world");
        }

        @DisplayName("Using a placeholder text")
        @Test
        void byPlaceholder() {
            page.navigate("https://practicesoftwaretesting.com/contact");
            page.getByPlaceholder("Your first name").fill("Obi-Wan");
            page.getByPlaceholder("Your last name").fill("Kenobi");
            page.getByPlaceholder("Your email").fill("test@test.com");
            page.getByLabel("Subject").selectOption("Webmaster");
            page.getByLabel("Message *").fill("Hello world");
            page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Send"));

            assertThat(page.getByLabel("First name")).hasValue("Obi-Wan");
            assertThat(page.getByLabel("Last name")).hasValue("Kenobi");
            assertThat(page.getByLabel("Email address")).hasValue("test@test.com");
            assertThat(page.getByLabel("Message *")).hasValue("Hello world");
        }
    }

    @DisplayName("Locating elements by role")
    @Nested
    class LocatingElementsByRole {
        @BeforeEach
        void openTheCatalogPage() {
            openPage();
        }

        @DisplayName("Using the BUTTON role")
        @Test
        void byButton() {
            page.navigate("https://practicesoftwaretesting.com/contact");
            page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Send")).click();

            List<String> errorMessages = page.getByRole(AriaRole.ALERT).allTextContents();
            Assertions.assertTrue(!errorMessages.isEmpty());
        }

        @DisplayName("Using the HEADING role")
        @Test
        void byHeaderRole() {
            page.locator("#search-query").fill("Pliers");
            page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Search")).click();
            Locator searchHeading = page.getByRole(AriaRole.HEADING,
                    new Page.GetByRoleOptions().setName(Pattern.compile("Searched for:.*")));

            assertThat(searchHeading).isVisible();
            assertThat(searchHeading).hasText("Searched for: Pliers");
        }

        @DisplayName("Using the HEADING role and level")
        @Test
        void byHeaderRoleLevel() {
            List<String> headings = page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Pliers").setLevel(5)).allTextContents();
            org.assertj.core.api.Assertions.assertThat(headings).isNotEmpty();
        }

        @DisplayName("Identifying checkboxes")
        @Test
        void byCheckboxes() {
            playwright.selectors().setTestIdAttribute("data-test");

            page.getByLabel("Hammer").click();
            page.getByLabel("Chisels").click();
            page.getByLabel("Wrench").click();

            int selectedCheckboxCont = page.getByTestId("filters")
                    .getByRole(AriaRole.CHECKBOX, new Locator.GetByRoleOptions().setChecked(true)).count();
            org.assertj.core.api.Assertions.assertThat(selectedCheckboxCont).isEqualTo(3);

            List<String> selectedOptions =
                    page.getByTestId("filters").
                            getByRole(AriaRole.CHECKBOX,
                                    new Locator.GetByRoleOptions().setChecked(true))
                            .all()
                            .stream()
                            .map(Locator::inputValue)
                            .toList();
            org.assertj.core.api.Assertions.assertThat(selectedOptions).hasSize(3);
        }
    }

    @DisplayName("Locating elements by Test id")
    @Nested
    class LocatingElementsByTestId {
        @BeforeEach
        void openTheCatalogPage() {
            openPage();
            playwright.selectors().setTestIdAttribute("data-test"); //set default test id attribute for getByTestId
        }


        @DisplayName("Using a custom data-test field")
        @Test
        void byTestId() {
            //playwright.selectors().setTestIdAttribute("data-test");

            page.getByTestId("search-query").fill("Pliers");
            page.getByTestId("search-submit").click();

            assertThat(page.locator(".card")).hasCount(4);
        }
    }

    @DisplayName("Locating elements using CSS")
    @Nested
    class LocatingElementsUsingCSS {
        @BeforeEach
        void openContactPage() {
            page.navigate("https://practicesoftwaretesting.com/contact");
        }

        @DisplayName("By Id")
        @Test
        void byId() {
            Locator firstName = page.locator("#first_name");
            firstName.fill("Sarrah");

            assertThat(firstName).hasValue("Sarrah");
        }

        @DisplayName("By CSS class")
        @Test
        void byCSSClass() {
            page.locator("#first_name").fill("Sarrah");
            page.locator(".btnSubmit").click();

            List<String> allertMessages = page.locator(".alert").allTextContents();
            Assertions.assertTrue(!allertMessages.isEmpty());
        }

        @DisplayName("By attribute")
        @Test
        void byAttribute() {
            page.locator("[placeholder='Your last name *']").fill("Smith");
            page.locator("input[placeholder='Your first name *']").fill("Sarrah");

            assertThat(page.locator("#first_name")).hasValue("Sarrah");
            assertThat(page.locator("#last_name")).hasValue("Smith");
        }
    }

    @DisplayName("Locating elements by text using CSS")
    @Nested
    class LocatingElementsByTextUsingCSS {
        @BeforeEach
        void openContactPage() {
            page.navigate("https://practicesoftwaretesting.com/contact");
        }

        // :has-text matches any element containing specified text somewhere inside.
        @DisplayName("Using :has-text")
        @Test
        void locateTheSendButtonByText() {
            page.locator("#first_name").fill("Sarah-Jane");
            page.locator("#last_name").fill("Smith");
            page.locator("input:has-text('Send')").click();
        }

        // :text matches the smallest element containing specified text.
        @DisplayName("Using :text")
        @Test
        void locateAProductItemByText() {
            page.locator(".navbar :text('Home')").click();
            page.locator(".card :text('Bolt')").click();
            assertThat(page.locator("[data-test=product-name]")).hasText("Bolt Cutters");
        }

        // Exact matches
        @DisplayName("Using :text-is")
        @Test
        void locateAProductItemByTextIs() {
            page.locator(".navbar :text('Home')").click();
            page.locator(".card :text-is('Bolt Cutters')").click();
            assertThat(page.locator("[data-test=product-name]")).hasText("Bolt Cutters");
        }

        // matching with regular expressions
        @DisplayName("Using :text-matches")
        @Test
        void locateAProductItemByTextMatches() {
            page.locator(".navbar :text('Home')").click();
            page.locator(".card :text-matches('Bolt \\\\w+')").click();
            assertThat(page.locator("[data-test=product-name]")).hasText("Bolt Cutters");
        }
    }

    @DisplayName("Locating visible elements")
    @Nested
    class LocatingVisibleElements {
        @BeforeEach
        void openContactPage() {
            openPage();
        }

        @DisplayName("Finding visible and invisible elements")
        @Test
        void locateVisibleAndInvisibleItems() {
            int dropdownItems = page.locator(".dropdown-item").count();
            Assertions.assertTrue(dropdownItems > 0);
        }

        @DisplayName("Finding only visible elements")
        @Test
        void locateVisibleItems() {
            int dropdownItems = page.locator(".dropdown-item:visible").count();
            Assertions.assertTrue(dropdownItems == 0);
        }
    }

    @DisplayName("Nested locators")
    @Nested
    class NestedLocators {
        @BeforeAll
        static void setTestId() {
            playwright.selectors().setTestIdAttribute("data-test");
        }

        @DisplayName("Using roles")
        @Test
        void locatingAMenuItemUsingRoles() {
            openPage();

            page.getByRole(AriaRole.MENUBAR, new Page.GetByRoleOptions().setName("Main Menu"))
                    .getByRole(AriaRole.MENUITEM, new Locator.GetByRoleOptions().setName("Home"))
                    .click();

        }

        @DisplayName("Using roles with other strategies")
        @Test
        void locatingAMenuItemUsingRolesAndOtherStrategies() {
            openPage();
            page.getByRole(AriaRole.MENUBAR, new Page.GetByRoleOptions().setName("Main Menu"))
                    .getByText("Home")
                    .click();
        }

        @DisplayName("filtering locators by text")
        @Test
        void filteringMenuItems() {
            openPage();
            page.getByRole(AriaRole.MENUBAR, new Page.GetByRoleOptions().setName("Main Menu"))
                    .getByText("Categories")
                    .click();

            page.getByRole(AriaRole.MENUBAR, new Page.GetByRoleOptions().setName("Main Menu"))
                    .getByText("Power Tools")
                    .click();

            page.waitForCondition(() -> page.getByTestId("product-name").count() > 0);

            List<String> allProducts = page.getByTestId("product-name")
                    .filter(new Locator.FilterOptions().setHasText("Sander"))
                    .allTextContents();

            org.assertj.core.api.Assertions.assertThat(allProducts).allMatch(name -> name.contains("Sander"));
        }

        @DisplayName("filtering locators by locator")
        @Test
        void filteringMenuItemsByLocator() {
            openPage();
            List<String> allProducts = page.locator(".card")
                    .filter(new Locator.FilterOptions().setHas(page.getByText("Out of stock")))
                    .getByTestId("product-name")
                    .allTextContents();

            org.assertj.core.api.Assertions.assertThat(allProducts).hasSize(1)
                    .allMatch(name -> name.contains("Long Nose Pliers"));
        }
    }

    private void openPage() {
        page.navigate(URL);
        page.waitForLoadState(LoadState.NETWORKIDLE);
    }
}
