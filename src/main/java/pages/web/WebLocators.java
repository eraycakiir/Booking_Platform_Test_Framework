package pages.web;

import org.openqa.selenium.By;

public final class WebLocators {
    private WebLocators() {}

    public static final By SIGN_IN_BUTTON = By.cssSelector("[data-testid='header-sign-in-button']");

    public static final By USERNAME_FIELD = By.id("username");

    public static final By CONTINUE_WITH_EMAIL = By.xpath(
            "//button[.//span[normalize-space(text())='Continue with email']]|" +
            "//span[normalize-space(text())='Continue with email']/ancestor::button[1]");

    public static final By MULTI_CODE_ANY = By.cssSelector("input[name^='code_']");
    public static final By VERIFY_EMAIL_BUTTON = By.xpath(
            "//button[.//span[normalize-space(text())='Verify email']]|" +
            "//span[normalize-space(text())='Verify email']/ancestor::button[1]");

    public static final By ACCOUNT_AVATAR = By.cssSelector("button[aria-label='Account menu'], [data-testid='header-profile']");
}


