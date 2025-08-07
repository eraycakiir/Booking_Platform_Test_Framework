package pages.web;

import pages.BasePage;
import core.DriverFactory;
import org.openqa.selenium.WebDriver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Base class for all Web page objects
 * Handles Web-specific functionality using Selenium WebDriver
 */
public abstract class WebBasePage extends BasePage {
    protected static final Logger logger = LogManager.getLogger(WebBasePage.class);

    /**
     * Constructor - gets WebDriver from DriverFactory
     */
    protected WebBasePage() {
        super(DriverFactory.getDriver());
        logger.debug("WebBasePage initialized");
    }

    /**
     * Navigate to a URL
     * @param url URL to navigate to
     */
    protected void navigateTo(String url) {
        driver.get(url);
        logger.info("Navigated to: {}", url);
    }

    /**
     * Refresh the current page
     */
    protected void refreshPage() {
        driver.navigate().refresh();
        logger.info("Page refreshed");
    }

    /**
     * Go back to previous page
     */
    protected void goBack() {
        driver.navigate().back();
        logger.info("Navigated back");
    }
}