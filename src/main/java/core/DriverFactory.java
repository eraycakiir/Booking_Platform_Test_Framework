package core;

import enums.BrowserType;
import enums.PlatformType;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.ios.IOSDriver;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;

/**
 * Factory class for WebDriver management with local and remote execution support
 */
public class DriverFactory {
    private static final Logger logger = LogManager.getLogger(DriverFactory.class);
    private static final ThreadLocal<WebDriver> driverThreadLocal = new ThreadLocal<>();

    /**
     * Get WebDriver instance for current thread
     */
    public static WebDriver getDriver() {
        return driverThreadLocal.get();
    }

    /**
     * Initialize WebDriver based on configuration
     */
    public static void initializeDriver() {
        PlatformType platformType = PlatformType.fromString(ConfigReader.getPlatform());
        WebDriver driver;

        switch (platformType) {
            case WEB:
                BrowserType browserType = BrowserType.fromString(ConfigReader.getBrowser());
                if (ConfigReader.isRemoteExecution()) {
                    driver = createRemoteDriver(browserType);
                } else {
                    driver = createLocalDriver(browserType);
                }
                // Anti-detection: hide navigator.webdriver via CDP on Chrome
                try {
                    if (driver instanceof org.openqa.selenium.chrome.ChromeDriver) {
                        java.util.Map<String, Object> params = new java.util.HashMap<>();
                        params.put("source", "Object.defineProperty(navigator, 'webdriver', {get: () => undefined})");
                        ((org.openqa.selenium.chrome.ChromeDriver) driver)
                                .executeCdpCommand("Page.addScriptToEvaluateOnNewDocument", params);
                    }
                } catch (Exception ignored) {
                }
                // Maximize window for web
                driver.manage().window().maximize();
                break;
            
            case IOS:
                driver = createIOSDriver();
                break;
            
            default:
                throw new IllegalArgumentException("Unsupported platform type: " + platformType);
        }

        // Set implicit wait
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(ConfigReader.getImplicitWait()));

        driverThreadLocal.set(driver);
        logger.info("Driver initialized successfully: Platform={}, Remote={}", 
                   platformType.getPlatformName(), ConfigReader.isRemoteExecution());
    }

    /**
     * Create local WebDriver instance
     */
    private static WebDriver createLocalDriver(BrowserType browserType) {
        switch (browserType) {
            case CHROME:
                WebDriverManager.chromedriver().setup();
                return new ChromeDriver(getChromeOptions());
            
            default:
                throw new IllegalArgumentException("Unsupported browser type: " + browserType);
        }
    }

    /**
     * Create remote WebDriver instance for Selenium Grid
     */
    private static WebDriver createRemoteDriver(BrowserType browserType) {
        try {
            URL hubUrl = new URL(ConfigReader.getHubUrl());
            
            switch (browserType) {
                case CHROME:
                    return new RemoteWebDriver(hubUrl, getChromeOptions());
                
                default:
                    throw new IllegalArgumentException("Unsupported browser type for remote execution: " + browserType);
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Invalid hub URL: " + ConfigReader.getHubUrl(), e);
        }
    }

    /**
     * Get Chrome options with common configurations
     */
    private static ChromeOptions getChromeOptions() {
        ChromeOptions options = new ChromeOptions();
        
        if (ConfigReader.isHeadless()) {
            options.addArguments("--headless=new");
        }
        // Minimal argümanlar
        options.addArguments(
            "--no-sandbox",
            "--disable-dev-shm-usage",
            "--disable-blink-features=AutomationControlled"
        );

        // Anti-automation bayrakları
        options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
        options.setExperimentalOption("useAutomationExtension", false);

        // Her çalıştırmada benzersiz geçici profil kullan (profil kilidi hatasını engeller, CAPTCHA riskini azaltır)
        try {
            java.nio.file.Path tempProfile = java.nio.file.Files.createTempDirectory("selenium-chrome-profile-");
            options.addArguments("--user-data-dir=" + tempProfile.toString());
            options.addArguments("--profile-directory=Default");
        } catch (Exception ignored) {
        }

        return options;
    }

    // Removed other browsers (Firefox/Edge/Safari) – Chrome only

    /**
     * Create iOS AppiumDriver instance
     */
    private static AppiumDriver createIOSDriver() {
        try {
            DesiredCapabilities capabilities = new DesiredCapabilities();
            
            // iOS Platform capabilities - W3C standard
            capabilities.setCapability("appium:platformName", "iOS");
            capabilities.setCapability("appium:deviceName", ConfigReader.getIOSDeviceName());
            capabilities.setCapability("appium:platformVersion", ConfigReader.getIOSPlatformVersion());
            capabilities.setCapability("appium:udid", ConfigReader.getIOSUdid());
            
            // App capabilities - Booking app
            capabilities.setCapability("appium:bundleId", ConfigReader.getIOSAppBundleId());
            capabilities.setCapability("appium:autoLaunch", true);
            capabilities.setCapability("appium:noReset", true);
            
            // Developer capabilities for real device
            capabilities.setCapability("appium:xcodeOrgId", ConfigReader.getIOSTeamId());
            capabilities.setCapability("appium:xcodeSigningId", "iPhone Developer");
            capabilities.setCapability("appium:updatedWDABundleId", "com.facebook.WebDriverAgentRunner");
            
            // Appium capabilities
            capabilities.setCapability("appium:automationName", "XCUITest");
            capabilities.setCapability("appium:newCommandTimeout", 300);
            capabilities.setCapability("appium:wdaStartupRetries", 4);
            capabilities.setCapability("appium:wdaStartupRetryInterval", 20000);
            
            URL appiumUrl = new URL(ConfigReader.getAppiumServerUrl());
            return new IOSDriver(appiumUrl, capabilities);
            
        } catch (MalformedURLException e) {
            throw new RuntimeException("Invalid Appium server URL: " + ConfigReader.getAppiumServerUrl(), e);
        }
    }

    /**
     * Quit WebDriver and remove from ThreadLocal
     */
    public static void quitDriver() {
        WebDriver driver = driverThreadLocal.get();
        if (driver != null) {
            try {
                driver.quit();
                logger.info("WebDriver closed successfully");
            } catch (Exception e) {
                logger.error("Error while closing WebDriver: {}", e.getMessage());
            } finally {
                driverThreadLocal.remove();
            }
        }
    }
}