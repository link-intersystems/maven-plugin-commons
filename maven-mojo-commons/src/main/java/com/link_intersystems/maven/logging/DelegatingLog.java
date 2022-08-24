package com.link_intersystems.maven.logging;

import org.apache.maven.plugin.logging.Log;

import static java.util.Objects.requireNonNull;

/**
 * @author René Link {@literal <rene.link@link-intersystems.com>}
 */
public class DelegatingLog extends AbstractLog {

    private Log log = OffLog.INSTANCE;

    public DelegatingLog() {
    }

    public DelegatingLog(Log log) {
        this.log = requireNonNull(log);
    }

    public void setLog(Log log) {
        this.log = requireNonNull(log);
    }

    public Log getLog() {
        return log;
    }

    @Override
    protected void logLevel(Level level, CharSequence content) {
        level.log(getLog(), content);
    }

    @Override
    protected void logLevel(Level level, CharSequence content, Throwable error) {
        level.log(getLog(), content, error);
    }

    @Override
    protected void logLevel(Level level, Throwable error) {
        level.log(getLog(), error);
    }

    @Override
    protected boolean isLevelEnabled(Level level) {
        return level.isEnabled(getLog());
    }
}
