package com.link_intersystems.maven;


import com.link_intersystems.io.CharSequenceDetector;
import org.codehaus.plexus.interpolation.ValueSource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PushbackReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class StreamingInterpolator extends Reader {

    private List<ValueSource> valueSources = new ArrayList<>();

    public static final String DEFAULT_START_EXPR = "${";

    public static final String DEFAULT_END_EXPR = "}";

    private PushbackReader reader;
    private String startExpr;
    private String endExpr;

    private String escapeString;
    private int escapeCount = 0;

    private StringBuilder pushback = new StringBuilder();
    private CharSequenceDetector charSequenceDetector = new CharSequenceDetector();


    public StreamingInterpolator(Reader reader) {
        this(reader, DEFAULT_START_EXPR, DEFAULT_END_EXPR);
    }

    public StreamingInterpolator(Reader reader, String startExpr, String endExpr) {
        this.reader = new PushbackReader(new BufferedReader(reader), 25);
        this.startExpr = startExpr;
        this.endExpr = endExpr;
    }

    public String getEscapeString() {
        return escapeString;
    }

    public void setEscapeString(String escapeString) {
        this.escapeString = escapeString;
    }

    public void addValueSource(ValueSource valueSource) {
        valueSources.add(valueSource);
    }

    public void removeValuesSource(ValueSource valueSource) {
        valueSources.remove(valueSource);
    }

    @Override
    public int read(char[] cbuf, int off, int len) throws IOException {
        int readChars = 0;
        while (len-- > 0) {
            if (escapeCount == 0 && escapeString != null && charSequenceDetector.detect(reader, escapeString)) {
                escapeCount = escapeString.length() + 1;
                len++;
                reader.unread(escapeString.toCharArray());
                continue;
            }

            if (escapeCount == 0 && charSequenceDetector.detect(reader, startExpr)) {
                StringBuilder interpolationBuffer = new StringBuilder();

                boolean endSequenceDetected;
                while (!(endSequenceDetected = charSequenceDetector.detect(reader, endExpr))) {
                    int read = reader.read();
                    if (read == -1) {
                        break;
                    }

                    interpolationBuffer.append((char) read);
                }

                if (endSequenceDetected) {
                    String interpolated = interpolate(interpolationBuffer.toString());
                    if (interpolated == null) {
                        reader.unread(endExpr.toCharArray());
                        reader.unread(interpolationBuffer.toString().toCharArray());
                        reader.unread(startExpr.toCharArray());
                    } else {
                        reader.unread(interpolated.toCharArray());
                    }
                } else {
                    reader.unread(interpolationBuffer.toString().toCharArray());
                }
            }

            int read = reader.read();
            if (read == -1) {
                break;
            }
            if (escapeCount > 0) {
                escapeCount--;
            }
            readChars++;
            cbuf[off++] = (char) read;
        }

        if (readChars == 0) {
            return -1;
        }

        return readChars;
    }

    private String interpolate(String expression) {
        Object value = null;
        for (ValueSource valueSource : valueSources) {
            value = valueSource.getValue(expression);

            if (value != null) {
                break;
            }
        }

        if (value == null) {
            return null;
        }

        return String.valueOf(value);
    }

    @Override
    public void close() throws IOException {
        reader.close();
    }


}
