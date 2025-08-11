package utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Common small utilities for tests to avoid in-test boilerplate.
 */
public final class TestHelper {
    private static final Logger logger = LogManager.getLogger(TestHelper.class);

    private TestHelper() {}

    public static void sleepSeconds(int seconds) {
        try {
            Thread.sleep(seconds * 1000L);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.warn("Sleep interrupted");
        }
    }
}


