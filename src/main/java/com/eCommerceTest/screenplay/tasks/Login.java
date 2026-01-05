package com.eCommerceTest.screenplay.tasks;

import com.eCommerceTest.pages.LoginPage;
import com.eCommerceTest.screenplay.Actor;
import com.eCommerceTest.screenplay.Task;
import com.eCommerceTest.screenplay.abilities.BrowseTheWeb;
import com.eCommerceTest.utils.UiNavigator;

public class Login implements Task {
    private final String email;
    private final String password;

    public Login(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public static Login withCredentials(String email, String password) {
        return new Login(email, password);
    }

    @Override
    public void performAs(Actor actor) {
        BrowseTheWeb browse = actor.abilityTo(BrowseTheWeb.class);
        if (browse == null) {
            throw new RuntimeException("Actor cannot BrowseTheWeb");
        }

        // Use existing page objects to avoid duplication, bridging Screenplay and POM
        UiNavigator.openLogin(browse.getDriver());
        LoginPage loginPage = new LoginPage(browse.getDriver());
        loginPage.login(email, password);
    }
}
