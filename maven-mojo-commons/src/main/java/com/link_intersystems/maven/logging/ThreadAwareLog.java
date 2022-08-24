package com.link_intersystems.maven.logging;

import org.apache.maven.plugin.logging.Log;

import java.util.Optional;
import java.util.function.Function;

import static java.util.Objects.requireNonNull;

/**
 * @author René Link {@literal <rene.link@link-intersystems.com>}
 */
public class ThreadAwareLog extends AbstractInterceptedLog {

    private class ThreadFormatLogInterceptor implements LogInterceptor {

        @Override
        public void invoke(LogInvocation logInvocation, Optional<CharSequence> content, Optional<Throwable> error) {
            StringBuilder sb = new StringBuilder();
            String threadFormatted = threadFormatter.apply(Thread.currentThread());
            sb.append(threadFormatted);

            content.ifPresent(c -> {
                if (c.length() > 0) sb.append(" ");
            });
            content.ifPresent(sb::append);
            logInvocation.proceed(sb, error.orElse(null));
        }
    }

    private Function<Thread, String> threadFormatter = t -> "<" + t.getName() + ">";

    public ThreadAwareLog(Log log) {
        super(log);
        setLogInterceptor(new ThreadFormatLogInterceptor());
    }

    public void setThreadFormatter(Function<Thread, String> threadFormatter) {
        this.threadFormatter = requireNonNull(threadFormatter);

    }
}
