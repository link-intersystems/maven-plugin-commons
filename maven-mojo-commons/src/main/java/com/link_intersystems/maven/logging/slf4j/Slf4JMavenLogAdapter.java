package com.link_intersystems.maven.logging.slf4j;

import com.link_intersystems.maven.logging.Level;
import org.apache.maven.plugin.logging.Log;
import org.slf4j.Logger;
import org.slf4j.Marker;
import org.slf4j.helpers.MessageFormatter;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static java.util.Objects.requireNonNull;

/**
 * @author René Link {@literal <rene.link@link-intersystems.com>}
 */
public class Slf4JMavenLogAdapter implements Logger {

    public static final Object[] EMPTY_ARGS = new Object[0];
    private Log log;
    private String name;

    private Level traceLevel = Level.off;

    public Slf4JMavenLogAdapter(Log log) {
        this(log, Slf4JMavenLogAdapter.class.getName());
    }

    public Slf4JMavenLogAdapter(Log log, String name) {
        this.log = requireNonNull(log);
        this.name = requireNonNull(name);
    }

    public void setTraceLevel(Level traceLevel) {
        this.traceLevel = requireNonNull(traceLevel);
    }

    private void logInfo(String format, Object[] args) {
        log(format, args, log::info, log::info);
    }


    private void logWarn(String format, Object[] args) {
        log(format, args, log::warn, log::warn);
    }

    private void logDebug(String format, Object[] args) {
        log(format, args, log::debug, log::debug);
    }

    private void logError(String format, Object[] args) {
        log(format, args, log::error, log::error);
    }

    private void logTrace(String format, Object[] args) {
        log(format, args, msg -> traceLevel.log(log, msg), (msg, t) -> traceLevel.log(log, msg, t));
    }

    private void log(String format, Object[] arg, Consumer<CharSequence> msgLogger, BiConsumer<CharSequence, Throwable> msgWithThrowable) {
        Throwable throwable = extractThrowable(arg);
        if (throwable != null) {
            arg = trimmedCopy(arg);
        }

        String formattedMessage = MessageFormatter.arrayFormat(format, arg).getMessage();

        if (throwable == null) {
            msgLogger.accept(formattedMessage);
        } else {
            msgWithThrowable.accept(formattedMessage, throwable);
        }
    }

    static final Throwable extractThrowable(Object[] argArray) {
        if (argArray == null || argArray.length == 0) {
            return null;
        }

        final Object lastEntry = argArray[argArray.length - 1];
        if (lastEntry instanceof Throwable) {
            return (Throwable) lastEntry;
        }
        return null;
    }

    private static Object[] trimmedCopy(Object[] argArray) {
        if (argArray == null || argArray.length == 0) {
            throw new IllegalStateException("non-sensical empty or null argument array");
        }
        final int trimemdLen = argArray.length - 1;
        Object[] trimmed = new Object[trimemdLen];
        System.arraycopy(argArray, 0, trimmed, 0, trimemdLen);
        return trimmed;
    }


    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isTraceEnabled() {
        return traceLevel.isEnabled(log);
    }

    @Override
    public void trace(String msg) {
        logTrace(msg, EMPTY_ARGS);
    }

    @Override
    public void trace(String format, Object arg) {
        logTrace(format, new Object[]{arg});
    }

    @Override
    public void trace(String format, Object arg1, Object arg2) {
        logTrace(format, new Object[]{arg1, arg2});
    }

    @Override
    public void trace(String format, Object... arguments) {
        logTrace(format, arguments);
    }

    @Override
    public void trace(String msg, Throwable t) {
        logTrace(msg, new Object[]{t});
    }

    @Override
    public boolean isTraceEnabled(Marker marker) {
        return traceLevel.isEnabled(log);
    }

    @Override
    public void trace(Marker marker, String msg) {
        logTrace(msg, EMPTY_ARGS);
    }

    @Override
    public void trace(Marker marker, String format, Object arg) {
        logTrace(format, new Object[]{arg});
    }

    @Override
    public void trace(Marker marker, String format, Object arg1, Object arg2) {
        logTrace(format, new Object[]{arg1, arg2});
    }

    @Override
    public void trace(Marker marker, String format, Object... argArray) {
        logTrace(format, argArray);
    }

    @Override
    public void trace(Marker marker, String msg, Throwable t) {
        logTrace(msg, new Object[]{t});
    }

    @Override
    public boolean isDebugEnabled() {
        return log.isDebugEnabled();
    }

    @Override
    public void debug(String msg) {
        log.debug(msg);
    }

    @Override
    public void debug(String format, Object arg) {
        if (!isDebugEnabled()) {
            return;
        }

        logDebug(format, new Object[]{arg});
    }

    @Override
    public void debug(String format, Object arg1, Object arg2) {
        if (!isDebugEnabled()) {
            return;
        }
        logDebug(format, new Object[]{arg1, arg2});
    }

    @Override
    public void debug(String format, Object... arguments) {
        if (!isDebugEnabled()) {
            return;
        }
        logDebug(format, arguments);
    }

    @Override
    public void debug(String msg, Throwable t) {
        log.debug(msg, t);
    }

    @Override
    public boolean isDebugEnabled(Marker marker) {
        return log.isDebugEnabled();
    }

    @Override
    public void debug(Marker marker, String msg) {
        log.debug(msg);
    }

    @Override
    public void debug(Marker marker, String format, Object arg) {
        if (!isDebugEnabled()) {
            return;
        }
        logDebug(format, new Object[]{arg});
    }

