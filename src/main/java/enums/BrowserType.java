package enums;

/**
 * Enum for supported browser types
 */
public enum BrowserType {
    CHROME("chrome");

    private final String browserName;

    BrowserType(String browserName) {
        this.browserName = browserName;
    }

    public String getBrowserName() {
        return browserName;
    }

    public static BrowserType fromString(String browser) {
        for (BrowserType type : BrowserType.values()) {
            if (type.getBrowserName().equalsIgnoreCase(browser)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unsupported browser: " + browser + ". Supported: chrome");
    }
}