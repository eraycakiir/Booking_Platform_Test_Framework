package pages.ios;

import interfaces.ILoginPage;
import pages.ios.IOSBasePage;
import io.appium.java_client.AppiumBy;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * iOS implementation of Login Page for Booking.com
 * Uses Appium for iOS mobile automation
 */
public class IOSLoginPage extends IOSBasePage implements ILoginPage {
    private static final Logger logger = LogManager.getLogger(IOSLoginPage.class);

    // Locators
    private final By signInOrRegisterLocator = AppiumBy.accessibilityId("Sign in or register");
    private final By continueWithEmailLocator = AppiumBy.accessibilityId("ai_email_authentication_button");
    private final By emailFieldLocator = AppiumBy.accessibilityId("Email address");

    // Constructor
    public IOSLoginPage() {
        super();
        logger.info("IOSLoginPage initialized");
    }

    @Override
    public boolean isPageLoaded() {
        try {
            waitHelper.waitForElementVisible(signInOrRegisterLocator, 15);
            return true;
        } catch (Exception e) {
            // Alternatif olarak email field'ını kontrol et
            try {
                waitHelper.waitForElementVisible(emailFieldLocator, 5);
                return true;
            } catch (Exception e2) {
                logger.warn("IOSLoginPage not loaded: {}", e.getMessage());
                return false;
            }
        }
    }

    @Override
    public void clickSignInOrRegister() {
        try {
            WebElement signInOrRegisterButton = waitHelper.waitForElementClickable(signInOrRegisterLocator, 15);
            signInOrRegisterButton.click();
            logger.info("Clicked 'Sign in or register' button");
        } catch (Exception e) {
            logger.error("Failed to click 'Sign in or register': {}", e.getMessage());
            throw new RuntimeException("Sign in or register button not clickable", e);
        }
    }

    @Override
    public void clickContinueWithEmail() {
        try {
            WebElement continueWithEmailButton = waitHelper.waitForElementClickable(continueWithEmailLocator, 15);
            continueWithEmailButton.click();
            logger.info("Clicked 'Continue with email' button");
        } catch (Exception e) {
            logger.error("Failed to click 'Continue with email': {}", e.getMessage());
            throw new RuntimeException("Continue with email button not clickable", e);
        }
    }

    @Override
    public void enterEmail(String email) {
        try {
            WebElement emailField = waitHelper.waitForElementVisible(emailFieldLocator, 15);
            emailField.clear();
            emailField.sendKeys(email);
            logger.info("Entered email: {}", email);
        } catch (Exception e) {
            logger.error("Failed to enter email: {}", e.getMessage());
            throw new RuntimeException("Email field not accessible", e);
        }
    }
}