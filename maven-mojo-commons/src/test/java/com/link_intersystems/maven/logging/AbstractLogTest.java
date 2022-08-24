package com.link_intersystems.maven.logging;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

/**
 * @author René Link {@literal <rene.link@link-intersystems.com>}
 */
class AbstractLogTest {

    private static class LogEntry {
        private Level level;
        private CharSequence content;
        private Throwable error;

        public LogEntry(Level level, CharSequence content, Throwable error) {
            this.level = level;
            this.content = content;
            this.error = error;
        }

        public void assertLevel(Level expectedLevel) {
            assertEquals(this.level, expectedLevel);
        }

        public void assertContent(String expectedContent) {
            assertEquals(expectedContent, content);
        }

        public void assertThrowable(Throwable expectedThrowable) {
            assertEquals(expectedThrowable, error);
        }
    }

    private AbstractLog log;
    private StringWriter sw;

    private LogEntry latestLogEntry;

    @BeforeEach
    void setUp() {
        sw = new StringWriter();
        log = spy(AbstractLog.class);
    }

    @AfterEach
    void tearDown() {
        latestLogEntry = null;
    }


    private void enableLevel(Level level) {
        doReturn(true).when(log).isLevelEnabled(level);
    }

    private void disableLevel(Level level) {
        doReturn(false).when(log).isLevelEnabled(level);
    }


    public static Stream<Level> levels() {
        List<Level> levels = new ArrayList<>(Arrays.asList(Level.values()));
        levels.remove(Level.off);
        return levels.stream();
    }

    @ParameterizedTest
    @MethodSource("levels")
    public void logLevelEnabled(Level level) {
        enableLevel(level);

        assertEquals(log.isLevelEnabled(level), level.isEnabled(log));
    }

    @ParameterizedTest
    @MethodSource("levels")
    public void logLevelDisabled(Level level) {
        disableLevel(level);

        assertEquals(log.isLevelEnabled(level), level.isEnabled(log));
    }

    @ParameterizedTest
    @MethodSource("levels")
    public void log(Level level) {
        enableLevel(level);

        level.log(log, "Test");

        verify(log, times(1)).logLevel(
                eq(level),
                CharSequenceMatcher.eq("Test")
        );
    }

    @ParameterizedTest
    @MethodSource("levels")
    public void logDisabled(Level level) {
        disableLevel(level);

        level.log(log, "Test");

        verify(log, never()).logLevel(
                eq(level),
                CharSequenceMatcher.eq("Test")
        );
    }

    @ParameterizedTest
    @MethodSource("levels")
    public void logException(Level level) {
        RuntimeException e = new RuntimeException();
        enableLevel(level);

        level.log(log, e);

        verify(log, times(1)).isLevelEnabled(level);
        verify(log, times(1)).logLevel(
                eq(level),
                eq(e)
        );

    }

    @ParameterizedTest
    @MethodSource("levels")
    public void logExceptionDisabled(Level level) {
        RuntimeException e = new RuntimeException();
        disableLevel(level);

        level.log(log, e);

        verify(log, times(1)).isLevelEnabled(level);
        verify(log, never()).logLevel(
                eq(level),
                eq(e)
        );
    }

    @ParameterizedTest
    @MethodSource("levels")
    public void logMessageWithException(Level level) {
        RuntimeException e = new RuntimeException();
        enableLevel(level);

        level.log(log, "Test", e);

        verify(log, times(1)).isLevelEnabled(level);
        verify(log, times(1)).logLevel(
                eq(level),
                CharSequenceMatcher.eq("Test"),
                eq(e)
        );
    }

    @ParameterizedTest
    @MethodSource("levels")
    public void logMessageWithExceptionDisabled(Level level) {
        RuntimeException e = new RuntimeException();
        disableLevel(level);

        level.log(log, "Test", e);


        verify(log, times(1)).isLevelEnabled(level);
        verify(log, never()).logLevel(
                eq(level),
                CharSequenceMatcher.eq("Test"),
                eq(e)
        );
    }
}