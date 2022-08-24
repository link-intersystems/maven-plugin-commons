package com.link_intersystems.maven.logging;

import org.apache.maven.plugin.logging.Log;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

/**
 * @author René Link {@literal <rene.link@link-intersystems.com>}
 */
class FilterLogTest {

    private Log log;
    private FilterLog filterLog;

    @BeforeEach
    void setUp() {
        log = mock(Log.class);
        filterLog = new FilterLog(log);
    }

    @Test
    void filterInfo() {
        filterLog.setLevel(Level.warn);

        filterLog.info("");

        verify(log, never()).info(CharSequenceMatcher.eq(""));
    }
}
