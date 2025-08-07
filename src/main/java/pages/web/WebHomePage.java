package pages.web;

import interfaces.IHomePage;
import pages.web.WebBasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Web implementation of Home Page for Booking.com
 * Uses Selenium WebDriver for web automation
 */
public class WebHomePage extends WebBasePage implements IHomePage {
    private static final Logger logger = LogManager.getLogger(WebHomePage.class);

    // Locators
    private final By signInButtonLocator = By.cssSelector("[data-testid='header-sign-in-button']");

    // Constructor
    public WebHomePage() {
        super();
        logger.info("WebHomePage initialized");
    }

    @Override
    public boolean isPageLoaded() {
        try {
            waitHelper.waitForElementVisible(signInButtonLocator, 10);
            return true;
        } catch (Exception e) {
            logger.warn("WebHomePage not loaded: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public void clickSignInButton() {
        try {
            WebElement signInButton = waitHelper.waitForElementClickable(signInButtonLocator, 10);
            signInButton.click();
            logger.info("Clicked sign in button");
        } catch (Exception e) {
            logger.error("Failed to click sign in button: {}", e.getMessage());
            throw new RuntimeException("Sign in button not clickable", e);
        }
    }
}