package org.sashiba.toolshop.cucumber.stepdefenitions;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.Before;
import io.cucumber.java.DataTableType;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.assertj.core.api.Assertions;
import org.sashiba.toolshop.catalog.ProductSummary;
import org.sashiba.toolshop.catalog.pageobjects.NavBar;
import org.sashiba.toolshop.catalog.pageobjects.ProductList;
import org.sashiba.toolshop.catalog.pageobjects.SearchComponent;

import java.util.List;
import java.util.Map;

public class ProductCatalogStepDefinitions {
    NavBar navBar;
    SearchComponent searchComponent;
    ProductList productList;

    @Before
    public void setupPageObjects() {
        System.out.println("setupPageObjects");
        navBar = new NavBar(PlaywrightCucumberFixtures.getPage());
        searchComponent = new SearchComponent(PlaywrightCucumberFixtures.getPage());
        productList = new ProductList(PlaywrightCucumberFixtures.getPage());
    }

    @Given("Sally is on the home page")
    public void sallyIsOnTheHomePage() {
        navBar.openHomePage();
    }

    @When("she searches for {string}")
    public void sheSearchesFor(String searchTerm) {
        searchComponent.searchBy(searchTerm);
    }

    @Then("the {string} product should be displayed")
    public void theProductShouldBeDisplayed(String productName) {
        var matchingProducts = productList.getProductNames();

        Assertions.assertThat(matchingProducts).contains(productName);
    }

    @Then("the following products should be displayed as list:")
    public void theFollowingProductsShouldBeDisplayedAsList(List<String> expectedProducts) {
        var matchingProducts = productList.getProductNames();

        Assertions.assertThat(matchingProducts).containsAll(expectedProducts);
    }

    @Then("the following products should be displayed Not Used:")
    public void theFollowingProductsShouldBeDisplayedNotUsed(DataTable expectedProducts) {
        var matchingProducts = productList.getProductSummaries();
        List<Map<String, String>> expectedProductsData = expectedProducts.asMaps();
        List<ProductSummary> expectedProductSummaries = expectedProductsData.stream()
                .map(productData -> new ProductSummary(
                        productData.get("Product"),
                        productData.get("Price")))
                .toList();

        Assertions.assertThat(matchingProducts).containsExactlyInAnyOrderElementsOf(expectedProductSummaries);
    }

    @DataTableType
    public ProductSummary productSummaryRow(Map<String, String> productData) {
        return new ProductSummary(productData.get("Product"), productData.get("Price"));
    }

    @Then("the following products should be displayed:")
    public void theFollowingProductsShouldBeDisplayed(List<ProductSummary> expectedProductSummaries) {
        var matchingProducts = productList.getProductSummaries();
        Assertions.assertThat(matchingProducts).containsExactlyInAnyOrderElementsOf(expectedProductSummaries);
    }
}