    @Override
    public void debug(Marker marker, String format, Object arg1, Object arg2) {
        if (!isDebugEnabled()) {
            return;
        }
        logDebug(format, new Object[]{arg1, arg2});
    }

    @Override
    public void debug(Marker marker, String format, Object... arguments) {
        if (!isDebugEnabled()) {
            return;
        }
        logDebug(format, arguments);
    }

    @Override
    public void debug(Marker marker, String msg, Throwable t) {
        log.debug(msg, t);
    }

    @Override
    public boolean isInfoEnabled() {
        return log.isInfoEnabled();
    }

    @Override
    public void info(String msg) {
        log.info(msg);
    }

    @Override
    public void info(String format, Object arg) {
        if (!isInfoEnabled()) {
            return;
        }
        logInfo(format, new Object[]{arg});
    }


    @Override
    public void info(String format, Object arg1, Object arg2) {
        if (!isInfoEnabled()) {
            return;
        }
        logInfo(format, new Object[]{arg1, arg2});
    }

    @Override
    public void info(String format, Object... arguments) {
        if (!isInfoEnabled()) {
            return;
        }
        logInfo(format, arguments);
    }

    @Override
    public void info(String msg, Throwable t) {
        log.info(msg, t);
    }


    @Override
    public boolean isInfoEnabled(Marker marker) {
        return log.isInfoEnabled();
    }

    @Override
    public void info(Marker marker, String msg) {
        log.info(msg);
    }

    @Override
    public void info(Marker marker, String format, Object arg) {
        if (!isInfoEnabled(marker)) {
            return;
        }
        logInfo(format, new Object[]{arg});
    }

    @Override
    public void info(Marker marker, String format, Object arg1, Object arg2) {
        if (!isInfoEnabled(marker)) {
            return;
        }
        logInfo(format, new Object[]{arg1, arg2});
    }

    @Override
    public void info(Marker marker, String format, Object... arguments) {
        if (!isInfoEnabled(marker)) {
            return;
        }
        logInfo(format, arguments);
    }

    @Override
    public void info(Marker marker, String msg, Throwable t) {
        if (!isInfoEnabled(marker)) {
            return;
        }
        log.info(msg, t);
    }

    @Override
    public boolean isWarnEnabled() {
        return log.isWarnEnabled();
    }

    @Override
    public void warn(String msg) {
        log.warn(msg);
    }

    @Override
    public void warn(String format, Object arg) {
        if (!isWarnEnabled()) {
            return;
        }
        logWarn(format, new Object[]{arg});
    }


    @Override
    public void warn(String format, Object arg1, Object arg2) {
        if (!isWarnEnabled()) {
            return;
        }
        logWarn(format, new Object[]{arg1, arg2});
    }

    @Override
    public void warn(String format, Object... arguments) {
        if (!isWarnEnabled()) {
            return;
        }
        logWarn(format, arguments);
    }

    @Override
    public void warn(String msg, Throwable t) {
        log.warn(msg, t);
    }


    @Override
    public boolean isWarnEnabled(Marker marker) {
        return log.isWarnEnabled();
    }

    @Override
    public void warn(Marker marker, String msg) {
        log.warn(msg);
    }

    @Override
    public void warn(Marker marker, String format, Object arg) {
        if (!isWarnEnabled(marker)) {
            return;
        }
        logWarn(format, new Object[]{arg});
    }

    @Override
    public void warn(Marker marker, String format, Object arg1, Object arg2) {
        if (!isWarnEnabled(marker)) {
            return;
        }
        logWarn(format, new Object[]{arg1, arg2});
    }

    @Override
    public void warn(Marker marker, String format, Object... arguments) {
        if (!isWarnEnabled(marker)) {
            return;
        }
        logWarn(format, arguments);
    }

    @Override
    public void warn(Marker marker, String msg, Throwable t) {
        if (!isWarnEnabled(marker)) {
            return;
        }

        log.warn(msg, t);
    }


    @Override
    public boolean isErrorEnabled() {
        return log.isErrorEnabled();
    }

    @Override
    public void error(String msg) {
        log.error(msg);
    }

    @Override
    public void error(String format, Object arg) {
        if (!isErrorEnabled()) {
            return;
        }
        logError(format, new Object[]{arg});
    }

    @Override
    public void error(String format, Object arg1, Object arg2) {
        if (!isErrorEnabled()) {
            return;
        }
        logError(format, new Object[]{arg1, arg2});
    }

    @Override
    public void error(String format, Object... arguments) {
        if (!isErrorEnabled()) {
            return;
        }
        logError(format, arguments);
    }

    @Override
    public void error(String msg, Throwable t) {
        log.error(msg, t);
    }


    @Override
    public boolean isErrorEnabled(Marker marker) {
        return log.isErrorEnabled();
    }

    @Override
    public void error(Marker marker, String msg) {
        log.error(msg);
    }

    @Override
    public void error(Marker marker, String format, Object arg) {
        if (!isErrorEnabled(marker)) {
            return;
        }
        logError(format, new Object[]{arg});
    }

    @Override
    public void error(Marker marker, String format, Object arg1, Object arg2) {
        if (!isErrorEnabled(marker)) {
            return;
        }
        logError(format, new Object[]{arg1, arg2});
    }

    @Override
    public void error(Marker marker, String format, Object... arguments) {
        if (!isErrorEnabled(marker)) {
            return;
        }
        logError(format, arguments);
    }

    @Override
    public void error(Marker marker, String msg, Throwable t) {
        if (!isErrorEnabled(marker)) {
            return;
        }

        log.error(msg, t);
    }
}
