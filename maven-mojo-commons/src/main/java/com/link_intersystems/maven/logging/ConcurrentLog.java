package com.link_intersystems.maven.logging;

import org.apache.maven.plugin.logging.Log;

import static java.util.Objects.requireNonNull;

/**
 * @author René Link {@literal <rene.link@link-intersystems.com>}
 */
public class ConcurrentLog implements Log {

    private Log log;

    public ConcurrentLog(Log log) {
        this.log = requireNonNull(log);
    }

    @Override
    public boolean isDebugEnabled() {
        return log.isDebugEnabled();
    }

    @Override
    public void debug(CharSequence content) {
        synchronized (log) {
            log.debug(content);
        }
    }

    @Override
    public void debug(CharSequence content, Throwable error) {
        synchronized (log) {
            log.debug(content, error);
        }
    }

    @Override
    public void debug(Throwable error) {
        synchronized (log) {
            log.debug(error);
        }
    }

    @Override
    public boolean isInfoEnabled() {
        synchronized (log) {
            return log.isInfoEnabled();
        }
    }

    @Override
    public void info(CharSequence content) {
        synchronized (log) {
            log.info(content);
        }
    }

    @Override
    public void info(CharSequence content, Throwable error) {
        synchronized (log) {
            log.info(content, error);
        }
    }

    @Override
    public void info(Throwable error) {
        synchronized (log) {
            log.info(error);
        }
    }

    @Override
    public boolean isWarnEnabled() {
        return log.isWarnEnabled();
    }

    @Override
    public void warn(CharSequence content) {
        synchronized (log) {
            log.warn(content);
        }
    }

    @Override
    public void warn(CharSequence content, Throwable error) {
        synchronized (log) {
            log.warn(content, error);
        }
    }

    @Override
    public void warn(Throwable error) {
        synchronized (log) {
            log.warn(error);
        }
    }

    @Override
    public boolean isErrorEnabled() {
        return log.isErrorEnabled();
    }

    @Override
    public void error(CharSequence content) {
        synchronized (log) {
            log.error(content);
        }
    }

    @Override
    public void error(CharSequence content, Throwable error) {
        synchronized (log) {
            log.error(content, error);
        }
    }

    @Override
    public void error(Throwable error) {
        synchronized (log) {
            log.error(error);
        }
    }
}
