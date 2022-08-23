package com.link_intersystems.maven.logging;

import java.io.PrintWriter;
import java.io.Writer;

import static java.util.Objects.requireNonNull;

/**
 * @author René Link {@literal <rene.link@link-intersystems.com>}
 */
public class PrintWriterLog extends AbstractLog {

    private PrintWriter printWriter;

    public PrintWriterLog(Writer writer) {
        this.printWriter = new PrintWriter(requireNonNull(writer));
    }

    @Override
    protected void doLogLevel(Level level, CharSequence content) {
        printWriter.print("[" + level.name() + "] ");

        printWriter.println(content);

        printWriter.flush();
    }

    protected void doLogLevel(Level level, CharSequence content, Throwable error) {
        printWriter.print("[" + level.name() + "] ");

        printWriter.println(content);
        printWriter.println();
        error.printStackTrace(printWriter);

        printWriter.flush();
    }

    @Override
    protected void doLogLevel(Level level, Throwable error) {
        printWriter.print("[" + level.name() + "] ");

        error.printStackTrace(printWriter);

        printWriter.flush();
    }
}
