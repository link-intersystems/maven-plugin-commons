package com.link_intersystems.maven.logging;

import org.apache.maven.plugin.logging.Log;

import java.util.Optional;

import static com.link_intersystems.maven.logging.Level.*;
import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;

/**
 * @author René Link {@literal <rene.link@link-intersystems.com>}
 */
public abstract class AbstractLog implements Log {

    private boolean debugEnabled = false;
    private boolean infoEnabled = true;
    private boolean warnEnabled = true;
    private boolean errorEnabled = true;

    @Override
    public boolean isDebugEnabled() {
        return debugEnabled;
    }

    @Override
    public boolean isInfoEnabled() {
        return infoEnabled;
    }

    @Override
    public boolean isWarnEnabled() {
        return warnEnabled;
    }

    @Override
    public boolean isErrorEnabled() {
        return errorEnabled;
    }

    protected void setDebugEnabled(boolean debugEnabled) {
        this.debugEnabled = debugEnabled;
    }

    protected void setInfoEnabled(boolean infoEnabled) {
        this.infoEnabled = infoEnabled;
    }

    protected void setWarnEnabled(boolean warnEnabled) {
        this.warnEnabled = warnEnabled;
    }

    protected void setErrorEnabled(boolean errorEnabled) {
        this.errorEnabled = errorEnabled;
    }

    public void debug(CharSequence content) {
        logLevel(debug, content);
    }

    public void debug(CharSequence content, Throwable error) {
        logLevel(debug, content, error);
    }

    public void debug(Throwable error) {
        logLevel(debug, error);
    }

    public void info(CharSequence content) {
        logLevel(info, content);
    }

    public void info(CharSequence content, Throwable error) {
        logLevel(info, content, error);
    }

    public void info(Throwable error) {
        logLevel(info, error);
    }

    public void warn(CharSequence content) {
        logLevel(warn, content);
    }

    public void warn(CharSequence content, Throwable error) {
        logLevel(warn, content, error);
    }

    public void warn(Throwable error) {
        logLevel(warn, error);
    }

    public void error(CharSequence content) {
        logLevel(error, content);
    }

    public void error(CharSequence content, Throwable throwable) {
        logLevel(error, content, throwable);
    }

    public void error(Throwable throwable) {
        logLevel(error, throwable);
    }


    private void logLevel(Level level, CharSequence content) {
        logLevel(level, ofNullable(content), empty());
    }

    private void logLevel(Level level, Throwable error) {
        logLevel(level, empty(), ofNullable(error));
    }

    private void logLevel(Level level, CharSequence content, Throwable error) {
        logLevel(level, ofNullable(content), ofNullable(error));
    }

    protected void logLevel(Level level, Optional<CharSequence> content, Optional<Throwable> error) {
        if (level.isEnabled(this)) {
            if (content.isPresent()) {
                if (error.isPresent()) {
                    doLogLevel(level, content.get(), error.get());
                } else {
                    doLogLevel(level, content.get());
                }
            } else if (error.isPresent()) {
                doLogLevel(level, error.get());
            }
        }
    }

    protected abstract void doLogLevel(Level level, CharSequence content);

    protected abstract void doLogLevel(Level level, CharSequence content, Throwable error);

    protected abstract void doLogLevel(Level level, Throwable error);
}
