package org.sashiba.login;

import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.RequestOptions;
import org.sashiba.domain.User;

public class UserAPIClient {
    private final Page page;

    private static final String REGISTER_USER = "https://api.practicesoftwaretesting.com/users/register";

    public UserAPIClient(Page page) {
        this.page = page;
    }

    public void registerUser(User user) {
        APIResponse response = page.request().post(REGISTER_USER, RequestOptions.create()
                .setHeader("Content-Type", "application/json")
                .setHeader("Accept", "application/json")
                .setData(user));

        if (response.status() != 201) {
            throw new IllegalArgumentException("Could not create user: "+ response.text());
        }
    }
}
