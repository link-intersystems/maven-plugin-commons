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

    public void setDebugEnabled(boolean debugEnabled) {
        this.debugEnabled = debugEnabled;
    }

    public void setInfoEnabled(boolean infoEnabled) {
        this.infoEnabled = infoEnabled;
    }

    public void setWarnEnabled(boolean warnEnabled) {
        this.warnEnabled = warnEnabled;
    }

    public void setErrorEnabled(boolean errorEnabled) {
        this.errorEnabled = errorEnabled;
    }

    public void debug(CharSequence content) {
        print(debug, content);
    }

    public void debug(CharSequence content, Throwable error) {
        print(debug, content, error);
    }

    public void debug(Throwable error) {
        print(debug, error);
    }

    public void info(CharSequence content) {
        print(info, content);
    }

    public void info(CharSequence content, Throwable error) {
        print(info, content, error);
    }

    public void info(Throwable error) {
        print(info, error);
    }

    public void warn(CharSequence content) {
        print(warn, content);
    }

    public void warn(CharSequence content, Throwable error) {
        print(warn, content, error);
    }

    public void warn(Throwable error) {
        print(warn, error);
    }

    public void error(CharSequence content) {
        print(error, content);
    }

    public void error(CharSequence content, Throwable throwable) {
        print(error, content, throwable);
    }

    public void error(Throwable throwable) {
        print(error, throwable);
    }


    private void print(Level level, CharSequence content) {
        print(level, ofNullable(content), empty());
    }

    private void print(Level level, Throwable error) {
        print(level, empty(), ofNullable(error));
    }

    private void print(Level level, CharSequence content, Throwable error) {
        print(level, ofNullable(content), ofNullable(error));
    }

    protected void print(Level level, Optional<CharSequence> content, Optional<Throwable> error) {
        if (level.isEnabled(this)) {
            doPrint(level, content, error);
        }
    }

    protected abstract void doPrint(Level level, Optional<CharSequence> content, Optional<Throwable> error);
}
