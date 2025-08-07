package pages.web;

import interfaces.ILoginPage;
import pages.web.WebBasePage;
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
        // Web'de direkt email sayfasına yönlendiriliyor, bu metod kullanılmıyor
        logger.info("Web platform - Sign in or register step not needed");
    }

    @Override
    public void clickContinueWithEmail() {
        // Web'de direkt email sayfasına yönlendiriliyor, bu metod kullanılmıyor
        logger.info("Web platform - Continue with email step not needed");
    }

    @Override
    public void enterEmail(String email) {
        try {
            WebElement usernameField = waitHelper.waitForElementVisible(usernameFieldLocator, 10);
            usernameField.clear();
            usernameField.sendKeys(email);
            logger.info("Entered email: {}", email);
        } catch (Exception e) {
            logger.error("Failed to enter email: {}", e.getMessage());
            throw new RuntimeException("Email field not accessible", e);
        }
    }
}