package com.link_intersystems.maven.logging;

import org.apache.maven.plugin.logging.Log;

import static java.util.Objects.requireNonNull;

/**
 * @author René Link {@literal <rene.link@link-intersystems.com>}
 */
public class ConcurrentLog extends AbstractLog {

    private Log log;
    private Object synchronizationMonitor;

    public ConcurrentLog(Log log) {
        this.log = requireNonNull(log);
        setSynchronizationMonitor(log);
    }

    /**
     * The monitor object to use for synchronization. The default is the {@link Log} that
     * this {@link ConcurrentLog} was constructed with.
     */
    public void setSynchronizationMonitor(Object synchronizationMonitor) {
        this.synchronizationMonitor = requireNonNull(synchronizationMonitor);
    }


    @Override
    protected void logLevel(Level level, CharSequence content) {
        synchronized (synchronizationMonitor) {
            level.log(log, content);
        }
    }

    @Override
    protected void logLevel(Level level, CharSequence content, Throwable error) {
        synchronized (synchronizationMonitor) {
            level.log(log, content, error);
        }
    }

    @Override
    protected void logLevel(Level level, Throwable error) {
        synchronized (synchronizationMonitor) {
            level.log(log, error);
        }
    }

    @Override
    protected boolean isLevelEnabled(Level level) {
        synchronized (synchronizationMonitor) {
            return level.isEnabled(log);
        }
    }
}
