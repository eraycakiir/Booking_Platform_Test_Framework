package pages.ios;

import interfaces.ILoginPage;
import io.appium.java_client.AppiumBy;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * iOS implementation of Login Page for Booking.com
 * Uses Appium for iOS mobile automation
 */
public class IOSLoginPage extends IOSBasePage implements ILoginPage {
    private static final Logger logger = LogManager.getLogger(IOSLoginPage.class);

    // Locators
    private final By signInOrRegisterLocator = AppiumBy.accessibilityId("Sign in or register");
    // Option list screen: "Continue with email" to navigate to email entry
    private final By emailAuthOptionButton = AppiumBy.accessibilityId("ai_email_authentication_button");
    // Email entry screen
    private final By emailFieldLocator = AppiumBy.accessibilityId("Email address");
    private final By emailContinueButton = AppiumBy.accessibilityId("ai_sign_in_email_continue_button");
    // OTP screen
    private final By otpContainerKey = AppiumBy.accessibilityId("ai_sign_in_otp_field");
    private final By secureOtpField = AppiumBy.className("XCUIElementTypeSecureTextField");
    private final By verifyEmailButton = AppiumBy.accessibilityId("ai_sign_in_otp_continue_button");
    private final By codeFieldLocator = AppiumBy.iOSNsPredicateString("label == 'Verification code' OR name == 'ai_sign_in_otp_field'");
    private final By submitCodeButtonLocator = AppiumBy.iOSNsPredicateString("type == 'XCUIElementTypeButton' AND (name CONTAINS 'Continue' OR name CONTAINS 'Verify')");
    private final By multiCodeFields = AppiumBy.iOSNsPredicateString("type == 'XCUIElementTypeTextField' AND name BEGINSWITH 'code_'");
    private final By successIndicator = AppiumBy.iOSNsPredicateString("name CONTAINS 'Account' OR name CONTAINS 'Profile' OR label CONTAINS 'Account'");

    // Constructor
    public IOSLoginPage() {
        super();
        logger.info("IOSLoginPage initialized");
    }

    @Override
    public boolean isPageLoaded() {
        try {
            waitHelper.waitForElementVisible(signInOrRegisterLocator, 15);
            return true;
        } catch (Exception e) {
            // Alternatif olarak email field'ını kontrol et
            try {
                waitHelper.waitForElementVisible(emailFieldLocator, 5);
                return true;
            } catch (Exception e2) {
                logger.warn("IOSLoginPage not loaded: {}", e.getMessage());
                return false;
            }
        }
    }

    @Override
    public void clickSignInOrRegister() {
        try {
            WebElement signInOrRegisterButton = waitHelper.waitForElementClickable(signInOrRegisterLocator, 15);
            attachScreenshot("ios_before_click_signin_register");
            signInOrRegisterButton.click();
            logger.info("Clicked 'Sign in or register' button");
            attachScreenshot("ios_after_click_signin_register");
            // After this screen, if email option is visible, tap it to navigate to email entry
            try {
                WebElement emailOption = waitHelper.waitForElementClickable(emailAuthOptionButton, 5);
                attachScreenshot("ios_before_click_continue_with_email_option");
                emailOption.click();
                logger.info("Clicked 'Continue with email' option");
                attachScreenshot("ios_after_click_continue_with_email_option");
            } catch (Exception ignored) {}
        } catch (Exception e) {
            logger.error("Failed to click 'Sign in or register': {}", e.getMessage());
            throw new RuntimeException("Sign in or register button not clickable", e);
        }
    }

    @Override
    public void clickContinueWithEmail() {
        // On email entry page, the button is 'ai_sign_in_email_continue_button'
        try {
            WebElement continueBtn = waitHelper.waitForElementClickable(emailContinueButton, 15);
            attachScreenshot("ios_before_click_email_continue");
            continueBtn.click();
            logger.info("Clicked 'Continue' on email entry page");
            attachScreenshot("ios_after_click_email_continue");
        } catch (Exception e) {
            logger.error("Failed to click email 'Continue': {}", e.getMessage());
            throw new RuntimeException("Continue with email button not clickable", e);
        }
    }

    @Override
    public void enterEmail(String email) {
        try {
            WebElement emailField = waitHelper.waitForElementVisible(emailFieldLocator, 15);
            emailField.clear();
            emailField.sendKeys(email);
            logger.info("Entered email: {}", email);
        } catch (Exception e) {
            logger.error("Failed to enter email: {}", e.getMessage());
            throw new RuntimeException("Email field not accessible", e);
        }
    }

    @Override
    public void enterVerificationCode(String code) {
        // Strategy 1: fill 6 separate code text fields if present
        try {
            java.util.List<WebElement> fields = appiumDriver.findElements(multiCodeFields);
            if (fields != null && fields.size() >= 6) {
                char[] chars = code.toCharArray();
                for (int i = 0; i < Math.min(chars.length, 6); i++) {
                    WebElement f = fields.get(i);
                    waitHelper.waitForElementToBeVisible(f);
                    f.clear();
                    attachScreenshot("ios_before_type_code_" + i);
                    f.sendKeys(String.valueOf(chars[i]));
                    attachScreenshot("ios_after_type_code_" + i);
                }
                logger.info("Entered verification code into 6 iOS fields");
            } else {
                // Fallback: single secure text field under OTP container
                try {
                    WebElement container = waitHelper.waitForElementVisible(otpContainerKey, 10);
                    WebElement secure = container.findElement(secureOtpField);
                    secure.clear();
                    attachScreenshot("ios_before_type_code_secure");
                    secure.sendKeys(code);
                    logger.info("Entered verification code in secure field (OTP container)");
                    attachScreenshot("ios_after_type_code_secure");
                } catch (Exception inner) {
                    WebElement codeField = waitHelper.waitForElementVisible(codeFieldLocator, 20);
                    codeField.clear();
                    attachScreenshot("ios_before_type_code_single");
                    codeField.sendKeys(code);
                    logger.info("Entered verification code in single iOS field by label");
                    attachScreenshot("ios_after_type_code_single");
                }
            }

            try {
                WebElement submit = waitHelper.waitForElementClickable(verifyEmailButton, 10);
                attachScreenshot("ios_before_click_verify");
                submit.click();
                logger.info("Tapped 'Verify email' button (iOS)");
                attachScreenshot("ios_after_click_verify");
            } catch (Exception e) {
                try {
                    WebElement submitAlt = waitHelper.waitForElementClickable(submitCodeButtonLocator, 5);
                    attachScreenshot("ios_before_click_verify_alt");
                    submitAlt.click();
                    logger.info("Submitted verification code via generic button (iOS)");
                    attachScreenshot("ios_after_click_verify_alt");
                } catch (Exception ignored) {}
            }

            // Wait for success indicator
            try {
                waitHelper.waitForElementToBeVisible(successIndicator);
                logger.info("iOS login confirmed");
                attachScreenshot("ios_login_confirmed");
            } catch (Exception ignored) {}

        } catch (Exception e) {
            logger.error("Failed to enter verification code: {}", e.getMessage());
            throw new RuntimeException("Verification code field not accessible", e);
        }
    }
}