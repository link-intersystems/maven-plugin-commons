package com.link_intersystems.maven.logging;

import com.link_intersystems.test.io.ReaderAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.StringReader;
import java.io.StringWriter;

/**
 * @author René Link {@literal <rene.link@link-intersystems.com>}
 */
class PrintWriterLogTest {

    private PrintWriterLog log;
    private StringWriter sw;

    @BeforeEach
    private void setUp() {
        sw = new StringWriter();
        log = new PrintWriterLog(sw);
    }

    @Test
    public void log() {
        log.setInfoEnabled(true);
        log.info("Test");

        ReaderAssertions outputAssertions = new ReaderAssertions(new StringReader(sw.toString()));
        outputAssertions.assertLine("[info] Test");
    }

    @Test
    public void logException() {
        log.setInfoEnabled(true);
        RuntimeException e = new RuntimeException();
        log.info(e);

        ReaderAssertions outputAssertions = new ReaderAssertions(new StringReader(sw.toString()));
        outputAssertions.assertLine("[info] java.lang.RuntimeException");
    }

    @Test
    public void logMessageWithException() {
        log.setInfoEnabled(true);
        RuntimeException e = new RuntimeException();
        log.info("Test", e);

        ReaderAssertions outputAssertions = new ReaderAssertions(new StringReader(sw.toString()));
        outputAssertions.assertLine("[info] Test");
        outputAssertions.assertEmptyLine();
        outputAssertions.assertLine("java.lang.RuntimeException");
    }
}