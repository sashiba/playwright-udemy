package org.sashiba;

import com.microsoft.playwright.junit.UsePlaywright;

@UsePlaywright(HeadlessChromeOptions.class)
public class PlaywrightCollectionsTest {

//    @BeforeEach
//    void setUp(Page page) {
//        openPage(page);
//    }
//
//    private void openPage(Page page) {
//        page.navigate("https://practicesoftwaretesting.com");
//        page.waitForCondition(() -> page.getByTestId("product-name").count() > 0);
//    }
//
//    @DisplayName("Counting items in a list")
//    @Test
//    void countingItemsOnThePage(Page page) {
//
//        int itemsOnThePage = page.locator(".card").count();
//
//        Assertions.assertThat(itemsOnThePage).isGreaterThan(0);
//    }
//
//    @DisplayName("Finding the first matching item")
//    @Test
//    void findingTheFirstMatchingItem(Page page) {
//
//        page.locator(".card").first().click();
//
//    }
//
//    @DisplayName("Finding the nth matching item")
//    @Test
//    void findingNthMatchingItem(Page page) {
//
//        page.locator(".card").nth(2).click();
//
//    }
//
//    @DisplayName("Finding the last matching item")
//    @Test
//    void findingLastMatchingItem(Page page) {
//
//        page.locator(".card").last().click();
//
//    }
//
//    @DisplayName("Finding text in a list")
//    @Nested
//    class FindingTheTextInAList {
//
//        @DisplayName("and finding all the text values ")
//        @Test
//        void withAllTextContents(Page page) {
//
//            List<String> itemNames = page.getByTestId("product-name").allTextContents();
//
//
//            Assertions.assertThat(itemNames).contains(" Combination Pliers ",
//                    " Pliers ",
//                    " Bolt Cutters ",
//                    " Long Nose Pliers ",
//                    " Slip Joint Pliers ",
//                    " Claw Hammer with Shock Reduction Grip ",
//                    " Hammer ",
//                    " Claw Hammer ",
//                    " Thor Hammer ");
//        }
//
//        @DisplayName("and asserting with  hasText")
//        @Test
//        void withHasText(Page page) {
//            assertThat(page.getByTestId("product-name"))
//                    .hasText(new String[]{
//                            " Combination Pliers ",
//                            " Pliers ",
//                            " Bolt Cutters ",
//                            " Long Nose Pliers ",
//                            " Slip Joint Pliers ",
//                            " Claw Hammer with Shock Reduction Grip ",
//                            " Hammer ",
//                            " Claw Hammer ",
//                            " Thor Hammer "
//                    });
//        }
//    }

}