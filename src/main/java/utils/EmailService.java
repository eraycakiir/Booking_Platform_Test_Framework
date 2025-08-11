package utils;

import interfaces.IEmailService;

import jakarta.mail.*;
import jakarta.mail.search.AndTerm;
import jakarta.mail.search.FromStringTerm;
import jakarta.mail.search.ReceivedDateTerm;
import jakarta.mail.search.SearchTerm;
import jakarta.mail.search.SubjectTerm;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Simple IMAP Gmail implementation using app password (or token) authentication.
 */
public class EmailService implements IEmailService {

    private static final Logger logger = LogManager.getLogger(EmailService.class);
    // 6 karakter alfasayısal ve TAMAMI harf olmayan (en az 1 rakam içeren) kodlar - capture group ile
    private static final Pattern DEFAULT_CODE_PATTERN = Pattern.compile("\\b(?![A-Z]{6}\\b)([A-Z0-9]{6})\\b", Pattern.CASE_INSENSITIVE);

    @Override
    public String fetchVerificationCode(String emailAccount,
                                        String appPassword,
                                        String fromFilter,
                                        String subjectFilter,
                                        int timeoutSeconds) {
        logger.info("Email code fetch started (account={}, from='{}', subject='{}', timeout={}s)", maskEmail(emailAccount), fromFilter, subjectFilter, timeoutSeconds);
        long deadline = System.currentTimeMillis() + timeoutSeconds * 1000L;
        MessagingException lastException = null;

        while (System.currentTimeMillis() < deadline) {
            try {
                String code = tryFetch(emailAccount, sanitize(appPassword), fromFilter, subjectFilter);
                if (code != null) {
                    logger.debug("Verification code found: {}", code);
                    return code;
                }
                Thread.sleep(3000);
            } catch (MessagingException e) {
                lastException = e;
                logger.warn("Email fetch attempt failed: {}", e.getMessage());
                sleepQuietly(3000);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        if (lastException != null) {
            logger.error("Verification code could not be fetched from email: {}", lastException.getMessage());
            throw new RuntimeException("Verification code could not be fetched from email", lastException);
        }
        throw new RuntimeException("Verification code not found within timeout");
    }

    @Override
    public String fetchVerificationCode(String emailAccount,
                                        String appPassword,
                                        String fromFilter,
                                        String subjectFilter,
                                        int timeoutSeconds,
                                        int initialDelaySeconds) {
        if (initialDelaySeconds > 0) {
            logger.debug("Initial delay before email fetch: {}s", initialDelaySeconds);
            sleepQuietly(initialDelaySeconds * 1000L);
        }
        return fetchVerificationCode(emailAccount, appPassword, fromFilter, subjectFilter, timeoutSeconds);
    }

    private String tryFetch(String emailAccount,
                            String appPassword,
                            String fromFilter,
                            String subjectFilter) throws MessagingException {
        Properties props = new Properties();
        props.put("mail.store.protocol", "imaps");
        props.put("mail.imaps.host", "imap.gmail.com");
        props.put("mail.imaps.port", "993");
        props.put("mail.imaps.ssl.enable", "true");

        Session session = Session.getInstance(props);
        Store store = session.getStore("imaps");
        logger.debug("Connecting to IMAP server as {}", maskEmail(emailAccount));
        store.connect("imap.gmail.com", emailAccount, appPassword);

        try (store) {
            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_ONLY);

            // 1) Arama: from/subject (varsa) + son 24 saat
            SearchTerm term = buildSearchTerm(fromFilter, subjectFilter);
            Message[] messages = (term != null) ? inbox.search(term) : inbox.getMessages();
            logger.debug("Primary search result count: {}", messages.length);
            String code = scanMessagesNewestFirst(messages);
            if (code != null) return code;

            // 2) Fallback: Sadece gönderen filtresi ile son 30 mail
            int total = inbox.getMessageCount();
            int end = total;
            int start = Math.max(1, end - 29);
            Message[] lastMessages = inbox.getMessages(start, end);
            logger.debug("Fallback scan last {} messages (total={})", (end - start + 1), total);
            if (fromFilter != null && !fromFilter.isBlank()) {
                lastMessages = inbox.search(new FromStringTerm(fromFilter), lastMessages);
                logger.debug("Filtered by from='{}': {} messages", fromFilter, lastMessages.length);
            }
            return scanMessagesNewestFirst(lastMessages);
        }
    }

    private SearchTerm buildSearchTerm(String fromFilter, String subjectFilter) {
        SearchTerm term = null;
        if (fromFilter != null && !fromFilter.isBlank()) {
            term = new FromStringTerm(fromFilter);
        }
        if (subjectFilter != null && !subjectFilter.isBlank()) {
            SubjectTerm st = new SubjectTerm(subjectFilter);
            term = (term == null) ? st : new AndTerm(term, st);
        }
        // Only recent emails (last 24h)
        Date since = Date.from(Instant.now().minus(24, ChronoUnit.HOURS));
        ReceivedDateTerm recent = new ReceivedDateTerm(ReceivedDateTerm.GE, since);
        term = (term == null) ? recent : new AndTerm(term, recent);
        return term;
    }

    private String scanMessagesNewestFirst(Message[] messages) {
        try {
            String newestCode = null;
            Date newestDate = null;
            for (int i = messages.length - 1; i >= 0; i--) {
                Message m = messages[i];
                String subject = null;
                Address[] from = null;
                Date received = null;
                try { subject = m.getSubject(); } catch (Exception ignored) {}
                try { from = m.getFrom(); } catch (Exception ignored) {}
                try { received = (m.getReceivedDate() != null) ? m.getReceivedDate() : m.getSentDate(); } catch (Exception ignored) {}
                logger.debug("Scanning mail: subject='{}', from='{}', date={} ", subject, firstAddress(from), received);
                // Önce subject içinde aramayı dene (çoğu zaman kod burada)
                if (subject != null) {
                    Matcher sm = DEFAULT_CODE_PATTERN.matcher(subject);
                    if (sm.find()) {
                        if (isNewer(received, newestDate)) {
                            newestCode = sm.group(1);
                            newestDate = received;
                            logger.debug("Candidate code (subject): {} at {}", newestCode, newestDate);
                        }
                    }
                }
                String body = EmailUtils.extractText(m);
                if (body == null) continue;
                Matcher matcher = DEFAULT_CODE_PATTERN.matcher(body);
                if (matcher.find()) {
                    String candidate = matcher.group(1);
                    if (isNewer(received, newestDate)) {
                        newestCode = candidate;
                        newestDate = received;
                        logger.debug("Candidate code (body): {} at {}", newestCode, newestDate);
                    }
                }
            }
            if (newestCode != null) {
                logger.debug("Newest code selected: {} at {}", newestCode, newestDate);
            }
            return newestCode;
        } catch (Exception e) {
            logger.warn("Scan messages error: {}", e.getMessage());
        }
        return null;
    }

    private boolean isNewer(Date a, Date b) {
        if (a == null) return false;
        if (b == null) return true;
        return a.after(b);
    }

    private void sleepQuietly(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private static String sanitize(String input) {
        return input == null ? null : input.replaceAll("\\s+", "");
    }

    private static String maskEmail(String email) {
        if (email == null || !email.contains("@")) return "***";
        String[] parts = email.split("@", 2);
        String local = parts[0];
        String domain = parts[1];
        String maskedLocal = local.length() <= 2 ? "**" : local.charAt(0) + "***" + local.charAt(local.length()-1);
        return maskedLocal + "@" + domain;
    }

    private static String firstAddress(Address[] addresses) {
        if (addresses == null || addresses.length == 0) return "";
        return addresses[0].toString();
    }

    /**
     * Helper for parsing message content to plain text
     */
    private static class EmailUtils {
        static String extractText(Part part) {
            try {
                if (part.isMimeType("text/*")) {
                    return (String) part.getContent();
                } else if (part.isMimeType("multipart/*")) {
                    Multipart mp = (Multipart) part.getContent();
                    for (int i = 0; i < mp.getCount(); i++) {
                        String text = extractText(mp.getBodyPart(i));
                        if (text != null && !text.isBlank()) {
                            return text;
                        }
                    }
                }
            } catch (Exception ignored) {
            }
            return null;
        }
    }
}


