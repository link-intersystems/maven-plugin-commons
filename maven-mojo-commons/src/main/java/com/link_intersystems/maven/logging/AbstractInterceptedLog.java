package com.link_intersystems.maven.logging;

import org.apache.maven.plugin.logging.Log;

import java.util.Optional;

import static java.util.Objects.requireNonNull;

/**
 * @author René Link {@literal <rene.link@link-intersystems.com>}
 */
public abstract class AbstractInterceptedLog extends DelegatingLog {
    private LogInterceptor logInterceptor = new ProceedingLogInterceptor();
    private EnabledInterceptor enabledInterceptor = new ProceedingEnabledInterceptor();

    protected AbstractInterceptedLog() {
    }

    protected AbstractInterceptedLog(Log log) {
        super(log);
    }

    protected void setLogInterceptor(LogInterceptor logInterceptor) {
        this.logInterceptor = requireNonNull(logInterceptor);
    }

    protected void setEnabledInterceptor(EnabledInterceptor enabledInterceptor) {
        this.enabledInterceptor = requireNonNull(enabledInterceptor);
    }

    @Override
    protected void logLevel(Level level, CharSequence content) {
        logInterceptor.invoke(
                (c, e) -> AbstractInterceptedLog.super.logLevel(level, c),
                Optional.ofNullable(content),
                Optional.empty()
        );
    }

    @Override
    protected void logLevel(Level level, CharSequence content, Throwable error) {
        logInterceptor.invoke(
                (c, e) -> AbstractInterceptedLog.super.logLevel(level, c, e),
                Optional.ofNullable(content),
                Optional.ofNullable(error)
        );
    }

    @Override
    protected void logLevel(Level level, Throwable error) {
        logInterceptor.invoke(
                (c, e) -> AbstractInterceptedLog.super.logLevel(level, e),
                Optional.empty(),
                Optional.ofNullable(error)
        );
    }

    @Override
    protected boolean isLevelEnabled(Level level) {
        return enabledInterceptor.invoke(() -> AbstractInterceptedLog.super.isLevelEnabled(level));
    }

    public static interface LogInterceptor {

        public void invoke(LogInvocation logInvocation, Optional<CharSequence> content, Optional<Throwable> error);
    }

    public static interface LogInvocation {

        public void proceed(CharSequence content, Throwable error);
    }

    public static interface EnabledInterceptor {

        public boolean invoke(EnabledInvocation enabledInvocation);
    }

    public static interface EnabledInvocation {

        public boolean proceed();
    }

    private static class ProceedingLogInterceptor implements LogInterceptor {

        @Override
        public void invoke(LogInvocation logInvocation, Optional<CharSequence> content, Optional<Throwable> error) {
            logInvocation.proceed(content.orElse(null), error.orElse(null));
        }
    }

    private static class ProceedingEnabledInterceptor implements EnabledInterceptor {

        @Override
        public boolean invoke(EnabledInvocation enabledInvocation) {
            return enabledInvocation.proceed();
        }
    }
}
