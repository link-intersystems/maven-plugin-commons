package com.link_intersystems.maven.logging;

import org.apache.maven.plugin.logging.Log;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

/**
 * @author René Link {@literal <rene.link@link-intersystems.com>}
 */
class DelegatingLogTest {

    private Log log;
    private DelegatingLog delegatingLog;

    @BeforeEach
    void setUp() {
        log = mock(Log.class);
        delegatingLog = new DelegatingLog(log);
    }


    @Test
    void debug() {
        doReturn(true).when(log).isDebugEnabled();

        RuntimeException error = new RuntimeException();
        String content = "test";

        delegatingLog.debug(content);
        delegatingLog.debug(content, error);
        delegatingLog.debug(error);

        verify(log, times(1)).debug(CharSequenceMatcher.eq(content));
        verify(log, times(1)).debug(CharSequenceMatcher.eq(content), eq(error));
        verify(log, times(1)).debug(eq(error));
    }

    @Test
    void info() {
        doReturn(true).when(log).isInfoEnabled();

        RuntimeException error = new RuntimeException();
        String content = "test";

        delegatingLog.info(content);
        delegatingLog.info(content, error);
        delegatingLog.info(error);

        verify(log, times(1)).info(CharSequenceMatcher.eq(content));
        verify(log, times(1)).info(CharSequenceMatcher.eq(content), eq(error));
        verify(log, times(1)).info(eq(error));
    }

    @Test
    void warn() {
        doReturn(true).when(log).isWarnEnabled();

        RuntimeException error = new RuntimeException();
        String content = "test";

        delegatingLog.warn(content);
        delegatingLog.warn(content, error);
        delegatingLog.warn(error);

        verify(log, times(1)).warn(CharSequenceMatcher.eq(content));
        verify(log, times(1)).warn(CharSequenceMatcher.eq(content), eq(error));
        verify(log, times(1)).warn(eq(error));
    }

    @Test
    void error() {
        doReturn(true).when(log).isErrorEnabled();

        RuntimeException error = new RuntimeException();
        String content = "test";

        delegatingLog.error(content);
        delegatingLog.error(content, error);
        delegatingLog.error(error);

        verify(log, times(1)).error(CharSequenceMatcher.eq(content));
        verify(log, times(1)).error(CharSequenceMatcher.eq(content), eq(error));
        verify(log, times(1)).error(eq(error));
    }

    @Test
    void setDelegate() {
        delegatingLog.setLog(log);

        doReturn(true).when(log).isInfoEnabled();

        delegatingLog.info("test");

        verify(log, times(1)).info(CharSequenceMatcher.eq("test"));
    }

}