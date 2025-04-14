package org.sashiba.login;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sashiba.domain.User;
import org.sashiba.fixtures.PlaywrightTestCase;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class LoginWithRegisteredUserTest extends PlaywrightTestCase {


    @DisplayName("Should be able to login with registered user")
    @Test
    void shouldLoginWithRegisteredUser() {
        //register a user via api
        User user = User.randomUser();
        UserAPIClient apiClient = new UserAPIClient(page);
        apiClient.registerUser(user);

        //login via login page
        LoginPage loginPage = new LoginPage(page);
        loginPage.open();
        loginPage.login(user);

        //check that we are on the right account page
        assertThat(loginPage.title()).isEqualTo("My account");
    }

    @Test
    @DisplayName("Should reject a user if they provide a wrong password")
    void shouldRejectUserWithInvalidPassword() {
        User user = User.randomUser();
        UserAPIClient apiClient = new UserAPIClient(page);
        apiClient.registerUser(user);

        //login via login page
        LoginPage loginPage = new LoginPage(page);
        loginPage.open();
        loginPage.login(user.withPassword("fake123"));

        //check that we are on the right account page
        assertThat(loginPage.getLoginErrorMessage()).isEqualTo("Invalid email or password");
    }
}
