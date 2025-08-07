package interfaces;

/**
 * Platform-independent interface for Home Page functionality
 * Both Web and iOS implementations will implement this interface
 */
public interface IHomePage {
    
    /**
     * Verify home page is loaded
     * @return true if home page is displayed
     */
    boolean isPageLoaded();
    
    /**
     * Click on sign in/login button
     */
    void clickSignInButton();
    
    /**
     * Get page title or main heading
     * @return page title/heading text
     */
    String getPageTitle();
}