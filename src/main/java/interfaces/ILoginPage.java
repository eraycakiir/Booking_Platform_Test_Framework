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
     * Doğrulama kodu giriş alanına kodu yazar ve devam eder (varsa)
     * Platforma göre implementasyon değişir.
     * @param code doğrulama kodu (örn: 6 haneli)
     */
    default void enterVerificationCode(String code) {
        // Varsayılan: Implementasyon gerektiren platformlarda override edilecek
        throw new UnsupportedOperationException("enterVerificationCode not implemented for this platform");
    }
    
    /**
     * Get current page title
     * @return page title
     */
    String getPageTitle();
}