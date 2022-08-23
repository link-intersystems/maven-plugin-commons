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
import static org.junit.jupiter.api.Assertions.assertNull;

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
        log = new AbstractLog() {

            @Override
            protected void doLogLevel(Level level, CharSequence content) {
                latestLogEntry =new LogEntry(level, content, null);
            }

            @Override
            protected void doLogLevel(Level level, CharSequence content, Throwable error) {
                latestLogEntry =new LogEntry(level, content, error);
            }

            @Override
            protected void doLogLevel(Level level, Throwable error) {
                latestLogEntry =new LogEntry(level, null, error);
            }
        };

    }

    @AfterEach
    void tearDown() {
        latestLogEntry = null;
    }

    public static Stream<Level> levels() {
        List<Level> levels = new ArrayList<>(Arrays.asList(Level.values()));
        levels.remove(Level.off);
        return levels.stream();
    }

    @ParameterizedTest
    @MethodSource("levels")
    public void log(Level level) {
        level.setEnabled(log, false);
        assertNull(latestLogEntry);

        level.setEnabled(log, true);
        level.log(log, "Test");

        latestLogEntry.assertLevel(level);
        latestLogEntry.assertContent("Test");
    }

    @ParameterizedTest
    @MethodSource("levels")
    public void logException(Level level) {
        RuntimeException e = new RuntimeException();
        level.setEnabled(log, false);

        level.log(log, e);
        assertNull(latestLogEntry);


        level.setEnabled(log, true);
        level.log(log, e);

        latestLogEntry.assertLevel(level);
        latestLogEntry.assertThrowable(e);

    }

    @ParameterizedTest
    @MethodSource("levels")
    public void logMessageWithException(Level level) {
        RuntimeException e = new RuntimeException();
        level.setEnabled(log, false);
        level.log(log, e);
        assertNull(latestLogEntry);


        level.setEnabled(log, true);
        level.log(log, "Test", e);

        latestLogEntry.assertLevel(level);
        latestLogEntry.assertContent("Test");
        latestLogEntry.assertThrowable(e);
    }


}