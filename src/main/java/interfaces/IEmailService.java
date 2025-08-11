package interfaces;

/**
 * Platform-independent interface for fetching verification (OTP) codes from email inbox.
 */
public interface IEmailService {

    /**
     * Fetch verification code using Gmail/IMAP.
     *
     * @param emailAccount     Email address
     * @param appPassword      App password or token for IMAP auth
     * @param fromFilter       Sender filter (e.g., "noreply@booking.com")
     * @param subjectFilter    Subject filter (e.g., "verification code")
     * @param timeoutSeconds   Max wait time; polls until a code is found
     * @return Captured OTP (e.g., 6 alphanumeric chars)
     */
    String fetchVerificationCode(
            String emailAccount,
            String appPassword,
            String fromFilter,
            String subjectFilter,
            int timeoutSeconds
    );

    /**
     * Fetch verification code with an initial delay before polling.
     */
    String fetchVerificationCode(
            String emailAccount,
            String appPassword,
            String fromFilter,
            String subjectFilter,
            int timeoutSeconds,
            int initialDelaySeconds
    );
}


