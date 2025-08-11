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
        String sys = System.getProperty("platform");
        if (sys != null && !sys.isBlank()) {
            return sys;
        }
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

    // Email/Gmail settings
    public static boolean isEmailVerificationEnabled() {
        return Boolean.parseBoolean(getProperty("email.verification.enabled", "false"));
    }

    // Platform-specific enable flags with fallback to global
    public static boolean isWebEmailVerificationEnabled() {
        return Boolean.parseBoolean(getProperty("email.web.verification.enabled", String.valueOf(isEmailVerificationEnabled())));
    }

    public static boolean isIOSEmailVerificationEnabled() {
        return Boolean.parseBoolean(getProperty("email.ios.verification.enabled", String.valueOf(isEmailVerificationEnabled())));
    }

    public static String getEmailAccount() {
        return getProperty("email.account", "");
    }

    public static String getEmailAppPassword() {
        return getProperty("email.app.password", "");
    }

    // Platform-specific accounts with fallback to global
    public static String getWebEmailAccount() {
        String v = getProperty("email.web.account", "");
        return v.isEmpty() ? getEmailAccount() : v;
    }

    public static String getWebEmailAppPassword() {
        String v = getProperty("email.web.app.password", "");
        return v.isEmpty() ? getEmailAppPassword() : v;
    }

    public static String getIOSEmailAccount() {
        String v = getProperty("email.ios.account", "");
        return v.isEmpty() ? getEmailAccount() : v;
    }

    public static String getIOSEmailAppPassword() {
        String v = getProperty("email.ios.app.password", "");
        return v.isEmpty() ? getEmailAppPassword() : v;
    }

    public static String getEmailFromFilter() {
        return getProperty("email.from.filter", "");
    }

    public static String getEmailSubjectFilter() {
        return getProperty("email.subject.filter", "");
    }

    public static int getEmailTimeoutSeconds() {
        return Integer.parseInt(getProperty("email.timeout.seconds", "120"));
    }

    // Login email (platform-specific override, falls back to global -> email.account)
    public static String getLoginEmail() {
        String platform = getPlatform();
        String override = null;
        if ("ios".equalsIgnoreCase(platform)) {
            override = properties.getProperty("login.ios.email");
        } else if ("web".equalsIgnoreCase(platform)) {
            override = properties.getProperty("login.web.email");
        }
        if (override != null && !override.isBlank()) {
            return override;
        }
        String loginEmail = properties.getProperty("login.email");
        if (loginEmail == null || loginEmail.isBlank()) {
            return getEmailAccount();
        }
        return loginEmail;
    }
}