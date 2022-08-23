package com.link_intersystems.maven.logging;

import org.apache.maven.plugin.logging.Log;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * @author René Link {@literal <rene.link@link-intersystems.com>}
 */
class ThreadAwareLogTest {

    private ExecutorService executorService;
    private ThreadAwareLog threadAwareLog;
    private Log log;

    @BeforeEach
    void setUp() {
        executorService = Executors.newSingleThreadExecutor(r -> {
            Thread thread = new Thread(r);
            thread.setName("ThreadAwareLogTest");
            return thread;
        });

        log = Mockito.mock(Log.class);
        threadAwareLog = new ThreadAwareLog(log);
    }

    @Test
    void info() throws ExecutionException, InterruptedException {
        executorService.submit(() -> {
            threadAwareLog.info("Hello");
        }).get();

        verify(log, times(1)).info(CharSequenceMatcher.eq("<ThreadAwareLogTest> Hello"));
    }

    @Test
    void debug() throws ExecutionException, InterruptedException {
        executorService.submit(() -> {
            threadAwareLog.debug("Hello");
        }).get();

        verify(log, times(1)).debug(CharSequenceMatcher.eq("<ThreadAwareLogTest> Hello"));
    }

    @Test
    void error() throws ExecutionException, InterruptedException {
        executorService.submit(() -> {
            threadAwareLog.error("Hello");
        }).get();

        verify(log, times(1)).error(CharSequenceMatcher.eq("<ThreadAwareLogTest> Hello"));
    }

    @Test
    void warn() throws ExecutionException, InterruptedException {
        executorService.submit(() -> {
            threadAwareLog.warn("Hello");
        }).get();

        verify(log, times(1)).warn(CharSequenceMatcher.eq("<ThreadAwareLogTest> Hello"));
    }


}