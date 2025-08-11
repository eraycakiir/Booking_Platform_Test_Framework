package enums;

/**
 * Enum for supported platform types
 */
public enum PlatformType {
    WEB("web"),
    IOS("ios");

    private final String platformName;

    PlatformType(String platformName) {
        this.platformName = platformName;
    }

    public String getPlatformName() {
        return platformName;
    }

    public static PlatformType fromString(String platform) {
        for (PlatformType type : PlatformType.values()) {
            if (type.getPlatformName().equalsIgnoreCase(platform)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unsupported platform: " + platform + ". Supported: web, ios");
    }
}