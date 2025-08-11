package pages;

import core.WaitHelper;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import io.qameta.allure.Allure;
import java.io.ByteArrayInputStream;

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

    /**
     * Attach current screen as Allure attachment for step evidence
     */
    protected void attachScreenshot(String name) {
        try {
            byte[] bytes = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            Allure.addAttachment(name, new ByteArrayInputStream(bytes));
        } catch (Exception e) {
            logger.warn("Failed to capture screenshot for '{}': {}", name, e.getMessage());
        }
    }
}