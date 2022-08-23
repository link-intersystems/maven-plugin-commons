package com.link_intersystems.maven.logging;

import org.apache.maven.plugin.logging.Log;

import java.util.function.Function;

import static java.util.Objects.requireNonNull;

/**
 * @author René Link {@literal <rene.link@link-intersystems.com>}
 */
public class ThreadAwareLog extends TransformedLog {

    private Function<Thread, String> threadFormatter = t -> "<" + t.getName() + ">";

    public ThreadAwareLog(Log log) {
        super(log);
    }

    public void setThreadFormatter(Function<Thread, String> threadFormatter) {
        this.threadFormatter = requireNonNull(threadFormatter);
    }

    @Override
    protected CharSequence transform(CharSequence content) {
        StringBuilder sb = new StringBuilder();
        String threadFormatted = threadFormatter.apply(Thread.currentThread());
        sb.append(threadFormatted);
        if (content.length() > 0) {
            sb.append(" ");
        }
        sb.append(content);
        return sb;
    }

}
