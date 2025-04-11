package org.sashiba.login;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.microsoft.playwright.APIRequest;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.junit.UsePlaywright;
import com.microsoft.playwright.options.RequestOptions;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sashiba.domain.User;


@UsePlaywright
public class RegisterUserAPITest {

    private APIRequestContext requestContext;
    private Gson gson = new Gson();

    @BeforeEach
    void setup(Playwright playwright) {
        requestContext = playwright.request().newContext(
                new APIRequest.NewContextOptions()
                        .setBaseURL("https://api.practicesoftwaretesting.com"));
    }

    @AfterEach
    void tearDown() {
        if (requestContext != null) {
            requestContext.dispose();
        }
    }

    @Test
    void shouldRegisterUser() {
        User validUser = User.randomUser();

        APIResponse response = requestContext.post("/users/register",
                RequestOptions.create()
                        .setHeader("Content-Type", "application/json")
                        .setData(validUser));

        String responseBody = response.text();
        User createdUser = gson.fromJson(responseBody, User.class);
        JsonObject responseObject = gson.fromJson(responseBody, JsonObject.class);

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(response.status())
                    .as("Register should return 201 CREATED")
                    .isEqualTo(201);
            softly.assertThat(createdUser)
                    .as("Created user should match the specified user w/o password")
                    .isEqualTo(validUser.withPassword(null));
            softly.assertThat(responseObject.get("id").getAsString())
                    .as("Registered user should have an id")
                    .isNotEmpty();
            softly.assertThat(responseObject.has("password"))
                    .as("No password should be returned")
                    .isFalse();
            softly.assertThat(response.headers().get("content-type"))
                    .contains("application/json");
        });
    }

    @Test
    void firstNameIsMandatory() {
        User userWithNoName = User.randomUser().withFirstName(null).withLastName(null);

        APIResponse response = requestContext.post("/users/register",
                RequestOptions.create()
                        .setHeader("Content-Type", "application/json")
                        .setData(userWithNoName));
        JsonObject responseObject = gson.fromJson(response.text(), JsonObject.class);

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(response.status())
                    .as("Register should return 422")
                    .isEqualTo(422);
            softly.assertThat(responseObject.has("first_name"))
                    .isTrue();
            softly.assertThat(responseObject.has("last_name"))
                    .isTrue();

            String errorMessage = responseObject.get("first_name").getAsString();
            softly.assertThat(errorMessage)
                    .isEqualTo("The first name field is required.");

            String errorMessageLastName = responseObject.get("last_name").getAsString();
            softly.assertThat(errorMessageLastName)
                    .isEqualTo("The last name field is required.");
        });
    }
}
