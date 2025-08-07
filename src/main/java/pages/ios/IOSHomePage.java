package pages.ios;

import interfaces.IHomePage;
import pages.ios.IOSBasePage;
import io.appium.java_client.AppiumBy;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * iOS implementation of Home Page for Booking.com
 * Uses Appium for iOS mobile automation
 */
public class IOSHomePage extends IOSBasePage implements IHomePage {
    private static final Logger logger = LogManager.getLogger(IOSHomePage.class);

    // Locators
    private final By signInButtonLocator = AppiumBy.accessibilityId("ai_tab_profile");

    // Constructor
    public IOSHomePage() {
        super();
        logger.info("IOSHomePage initialized");
    }

    @Override
    public boolean isPageLoaded() {
        try {
            waitHelper.waitForElementVisible(signInButtonLocator, 15);
            return true;
        } catch (Exception e) {
            logger.warn("IOSHomePage not loaded: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public void clickSignInButton() {
        try {
            WebElement signInButton = waitHelper.waitForElementClickable(signInButtonLocator, 15);
            signInButton.click();
            logger.info("Clicked iOS sign in button");
        } catch (Exception e) {
            logger.error("Failed to click iOS sign in button: {}", e.getMessage());
            throw new RuntimeException("iOS sign in button not clickable", e);
        }
    }
}