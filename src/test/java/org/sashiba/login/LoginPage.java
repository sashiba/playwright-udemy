package org.sashiba.login;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import org.sashiba.domain.User;

public class LoginPage {
    private final Page page;

    public LoginPage(Page page) {
        this.page = page;
    }

    public void open() {
        page.navigate("https://practicesoftwaretesting.com/auth/login");
    }

    public void login(User user) {
        page.getByPlaceholder("Your email").fill(user.email());
        page.getByPlaceholder("Your password").fill(user.password());

        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Login")).click();
    }

    public String title() {
        return page.getByTestId("page-title").textContent();
    }

    public String getLoginErrorMessage() {
        return page.getByTestId("login-error").textContent();
    }
}
