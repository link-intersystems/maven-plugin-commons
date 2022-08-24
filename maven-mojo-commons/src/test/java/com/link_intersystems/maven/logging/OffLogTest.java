package com.link_intersystems.maven.logging;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

/**
 * @author René Link {@literal <rene.link@link-intersystems.com>}
 */
class OffLogTest {

    private OffLog offLog;

    @BeforeEach
    void setUp() {
        offLog = spy(new OffLog());
    }

    @Test
    void allLevelDisabled() {
        RuntimeException error = new RuntimeException();
        String content = "test";

        offLog.debug(content);
        offLog.debug(content, error);
        offLog.debug(error);
        verify(offLog, never()).logLevel(Level.debug, content);
        verify(offLog, never()).logLevel(Level.debug, content, error);
        verify(offLog, never()).logLevel(Level.debug, error);

        offLog.info(content);
        offLog.info(content, error);
        offLog.info(error);
        verify(offLog, never()).logLevel(Level.info, content);
        verify(offLog, never()).logLevel(Level.info, content, error);
        verify(offLog, never()).logLevel(Level.info, error);


        offLog.warn(content);
        offLog.warn(content, error);
        offLog.warn(error);
        verify(offLog, never()).logLevel(Level.warn, content);
        verify(offLog, never()).logLevel(Level.warn, content, error);
        verify(offLog, never()).logLevel(Level.warn, error);

        offLog.error(content);
        offLog.error(content, error);
        offLog.error(error);
        verify(offLog, never()).logLevel(Level.error, content);
        verify(offLog, never()).logLevel(Level.error, content, error);
        verify(offLog, never()).logLevel(Level.error, error);
    }
}