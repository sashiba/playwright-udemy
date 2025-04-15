package org.sashiba.toolshop.cucumber.stepdefenitions;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.assertj.core.api.Assertions;
import org.sashiba.toolshop.catalog.pageobjects.NavBar;
import org.sashiba.toolshop.catalog.pageobjects.ProductList;
import org.sashiba.toolshop.catalog.pageobjects.SearchComponent;

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
    public void sally_is_on_the_home_page() {
        navBar.openHomePage();
    }

    @When("she searches for {string}")
    public void she_searches_for(String searchTerm) {
        searchComponent.searchBy(searchTerm);
    }

    @Then("the {string} product should be displayed")
    public void the_product_should_be_displayed(String productName) {
        var matchingProducts = productList.getProductNames();

        Assertions.assertThat(matchingProducts).contains(productName);
    }
}
