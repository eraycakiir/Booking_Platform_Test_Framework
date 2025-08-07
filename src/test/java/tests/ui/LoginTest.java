package tests.ui;

import core.DriverFactory;
import org.testng.Assert;
import org.testng.annotations.Test;
// Old imports removed - using platform-agnostic approach now
import io.qameta.allure.*;

/**
 * Test class for framework functionality verification
 * Actual tests will be added after iOS configuration
 */
@Epic("Framework")
@Feature("Basic Tests")
public class LoginTest extends BaseTest {

    @Test(priority = 1, description = "Framework verification test - Chrome browser")
    @Story("Framework Verification")
    @Severity(SeverityLevel.TRIVIAL)
    @Description("This test verifies that the test framework is working correctly with Chrome browser")
    public void testChromeFramework() {
        logStep("Verifying framework works with Chrome browser");
        
        String currentUrl = DriverFactory.getDriver().getCurrentUrl();
        Assert.assertNotNull(currentUrl, "Current URL should not be null");
        
        String pageTitle = DriverFactory.getDriver().getTitle();
        logInfo("Page title: " + pageTitle);
        logInfo("Current URL: " + currentUrl);
        
        logInfo("Chrome framework verification test completed successfully");
    }

    @Test(priority = 2, description = "Framework verification test - iOS app", enabled = true)
    @Story("iOS Framework Verification")
    @Severity(SeverityLevel.TRIVIAL)
    @Description("This test verifies that the test framework is working correctly with iOS app")
    public void testIOSFramework() {
        logStep("Verifying framework works with iOS app");
        
        // Get current app context
        String pageSource = DriverFactory.getDriver().getPageSource();
        Assert.assertNotNull(pageSource, "Page source should not be null");
        
        logInfo("iOS app launched successfully");
        logInfo("Framework works with iOS!");
        
        logInfo("iOS framework verification test completed successfully");
    }
}