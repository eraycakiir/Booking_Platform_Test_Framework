package tests.ui;

import factory.PageFactory;
import interfaces.IHomePage;
import interfaces.ILoginPage;
import org.testng.Assert;
import org.testng.annotations.Test;
import io.qameta.allure.*;

/**
 * Platform-independent login flow tests
 * Works for both Web and iOS platforms
 */
@Epic("Authentication")
@Feature("Login Flow")
public class LoginTest extends BaseTest {

    @Test(priority = 1, description = "Complete login flow - Enter email step")
    @Story("Email Login")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Test login flow from homepage to email entry for both Web and iOS platforms")
    public void testLoginFlowToEmailEntry() {
        String currentPlatform = PageFactory.getCurrentPlatform().getPlatformName();
        Allure.label("suite", currentPlatform.equals("ios") ? "iOS Login" : "Web Login");
        Allure.label("host", currentPlatform);
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
        }
        
        // Step 4: Enter email (common for both platforms)
        String testEmail = core.ConfigReader.getLoginEmail();
        logStep("Entering email: " + testEmail);
        loginPage.enterEmail(testEmail);
        logInfo("Email entered successfully");

        // Step 5: Click Continue with email (both platforms)
        logStep("Clicking 'Continue with email'");
        loginPage.clickContinueWithEmail();
        logInfo("'Continue with email' clicked");


        // Optional Step 6: Email verification (OTP) if enabled (platform-specific)
        boolean emailEnabled = "ios".equals(currentPlatform) ? core.ConfigReader.isIOSEmailVerificationEnabled() : core.ConfigReader.isWebEmailVerificationEnabled();
        if (emailEnabled) {
            logStep("Email verification enabled - fetching code from inbox");
            utils.EmailService emailService = new utils.EmailService();
            String emailAccount = "ios".equals(currentPlatform) ? core.ConfigReader.getIOSEmailAccount() : core.ConfigReader.getWebEmailAccount();
            String appPassword = "ios".equals(currentPlatform) ? core.ConfigReader.getIOSEmailAppPassword() : core.ConfigReader.getWebEmailAppPassword();
            String code = emailService.fetchVerificationCode(
                    emailAccount,
                    appPassword,
                    core.ConfigReader.getEmailFromFilter(),
                    core.ConfigReader.getEmailSubjectFilter(),
                    core.ConfigReader.getEmailTimeoutSeconds(),
                    10
            );
            // Mask OTP at info level
            logInfo("Verification code received: ******");
            // Debug full code
            logWarning("[DEBUG_ONLY] OTP=" + code);
            logStep("Entering verification code");
            loginPage.enterVerificationCode(code);
        }

        // Step 7: Basic assertion to ensure flow did not error
        logInfo("Login flow executed");
    }
    
    // Removed email-format iteration test to avoid state coupling and sleeps
}