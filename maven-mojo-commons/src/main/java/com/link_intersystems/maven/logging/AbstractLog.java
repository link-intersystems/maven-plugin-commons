package com.link_intersystems.maven.logging;

import org.apache.maven.plugin.logging.Log;

import static com.link_intersystems.maven.logging.Level.*;

/**
 * @author René Link {@literal <rene.link@link-intersystems.com>}
 */
public abstract class AbstractLog implements Log {

    protected abstract void logLevel(Level level, CharSequence content);

    protected abstract void logLevel(Level level, CharSequence content, Throwable error);

    protected abstract void logLevel(Level level, Throwable error);

    protected void tryLogLevel(Level level, CharSequence content) {
        if (isLevelEnabled(level)) {
            logLevel(level, content);
        }
    }

    protected void tryLogLevel(Level level, CharSequence content, Throwable error) {
        if (isLevelEnabled(level)) {
            logLevel(level, content, error);
        }
    }

    protected void tryLogLevel(Level level, Throwable error) {
        if (isLevelEnabled(level)) {
            logLevel(level, error);
        }
    }

    protected abstract boolean isLevelEnabled(Level level);

    @Override
    public boolean isDebugEnabled() {
        return isLevelEnabled(debug);
    }

    @Override
    public boolean isInfoEnabled() {
        return isLevelEnabled(info);
    }

    @Override
    public boolean isWarnEnabled() {
        return isLevelEnabled(warn);
    }

    @Override
    public boolean isErrorEnabled() {
        return isLevelEnabled(error);
    }

    public void debug(CharSequence content) {
        tryLogLevel(debug, content);
    }

    public void debug(CharSequence content, Throwable error) {
        tryLogLevel(debug, content, error);
    }

    public void debug(Throwable error) {
        tryLogLevel(debug, error);
    }

    public void info(CharSequence content) {
        tryLogLevel(info, content);
    }

    public void info(CharSequence content, Throwable error) {
        tryLogLevel(info, content, error);
    }

    public void info(Throwable error) {
        tryLogLevel(info, error);
    }

    public void warn(CharSequence content) {
        tryLogLevel(warn, content);
    }

    public void warn(CharSequence content, Throwable error) {
        tryLogLevel(warn, content, error);
    }

    public void warn(Throwable error) {
        tryLogLevel(warn, error);
    }

    public void error(CharSequence content) {
        tryLogLevel(error, content);
    }

    public void error(CharSequence content, Throwable throwable) {
        tryLogLevel(error, content, throwable);
    }

    public void error(Throwable throwable) {
        tryLogLevel(error, throwable);
    }

}
