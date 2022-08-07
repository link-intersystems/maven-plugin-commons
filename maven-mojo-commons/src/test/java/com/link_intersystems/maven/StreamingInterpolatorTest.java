package com.link_intersystems.maven;

import org.codehaus.plexus.interpolation.AbstractValueSource;
import org.codehaus.plexus.interpolation.InterpolationException;
import org.codehaus.plexus.interpolation.StringSearchInterpolator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringReader;

/**
 * @author René Link {@literal <rene.link@link-intersystems.com>}
 */
class StreamingInterpolatorTest {

    private StreamingInterpolator streamingInterpolator;
    private AbstractValueSource valueSource;

    @BeforeEach
    void setUp() {
        streamingInterpolator = new StreamingInterpolator(new StringReader("<artifactId>${artifactId}</artifactId>"));
        valueSource = new AbstractValueSource(false) {
            @Override
            public Object getValue(String expression) {
                if ("artifactId".equals(expression)) {
                    return "StreamingInterpolator";
                }
                return null;
            }
        };
    }

    @AfterEach
    void tearDown() throws IOException {
        streamingInterpolator.close();
    }

    @Test
    void readWithoutInterpolation() throws IOException {
        StringBuilder sb = new StringBuilder();

        int read;
        while ((read = streamingInterpolator.read()) != -1) {
            sb.append((char) read);
        }

        Assertions.assertEquals("<artifactId>${artifactId}</artifactId>", sb.toString());

    }

    @Test
    void interpolate() throws IOException {
        streamingInterpolator.addValueSource(valueSource);

        StringBuilder sb = new StringBuilder();

        int read;
        while ((read = streamingInterpolator.read()) != -1) {
            sb.append((char) read);
        }

        Assertions.assertEquals("<artifactId>StreamingInterpolator</artifactId>", sb.toString());

    }

    @Test
    void escaped() throws IOException {
        streamingInterpolator.close();
        streamingInterpolator = new StreamingInterpolator(new StringReader("<artifactId>\\$${artifactId}</artifactId>"));
        streamingInterpolator.addValueSource(valueSource);
        streamingInterpolator.setEscapeString("\\$");
        StringBuilder sb = new StringBuilder();

        int read;
        while ((read = streamingInterpolator.read()) != -1) {
            sb.append((char) read);
        }

        Assertions.assertEquals("<artifactId>\\$${artifactId}</artifactId>", sb.toString());

    }
}