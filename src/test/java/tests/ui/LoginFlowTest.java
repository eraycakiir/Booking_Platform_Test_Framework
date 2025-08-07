package tests.ui;

import factory.PageFactory;
import interfaces.IHomePage;
import interfaces.ILoginPage;
import core.DriverFactory;
import org.testng.Assert;
import org.testng.annotations.Test;
import io.qameta.allure.*;

/**
 * Platform-independent login flow tests
 * Works for both Web and iOS platforms
 */
@Epic("Authentication")
@Feature("Login Flow")
public class LoginFlowTest extends BaseTest {

    @Test(priority = 1, description = "Complete login flow - Enter email step")
    @Story("Email Login")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Test login flow from homepage to email entry for both Web and iOS platforms")
    public void testLoginFlowToEmailEntry() {
        logStep("Starting login flow test");
        
        // Get platform-specific page implementations
        IHomePage homePage = PageFactory.getHomePage();
        ILoginPage loginPage = PageFactory.getLoginPage();
        
        // Step 1: Verify home page is loaded
        logStep("Verifying home page is loaded");
        Assert.assertTrue(homePage.isPageLoaded(), "Home page should be loaded");
        logInfo("Home page verified successfully");
        
        // Step 2: Click sign in button
        logStep("Clicking sign in button");
        homePage.clickSignInButton();
        logInfo("Sign in button clicked");
        
        // Step 3: Handle platform-specific login steps
        String currentPlatform = PageFactory.getCurrentPlatform().getPlatformName();
        logInfo("Current platform: " + currentPlatform);
        
        if ("ios".equals(currentPlatform)) {
            // iOS specific steps
            logStep("Handling iOS login flow");
            
            // Wait for login page to load
            Assert.assertTrue(loginPage.isPageLoaded(), "Login page should be loaded");
            
            // Click "Sign in or register"
            logStep("Clicking 'Sign in or register'");
            loginPage.clickSignInOrRegister();
            logInfo("'Sign in or register' clicked");
            
            // Click "Continue with email"
            logStep("Clicking 'Continue with email'");
            loginPage.clickContinueWithEmail();
            logInfo("'Continue with email' clicked");
        }
        
        // Step 4: Enter email (common for both platforms)
        String testEmail = "test.booking@gmail.com";
        logStep("Entering email: " + testEmail);
        loginPage.enterEmail(testEmail);
        logInfo("Email entered successfully");
        
        // Step 5: Verify we reached email entry step
        logStep("Verifying login flow completed to email entry");
        Assert.assertTrue(loginPage.isPageLoaded(), "Should be on email entry page");
        
        logInfo("Login flow test completed successfully - Email entry step reached");
    }
    
    @Test(priority = 2, description = "Test different email addresses")
    @Story("Email Validation")
    @Severity(SeverityLevel.NORMAL)
    @Description("Test login flow with different email formats")
    public void testLoginWithDifferentEmails() {
        logStep("Testing login with different email formats");
        
        String[] testEmails = {
            "web.test@gmail.com",
            "ios.test@gmail.com", 
            "automation.test@booking.com"
        };
        
        for (String email : testEmails) {
            logStep("Testing with email: " + email);
            
            IHomePage homePage = PageFactory.getHomePage();
            ILoginPage loginPage = PageFactory.getLoginPage();
            
            // Navigate through login flow
            homePage.clickSignInButton();
            
            if ("ios".equals(PageFactory.getCurrentPlatform().getPlatformName())) {
                loginPage.clickSignInOrRegister();
                loginPage.clickContinueWithEmail();
            }
            
            loginPage.enterEmail(email);
            logInfo("Successfully entered email: " + email);
            
            // Refresh page for next iteration
            refreshForNextTest();
        }
        
        logInfo("Different email test completed successfully");
    }
    
    /**
     * Helper method to refresh page between tests
     */
    private void refreshForNextTest() {
        try {
            Thread.sleep(2000); // Wait a bit
            // Navigate back to home for next test
            DriverFactory.getDriver().navigate().refresh();
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}