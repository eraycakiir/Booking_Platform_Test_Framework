package pages.web;

import interfaces.ILoginPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Web implementation of Login Page for Booking.com
 * Uses Selenium WebDriver for web automation
 */
public class WebLoginPage extends WebBasePage implements ILoginPage {
    private static final Logger logger = LogManager.getLogger(WebLoginPage.class);

    // Locators
    private final By usernameFieldLocator = By.id("username");
    private final By continueWithEmailButton = By.xpath(
            "//button[.//span[normalize-space(text())='Continue with email']]|"
            + "//span[normalize-space(text())='Continue with email']/ancestor::button[1]");
    private final By multiCodeInputAny = By.cssSelector("input[name^='code_']");
    private final By singleCodeFieldLocator = By.cssSelector("input[name='code'], input[name='otp'], input[data-testid='verification-code']");
    private final By verifyEmailButton = By.xpath(
            "//button[.//span[normalize-space(text())='Verify email']]|"
            + "//span[normalize-space(text())='Verify email']/ancestor::button[1]");
    private final By accountHeaderAvatar = By.cssSelector("button[aria-label='Account menu'], [data-testid='header-profile']");

    // Constructor
    public WebLoginPage() {
        super();
        logger.info("WebLoginPage initialized");
    }

    @Override
    public boolean isPageLoaded() {
        try {
            waitHelper.waitForElementVisible(usernameFieldLocator, 10);
            return true;
        } catch (Exception e) {
            logger.warn("WebLoginPage not loaded: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public void clickSignInOrRegister() {
        // Not used on Web. The flow lands directly on the email page.
        logger.info("Web platform - Sign in or register step not needed");
    }

    @Override
    public void clickContinueWithEmail() {
        try {
            WebElement btn = waitHelper.waitForElementClickable(continueWithEmailButton, 10);
            attachScreenshot("web_before_continue_email");
            btn.click();
            logger.info("Clicked 'Continue with email' (localized)");
            attachScreenshot("web_after_continue_email");
        } catch (Exception e) {
            logger.warn("Continue button not found/clickable: {}", e.getMessage());
        }
    }

    @Override
    public void enterEmail(String email) {
        try {
            WebElement usernameField = waitHelper.waitForElementVisible(usernameFieldLocator, 10);
            usernameField.clear();
            attachScreenshot("web_before_type_email");
            usernameField.sendKeys(email);
            logger.info("Entered email: {}", email);
            attachScreenshot("web_after_type_email");
        } catch (Exception e) {
            logger.error("Failed to enter email: {}", e.getMessage());
            throw new RuntimeException("Email field not accessible", e);
        }
    }

    @Override
    public void enterVerificationCode(String code) {
        try {
            waitHelper.waitForElementVisible(multiCodeInputAny, 10);
            char[] chars = code.toCharArray();
            int len = Math.min(chars.length, 6);
            for (int i = 0; i < len; i++) {
                By field = By.cssSelector("input[name='code_" + i + "']");
                WebElement input = waitHelper.waitForElementVisible(field, 10);
                input.clear();
                attachScreenshot("web_before_type_code_" + i);
                input.sendKeys(String.valueOf(chars[i]));
                attachScreenshot("web_after_type_code_" + i);
            }
            logger.info("Entered verification code into 6 separate inputs");
        } catch (Exception e) {
            logger.error("Failed to enter verification code (6 inputs): {}", e.getMessage());
            throw new RuntimeException("Verification code field not accessible", e);
        }

        // Click Verify button if present
        try {
            WebElement verifyBtn = waitHelper.waitForElementClickable(verifyEmailButton, 5);
            attachScreenshot("web_before_click_verify");
            verifyBtn.click();
            logger.info("Clicked Verify email button");
            attachScreenshot("web_after_click_verify");
        } catch (Exception ignored) {
        }

        // Login confirmation: wait for header avatar visibility
        try {
            waitHelper.waitForElementVisible(accountHeaderAvatar, 15);
            logger.info("Login confirmed: account avatar visible");
            attachScreenshot("web_login_confirmed");
        } catch (Exception e) {
            logger.warn("Account avatar not visible after verification: {}", e.getMessage());
        }
    }
}