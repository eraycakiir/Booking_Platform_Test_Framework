package pages.web;

import interfaces.IHomePage;
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
    private final By cookieAcceptButton = By.cssSelector("#onetrust-accept-btn-handler, [data-testid='cookie-accept']");

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
            attachScreenshot("web_before_click_signin");
            signInButton.click();
            logger.info("Clicked sign in button");
            attachScreenshot("web_after_click_signin");
        } catch (Exception e) {
            logger.warn("Sign in click failed, trying to accept cookies if present: {}", e.getMessage());
            try {
                WebElement cookieBtn = waitHelper.waitForElementClickable(cookieAcceptButton, 3);
                cookieBtn.click();
                logger.info("Cookie banner accepted");
            } catch (Exception ignored) {}
            // Retry click once
            try {
                WebElement signInButton = waitHelper.waitForElementClickable(signInButtonLocator, 10);
                attachScreenshot("web_retry_before_click_signin");
                signInButton.click();
                logger.info("Clicked sign in button after cookie acceptance");
                attachScreenshot("web_retry_after_click_signin");
            } catch (Exception e2) {
                logger.error("Failed to click sign in button: {}", e2.getMessage());
                throw new RuntimeException("Sign in button not clickable", e2);
            }
        }
    }
}