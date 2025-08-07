package listeners;

import core.ConfigReader;
import core.DriverFactory;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.ITestListener;
import org.testng.ITestResult;
import io.qameta.allure.Attachment;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * TestNG listener for capturing screenshots on test failure
 */
public class ScreenshotListener implements ITestListener {
    private static final Logger logger = LogManager.getLogger(ScreenshotListener.class);

    @Override
    public void onTestFailure(ITestResult result) {
        logger.info("Test failed: {}", result.getMethod().getMethodName());
        
        WebDriver driver = DriverFactory.getDriver();
        if (driver != null) {
            // Capture screenshot for Allure report
            captureScreenshotForAllure(result.getMethod().getMethodName());
            
            // Save screenshot to file system
            saveScreenshotToFile(result.getMethod().getMethodName());
        } else {
            logger.warn("WebDriver is null, cannot capture screenshot");
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        logger.info("Test skipped: {}", result.getMethod().getMethodName());
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        logger.info("Test passed: {}", result.getMethod().getMethodName());
    }

    @Override
    public void onTestStart(ITestResult result) {
        logger.info("Test started: {}", result.getMethod().getMethodName());
    }

    /**
     * Capture screenshot and attach to Allure report
     */
    @Attachment(value = "Screenshot on Failure", type = "image/png")
    public byte[] captureScreenshotForAllure(String testName) {
        try {
            WebDriver driver = DriverFactory.getDriver();
            if (driver instanceof TakesScreenshot) {
                byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
                logger.info("Screenshot captured for Allure report: {}", testName);
                return screenshot;
            }
        } catch (Exception e) {
            logger.error("Failed to capture screenshot for Allure: {}", e.getMessage());
        }
        return new byte[0];
    }

    /**
     * Save screenshot to file system
     */
    public void saveScreenshotToFile(String testName) {
        try {
            WebDriver driver = DriverFactory.getDriver();
            if (driver instanceof TakesScreenshot) {
                // Create screenshot directory if not exists
                String screenshotDir = ConfigReader.getProperty("screenshot.path", "reports/screenshots/");
                File directory = new File(screenshotDir);
                if (!directory.exists()) {
                    directory.mkdirs();
                }

                // Generate filename with timestamp
                String timestamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
                String fileName = String.format("%s_%s_%s.png", 
                    testName, timestamp, Thread.currentThread().getId());
                
                File screenshotFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
                File destinationFile = new File(directory, fileName);
                
                FileUtils.copyFile(screenshotFile, destinationFile);
                logger.info("Screenshot saved to: {}", destinationFile.getAbsolutePath());
                
                // Set system property for ExtentReports (if used)
                System.setProperty("screenshot.path", destinationFile.getAbsolutePath());
                
            }
        } catch (IOException e) {
            logger.error("Failed to save screenshot to file: {}", e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error while saving screenshot: {}", e.getMessage());
        }
    }

    /**
     * Capture screenshot with custom name
     */
    public String captureScreenshot(String customName) {
        try {
            WebDriver driver = DriverFactory.getDriver();
            if (driver instanceof TakesScreenshot) {
                String screenshotDir = ConfigReader.getProperty("screenshot.path", "reports/screenshots/");
                File directory = new File(screenshotDir);
                if (!directory.exists()) {
                    directory.mkdirs();
                }

                String timestamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
                String fileName = String.format("%s_%s.png", customName, timestamp);
                
                File screenshotFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
                File destinationFile = new File(directory, fileName);
                
                FileUtils.copyFile(screenshotFile, destinationFile);
                logger.info("Custom screenshot saved: {}", destinationFile.getAbsolutePath());
                
                return destinationFile.getAbsolutePath();
            }
        } catch (Exception e) {
            logger.error("Failed to capture custom screenshot: {}", e.getMessage());
        }
        return null;
    }
}