package tests.ui;

import core.ConfigReader;
import core.DriverFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.*;
import io.qameta.allure.Attachment;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

/**
 * Base test class with common setup and teardown methods
 */
public class BaseTest {
    protected static final Logger logger = LogManager.getLogger(BaseTest.class);

    @BeforeMethod(alwaysRun = true)
    @Parameters({"browser", "environment"})
    public void setUp(@Optional String browser, @Optional String environment) {
        logger.info("Starting test setup...");
        
        // Set system properties if provided via TestNG parameters
        if (browser != null && !browser.isEmpty()) {
            System.setProperty("browser", browser);
        }
        if (environment != null && !environment.isEmpty()) {
            System.setProperty("environment", environment);
        }

        // Initialize WebDriver
        DriverFactory.initializeDriver();
        
        // Navigate to base URL only for web platform
        String platform = ConfigReader.getPlatform();
        if ("web".equals(platform)) {
            String baseUrl = ConfigReader.getBaseUrl();
            DriverFactory.getDriver().get(baseUrl);
            logger.info("Navigated to: {}", baseUrl);
        } else {
            logger.info("iOS platform - App already launched, skipping URL navigation");
        }
        
        logger.info("Test setup completed successfully");
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        logger.info("Starting test cleanup...");
        
        // Take screenshot on failure (handled by listener as well)
        if (DriverFactory.getDriver() != null) {
            takeScreenshot();
        }
        
        // Quit WebDriver
        DriverFactory.quitDriver();
        logger.info("Test cleanup completed");
    }

    /**
     * Take screenshot and attach to Allure report
     */
    @Attachment(value = "Screenshot", type = "image/png")
    public byte[] takeScreenshot() {
        try {
            TakesScreenshot screenshot = (TakesScreenshot) DriverFactory.getDriver();
            byte[] screenshotBytes = screenshot.getScreenshotAs(OutputType.BYTES);
            logger.info("Screenshot captured successfully");
            return screenshotBytes;
        } catch (Exception e) {
            logger.error("Failed to capture screenshot: {}", e.getMessage());
            return new byte[0];
        }
    }

    /**
     * Get current test method name
     */
    protected String getCurrentTestName() {
        return Thread.currentThread().getStackTrace()[2].getMethodName();
    }

    /**
     * Sleep utility method
     */
    protected void sleep(int seconds) {
        try {
            Thread.sleep(seconds * 1000L);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("Sleep interrupted", e);
        }
    }

    /**
     * Log test step
     */
    protected void logStep(String stepDescription) {
        logger.info("TEST STEP: {}", stepDescription);
    }

    /**
     * Log test info
     */
    protected void logInfo(String message) {
        logger.info("TEST INFO: {}", message);
    }

    /**
     * Log test warning
     */
    protected void logWarning(String message) {
        logger.warn("TEST WARNING: {}", message);
    }

    /**
     * Log test error
     */
    protected void logError(String message) {
        logger.error("TEST ERROR: {}", message);
    }
}