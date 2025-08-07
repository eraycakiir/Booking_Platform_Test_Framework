package listeners;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.*;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * TestNG listener for enhanced logging
 */
public class LoggerListener implements ITestListener, ISuiteListener, IInvokedMethodListener {
    private static final Logger logger = LogManager.getLogger(LoggerListener.class);
    private long testStartTime;
    private long suiteStartTime;

    // Suite level methods
    @Override
    public void onStart(ISuite suite) {
        suiteStartTime = System.currentTimeMillis();
        logger.info("=================================================================");
        logger.info("SUITE STARTED: {} at {}", suite.getName(), getCurrentTimestamp());
        logger.info("=================================================================");
    }

    @Override
    public void onFinish(ISuite suite) {
        long duration = System.currentTimeMillis() - suiteStartTime;
        logger.info("=================================================================");
        logger.info("SUITE FINISHED: {} at {}", suite.getName(), getCurrentTimestamp());
        logger.info("SUITE DURATION: {} ms ({} seconds)", duration, duration / 1000.0);
        logger.info("=================================================================");
    }

    // Test level methods
    @Override
    public void onTestStart(ITestResult result) {
        testStartTime = System.currentTimeMillis();
        logger.info("▶️ TEST STARTED: {} - {}", 
            result.getTestClass().getName(), 
            result.getMethod().getMethodName());
        logger.info("   Description: {}", 
            result.getMethod().getDescription() != null ? 
            result.getMethod().getDescription() : "No description provided");
        logger.info("   Groups: {}", java.util.Arrays.toString(result.getMethod().getGroups()));
        logger.info("   Thread ID: {}", Thread.currentThread().getId());
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        long duration = System.currentTimeMillis() - testStartTime;
        logger.info("✅ TEST PASSED: {} - {} (Duration: {} ms)", 
            result.getTestClass().getName(), 
            result.getMethod().getMethodName(), 
            duration);
    }

    @Override
    public void onTestFailure(ITestResult result) {
        long duration = System.currentTimeMillis() - testStartTime;
        logger.error("❌ TEST FAILED: {} - {} (Duration: {} ms)", 
            result.getTestClass().getName(), 
            result.getMethod().getMethodName(), 
            duration);
        
        // Log failure reason
        Throwable throwable = result.getThrowable();
        if (throwable != null) {
            logger.error("   Failure Reason: {}", throwable.getMessage());
            logger.error("   Stack Trace: ", throwable);
        }
        
        // Log test parameters if any
        Object[] parameters = result.getParameters();
        if (parameters != null && parameters.length > 0) {
            logger.error("   Test Parameters: {}", java.util.Arrays.toString(parameters));
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        long duration = System.currentTimeMillis() - testStartTime;
        logger.warn("⏭️ TEST SKIPPED: {} - {} (Duration: {} ms)", 
            result.getTestClass().getName(), 
            result.getMethod().getMethodName(), 
            duration);
        
        // Log skip reason
        Throwable throwable = result.getThrowable();
        if (throwable != null) {
            logger.warn("   Skip Reason: {}", throwable.getMessage());
        }
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        logger.warn("⚠️ TEST FAILED BUT WITHIN SUCCESS PERCENTAGE: {} - {}", 
            result.getTestClass().getName(), 
            result.getMethod().getMethodName());
    }

    // Configuration methods (for @BeforeMethod, @AfterMethod, etc.)
    @Override
    public void beforeInvocation(IInvokedMethod method, ITestResult testResult) {
        if (method.isConfigurationMethod()) {
            logger.debug("🔧 CONFIG METHOD STARTED: {} - {}", 
                method.getTestMethod().getTestClass().getName(),
                method.getTestMethod().getMethodName());
        }
    }

    @Override
    public void afterInvocation(IInvokedMethod method, ITestResult testResult) {
        if (method.isConfigurationMethod()) {
            if (testResult.getStatus() == ITestResult.FAILURE) {
                logger.error("🔧❌ CONFIG METHOD FAILED: {} - {}", 
                    method.getTestMethod().getTestClass().getName(),
                    method.getTestMethod().getMethodName());
                
                Throwable throwable = testResult.getThrowable();
                if (throwable != null) {
                    logger.error("   Configuration Failure: {}", throwable.getMessage());
                }
            } else {
                logger.debug("🔧✅ CONFIG METHOD COMPLETED: {} - {}", 
                    method.getTestMethod().getTestClass().getName(),
                    method.getTestMethod().getMethodName());
            }
        }
    }

    // Test context methods
    @Override
    public void onStart(ITestContext context) {
        logger.info("📋 TEST CONTEXT STARTED: {}", context.getName());
        logger.info("   Included Methods: {}", context.getIncludedGroups().length);
        logger.info("   Excluded Methods: {}", context.getExcludedGroups().length);
        logger.info("   Parallel Mode: {}", context.getCurrentXmlTest().getParallel());
        logger.info("   Thread Count: {}", context.getCurrentXmlTest().getThreadCount());
    }

    @Override
    public void onFinish(ITestContext context) {
        logger.info("📋 TEST CONTEXT FINISHED: {}", context.getName());
        logger.info("   Tests Run: {}", 
            context.getPassedTests().size() + 
            context.getFailedTests().size() + 
            context.getSkippedTests().size());
        logger.info("   Passed: {}", context.getPassedTests().size());
        logger.info("   Failed: {}", context.getFailedTests().size());
        logger.info("   Skipped: {}", context.getSkippedTests().size());
        
        // Log failed test names
        if (context.getFailedTests().size() > 0) {
            logger.error("   Failed Tests:");
            context.getFailedTests().getAllResults().forEach(result -> 
                logger.error("     - {}.{}", 
                    result.getTestClass().getName(), 
                    result.getMethod().getMethodName()));
        }
    }

    /**
     * Get current timestamp as formatted string
     */
    private String getCurrentTimestamp() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }
}