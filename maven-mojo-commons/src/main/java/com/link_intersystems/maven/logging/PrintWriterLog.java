package com.link_intersystems.maven.logging;

import java.io.PrintWriter;
import java.io.Writer;
import java.util.Optional;

import static java.util.Objects.requireNonNull;
import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;

/**
 * @author René Link {@literal <rene.link@link-intersystems.com>}
 */
public class PrintWriterLog extends AbstractLog {

    private PrintWriter printWriter;

    public PrintWriterLog(Writer writer) {
        this.printWriter = new PrintWriter(requireNonNull(writer));
    }

    protected void doPrint(Level level, Optional<CharSequence> content, Optional<Throwable> error) {
        printWriter.print("[" + level.name() + "] ");

        content.ifPresent(c -> {
            printWriter.println(c);
            error.ifPresent(e -> printWriter.println());

        });

        error.ifPresent(e -> {
            e.printStackTrace(printWriter);
        });

        printWriter.flush();
    }
}
