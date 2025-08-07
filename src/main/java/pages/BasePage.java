package pages;

import core.WaitHelper;
import org.openqa.selenium.WebDriver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Abstract base class for all page objects
 * Contains common functionality shared across all platforms
 */
public abstract class BasePage {
    protected static final Logger logger = LogManager.getLogger(BasePage.class);
    protected WebDriver driver;
    protected WaitHelper waitHelper;

    /**
     * Constructor - child classes must call this
     * @param driver WebDriver instance
     */
    protected BasePage(WebDriver driver) {
        this.driver = driver;
        this.waitHelper = new WaitHelper(driver);
    }

    /**
     * Get page title
     * @return current page title
     */
    public String getPageTitle() {
        return driver.getTitle();
    }

    /**
     * Get current URL
     * @return current page URL
     */
    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    /**
     * Check if page is loaded (to be implemented by child classes)
     * @return true if page is loaded
     */
    public abstract boolean isPageLoaded();
}