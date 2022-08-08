package com.link_intersystems.maven;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * @author René Link {@literal <rene.link@link-intersystems.com>}
 */
public class OutputAssertion {

    private BufferedReader reader;
    private StringBuffer outputBuffer;

    public OutputAssertion(StringBuffer outputBuffer) {
        this.outputBuffer = outputBuffer;
    }

    private BufferedReader getReader() {
        if (reader == null) {
            String output = outputBuffer.toString();
            reader = new BufferedReader(new StringReader(output));
        }
        return reader;
    }

    public void reset() {
        reader = null;
    }

    public void assertEmptyLine() {
        assertLine("");
    }

    public void assertNoLine() {
        assertLine(null);
    }

    public void assertLine(String expectedLine) {
        try {
            String line = getReader().readLine();
            assertEquals(expectedLine, line);
        } catch (IOException e) {
            fail(e);
        }
    }
}
