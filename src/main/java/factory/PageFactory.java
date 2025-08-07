package factory;

import interfaces.IHomePage;
import interfaces.ILoginPage;
import pages.web.WebHomePage;
import pages.web.WebLoginPage;
import pages.ios.IOSHomePage;
import pages.ios.IOSLoginPage;
import core.ConfigReader;
import enums.PlatformType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Factory class to create platform-specific page objects
 * Returns interface implementations based on current platform configuration
 */
public class PageFactory {
    private static final Logger logger = LogManager.getLogger(PageFactory.class);

    /**
     * Get HomePage implementation based on current platform
     * @return IHomePage implementation (Web or iOS)
     */
    public static IHomePage getHomePage() {
        PlatformType platform = PlatformType.fromString(ConfigReader.getPlatform());
        
        switch (platform) {
            case WEB:
                logger.debug("Creating WebHomePage instance");
                return new WebHomePage();
            case IOS:
                logger.debug("Creating IOSHomePage instance");
                return new IOSHomePage();
            default:
                throw new IllegalArgumentException("Unsupported platform: " + platform);
        }
    }

    /**
     * Get LoginPage implementation based on current platform
     * @return ILoginPage implementation (Web or iOS)
     */
    public static ILoginPage getLoginPage() {
        PlatformType platform = PlatformType.fromString(ConfigReader.getPlatform());
        
        switch (platform) {
            case WEB:
                logger.debug("Creating WebLoginPage instance");
                return new WebLoginPage();
            case IOS:
                logger.debug("Creating IOSLoginPage instance");
                return new IOSLoginPage();
            default:
                throw new IllegalArgumentException("Unsupported platform: " + platform);
        }
    }

    /**
     * Get current platform type
     * @return current platform type
     */
    public static PlatformType getCurrentPlatform() {
        return PlatformType.fromString(ConfigReader.getPlatform());
    }
}