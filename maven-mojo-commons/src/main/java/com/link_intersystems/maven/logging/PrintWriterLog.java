package com.link_intersystems.maven.logging;

import java.io.PrintWriter;
import java.io.Writer;

import static java.util.Objects.requireNonNull;

/**
 * @author René Link {@literal <rene.link@link-intersystems.com>}
 */
public class PrintWriterLog extends AbstractCustomEnablementLog {

    private PrintWriter printWriter;

    public PrintWriterLog(Writer writer) {
        this.printWriter = new PrintWriter(requireNonNull(writer));
    }

    @Override
    public void setDebugEnabled(boolean debugEnabled) {
        super.setDebugEnabled(debugEnabled);
    }

    @Override
    public void setErrorEnabled(boolean errorEnabled) {
        super.setErrorEnabled(errorEnabled);
    }

    @Override
    public void setInfoEnabled(boolean infoEnabled) {
        super.setInfoEnabled(infoEnabled);
    }

    @Override
    public void setWarnEnabled(boolean warnEnabled) {
        super.setWarnEnabled(warnEnabled);
    }

    @Override
    protected void logLevel(Level level, CharSequence content) {
        printWriter.print("[" + level.name() + "] ");

        printWriter.println(content);

        printWriter.flush();
    }

    protected void logLevel(Level level, CharSequence content, Throwable error) {
        printWriter.print("[" + level.name() + "] ");

        printWriter.println(content);
        printWriter.println();
        error.printStackTrace(printWriter);

        printWriter.flush();
    }

    @Override
    protected void logLevel(Level level, Throwable error) {
        printWriter.print("[" + level.name() + "] ");

        error.printStackTrace(printWriter);

        printWriter.flush();
    }
}
