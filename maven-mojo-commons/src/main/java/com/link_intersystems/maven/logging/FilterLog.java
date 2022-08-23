package com.link_intersystems.maven.logging;

import org.apache.maven.plugin.logging.Log;

import static java.util.Objects.requireNonNull;

/**
 * @author René Link {@literal <rene.link@link-intersystems.com>}
 */
public class FilterLog extends AbstractLog {

    private Log log;

    public FilterLog(Log log) {
        this.log = requireNonNull(log);
    }

    @Override
    protected void doLogLevel(Level level, CharSequence content) {
        level.log(log, content);
    }

    @Override
    protected void doLogLevel(Level level, CharSequence content, Throwable error) {
        level.log(log, content, error);
    }

    @Override
    protected void doLogLevel(Level level, Throwable error) {
        level.log(log, error);
    }
}
