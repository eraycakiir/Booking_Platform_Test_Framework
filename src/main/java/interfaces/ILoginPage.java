package interfaces;

/**
 * Platform-independent interface for Login Page functionality
 * Both Web and iOS implementations will implement this interface
 */
public interface ILoginPage {
    
    /**
     * Verify login page is loaded
     * @return true if login page is displayed
     */
    boolean isPageLoaded();
    
    /**
     * Click on "Sign in or register" button (iOS specific flow)
     */
    void clickSignInOrRegister();
    
    /**
     * Click on "Continue with email" button
     */
    void clickContinueWithEmail();
    
    /**
     * Enter email address
     * @param email email address to enter
     */
    void enterEmail(String email);
    
    /**
     * Get current page title
     * @return page title
     */
    String getPageTitle();
}