package com.link_intersystems.maven.logging;

import org.apache.maven.plugin.logging.Log;

import static java.util.Objects.requireNonNull;

/**
 * @author René Link {@literal <rene.link@link-intersystems.com>}
 */
public abstract class TransformedLog implements Log {
    private Log log;

    public TransformedLog(Log log) {
        this.log = requireNonNull(log);
    }

    protected abstract CharSequence transform(CharSequence content);

    @Override
    public boolean isDebugEnabled() {
        return log.isDebugEnabled();
    }

    @Override
    public void debug(CharSequence content) {
        log.debug(transform(content));
    }

    @Override
    public void debug(CharSequence content, Throwable error) {
        log.debug(transform(content), error);
    }

    @Override
    public void debug(Throwable error) {
        log.debug(transform(""), error);
    }

    @Override
    public boolean isInfoEnabled() {
        return log.isInfoEnabled();
    }

    @Override
    public void info(CharSequence content) {
        log.info(transform(content));
    }

    @Override
    public void info(CharSequence content, Throwable error) {
        log.info(transform(content), error);
    }

    @Override
    public void info(Throwable error) {
        log.info(transform(""), error);
    }

    @Override
    public boolean isWarnEnabled() {
        return log.isWarnEnabled();
    }

    @Override
    public void warn(CharSequence content) {
        log.warn(transform(content));
    }

    @Override
    public void warn(CharSequence content, Throwable error) {
        log.warn(transform(content), error);
    }

    @Override
    public void warn(Throwable error) {
        log.warn(transform(""), error);
    }

    @Override
    public boolean isErrorEnabled() {
        return log.isErrorEnabled();
    }

    @Override
    public void error(CharSequence content) {
        log.error(transform(content));
    }

    @Override
    public void error(CharSequence content, Throwable error) {
        log.error(transform(content), error);
    }

    @Override
    public void error(Throwable error) {
        log.error(transform(""), error);
    }
}
