package core;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.By;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Duration;

/**
 * Helper class for WebDriver waits and element interaction utilities
 */
public class WaitHelper {
    private static final Logger logger = LogManager.getLogger(WaitHelper.class);
    private final WebDriverWait wait;
    private final WebDriver driver;

    public WaitHelper(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(ConfigReader.getExplicitWait()));
    }

    /**
     * Wait for element to be visible
     */
    public WebElement waitForElementToBeVisible(By locator) {
        logger.debug("Waiting for element to be visible: {}", locator);
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    /**
     * Wait for element to be visible
     */
    public WebElement waitForElementToBeVisible(WebElement element) {
        logger.debug("Waiting for element to be visible: {}", element);
        return wait.until(ExpectedConditions.visibilityOf(element));
    }

    /**
     * Wait for element to be clickable
     */
    public WebElement waitForElementToBeClickable(By locator) {
        logger.debug("Waiting for element to be clickable: {}", locator);
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    /**
     * Wait for element to be clickable
     */
    public WebElement waitForElementToBeClickable(WebElement element) {
        logger.debug("Waiting for element to be clickable: {}", element);
        return wait.until(ExpectedConditions.elementToBeClickable(element));
    }

    /**
     * Wait for element to be present in DOM
     */
    public WebElement waitForElementToBePresent(By locator) {
        logger.debug("Waiting for element to be present: {}", locator);
        return wait.until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    /**
     * Wait for element to disappear
     */
    public boolean waitForElementToDisappear(By locator) {
        logger.debug("Waiting for element to disappear: {}", locator);
        return wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }

    /**
     * Wait for text to be present in element
     */
    public boolean waitForTextToBePresentInElement(WebElement element, String text) {
        logger.debug("Waiting for text '{}' to be present in element: {}", text, element);
        return wait.until(ExpectedConditions.textToBePresentInElement(element, text));
    }

    /**
     * Wait for title to contain text
     */
    public boolean waitForTitleToContain(String title) {
        logger.debug("Waiting for title to contain: {}", title);
        return wait.until(ExpectedConditions.titleContains(title));
    }

    /**
     * Wait for URL to contain text
     */
    public boolean waitForUrlToContain(String urlPart) {
        logger.debug("Waiting for URL to contain: {}", urlPart);
        return wait.until(ExpectedConditions.urlContains(urlPart));
    }

    /**
     * Wait for alert to be present
     */
    public void waitForAlert() {
        logger.debug("Waiting for alert to be present");
        wait.until(ExpectedConditions.alertIsPresent());
    }

    /**
     * Wait for custom condition with timeout
     */
    public <T> T waitForCondition(java.util.function.Function<WebDriver, T> condition, int timeoutSeconds) {
        WebDriverWait customWait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
        return customWait.until(condition);
    }

    /**
     * Sleep for specified milliseconds (use sparingly)
     */
    public void sleep(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("Sleep interrupted", e);
        }
    }
    
    // Additional wait methods with timeout parameter
    public WebElement waitForElementVisible(By locator, int timeoutSeconds) {
        WebDriverWait customWait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
        return customWait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }
    
    public WebElement waitForElementClickable(By locator, int timeoutSeconds) {
        WebDriverWait customWait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
        return customWait.until(ExpectedConditions.elementToBeClickable(locator));
    }
}