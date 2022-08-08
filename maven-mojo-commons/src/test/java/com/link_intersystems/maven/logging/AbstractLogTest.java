package com.link_intersystems.maven.logging;

import com.link_intersystems.maven.OutputAssertion;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.StringWriter;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * @author René Link {@literal <rene.link@link-intersystems.com>}
 */
class AbstractLogTest {

    private static class DoPrint {
        private Level level;
        private Optional<CharSequence> content;
        private Optional<Throwable> error;

        public DoPrint(Level level, Optional<CharSequence> content, Optional<Throwable> error) {
            this.level = level;
            this.content = content;
            this.error = error;
        }

        public void assertLevel(Level expectedLevel) {
            assertEquals(this.level, expectedLevel);
        }

        public void assertContent(String expectedContent) {
            assertEquals(expectedContent, content.orElse(null));
        }

        public void assertThrowable(Throwable expectedThrowable) {
            assertEquals(expectedThrowable, error.orElse(null));
        }
    }

    private AbstractLog log;
    private StringWriter sw;
    private OutputAssertion outputAssertion;

    private DoPrint latestPrint;

    @BeforeEach
    private void setUp() {
        sw = new StringWriter();
        log = new AbstractLog() {

            @Override
            protected void doPrint(Level level, Optional<CharSequence> content, Optional<Throwable> error) {
                latestPrint = new DoPrint(level, content, error);
            }
        };


        outputAssertion = new OutputAssertion(sw.getBuffer());
    }

    @AfterEach
    void tearDown() {
        latestPrint = null;
    }

    public static Stream<Level> levels() {
        return Arrays.stream(Level.values());
    }

    @ParameterizedTest
    @MethodSource("levels")
    public void log(Level level) {
        level.setEnabled(log, false);
        assertNull(latestPrint);

        level.setEnabled(log, true);
        level.log(log, "Test");

        latestPrint.assertLevel(level);
        latestPrint.assertContent("Test");
    }

    @ParameterizedTest
    @MethodSource("levels")
    public void logException(Level level) {
        RuntimeException e = new RuntimeException();
        level.setEnabled(log, false);

        level.log(log, e);
        assertNull(latestPrint);


        level.setEnabled(log, true);
        level.log(log, e);

        latestPrint.assertLevel(level);
        latestPrint.assertThrowable(e);

    }

    @ParameterizedTest
    @MethodSource("levels")
    public void logMessageWithException(Level level) {
        RuntimeException e = new RuntimeException();
        level.setEnabled(log, false);
        level.log(log, e);
        assertNull(latestPrint);


        level.setEnabled(log, true);
        level.log(log, "Test", e);

        latestPrint.assertLevel(level);
        latestPrint.assertContent("Test");
        latestPrint.assertThrowable(e);
    }


}