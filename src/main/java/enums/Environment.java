package enums;

/**
 * Enum for different MifosX Banking test environments
 */
public enum Environment {
    DEV("dev", "https://dev.fineract.dev"),
    TEST("test", "https://demo.fineract.dev"),
    STAGING("staging", "https://staging.fineract.dev"),
    PROD("prod", "https://fineract.apache.org");

    private final String name;
    private final String baseUrl;

    Environment(String name, String baseUrl) {
        this.name = name;
        this.baseUrl = baseUrl;
    }

    public String getName() {
        return name;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public static Environment fromString(String environment) {
        for (Environment env : Environment.values()) {
            if (env.getName().equalsIgnoreCase(environment)) {
                return env;
            }
        }
        throw new IllegalArgumentException("Unsupported environment: " + environment);
    }
}