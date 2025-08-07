package core;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Configuration reader for application properties
 */
public class ConfigReader {
    private static Properties properties;
    private static final String CONFIG_FILE_PATH = "src/main/resources/config.properties";

    static {
        loadProperties();
    }

    private static void loadProperties() {
        properties = new Properties();
        try (FileInputStream fis = new FileInputStream(CONFIG_FILE_PATH)) {
            properties.load(fis);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load configuration file: " + CONFIG_FILE_PATH, e);
        }
    }

    public static String getProperty(String key) {
        String value = properties.getProperty(key);
        if (value == null) {
            throw new RuntimeException("Property '" + key + "' not found in configuration file");
        }
        return value;
    }

    public static String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    public static String getBrowser() {
        return getProperty("browser", "chrome");
    }

    public static String getPlatform() {
        return getProperty("platform", "web");
    }

    // iOS Configuration methods
    public static String getIOSAppBundleId() {
        return getProperty("ios.app.bundle.id");
    }

    public static String getIOSDeviceName() {
        return getProperty("ios.device.name");
    }

    public static String getIOSPlatformVersion() {
        return getProperty("ios.platform.version");
    }

    public static String getIOSUdid() {
        return getProperty("ios.udid");
    }

    public static String getIOSTeamId() {
        return getProperty("ios.team.id");
    }

    public static String getAppiumServerUrl() {
        return getProperty("appium.server.url", "http://localhost:4723");
    }

    public static String getBaseUrl() {
        return getProperty("base.url");
    }

    public static boolean isHeadless() {
        return Boolean.parseBoolean(getProperty("headless", "false"));
    }

    public static int getImplicitWait() {
        return Integer.parseInt(getProperty("implicit.wait", "10"));
    }

    public static int getExplicitWait() {
        return Integer.parseInt(getProperty("explicit.wait", "20"));
    }

    public static String getEnvironment() {
        return getProperty("environment", "test");
    }

    public static boolean isRemoteExecution() {
        return Boolean.parseBoolean(getProperty("remote.execution", "false"));
    }

    public static String getHubUrl() {
        return getProperty("hub.url", "http://localhost:4444/wd/hub");
    }
}