package com.link_intersystems.maven.logging.slf4j;

import org.apache.maven.plugin.logging.Log;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.Logger;
import org.slf4j.Marker;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * @author René Link {@literal <rene.link@link-intersystems.com>}
 */
class Slf4JMavenLogAdapterTest {

    private Log mavenLog;
    private Logger logger;
    private Marker marker;

    public static List<LoggerInvocation> getInvocations() {
        List<LoggerInvocation> loggerInvocations = new ArrayList<>();

        List<String> levels = Arrays.asList("info", "warn", "error", "debug");
        for (String level : levels) {

            List<Marker> markers = Arrays.asList(null, mock(Marker.class));

            for (Marker marker : markers) {
                loggerInvocations.add(new LoggerInvocation("Message", level, marker, "Message", null, null));
                loggerInvocations.add(new LoggerInvocation("Message Hello", level, marker, "Message {}", new Object[]{"Hello"}, null));
                loggerInvocations.add(new LoggerInvocation("Message Hello World", level, marker, "Message {} {}", new Object[]{"Hello", "World"}, null));
                loggerInvocations.add(new LoggerInvocation("Message Hello World !", level, marker, "Message {} {} {}", new Object[]{"Hello", "World", "!"}, null));
                loggerInvocations.add(new LoggerInvocation("Message", level, marker, "Message", null, new RuntimeException()));
            }
        }

        return loggerInvocations;
    }

    @BeforeEach
    void setUp() {
        mavenLog = mock(Log.class);
        marker = mock(Marker.class);
        logger = new Slf4JMavenLogAdapter(mavenLog, Slf4JMavenLogAdapterTest.class.getName());
    }

    @Test
    void getName() {
        assertEquals(Slf4JMavenLogAdapterTest.class.getName(), logger.getName());
    }

    @Test
    void isDebugEnabled() {
        assertFalse(logger.isDebugEnabled());
        assertFalse(logger.isDebugEnabled(marker));

        when(mavenLog.isDebugEnabled()).thenReturn(true);

        assertTrue(logger.isDebugEnabled());
        assertTrue(logger.isDebugEnabled(marker));
    }

    @Test
    void isInfoEnabled() {
        assertFalse(logger.isInfoEnabled());
        assertFalse(logger.isInfoEnabled(marker));

        when(mavenLog.isInfoEnabled()).thenReturn(true);

        assertTrue(logger.isInfoEnabled());
        assertTrue(logger.isInfoEnabled(marker));
    }

    @Test
    void isErrorEnabled() {
        assertFalse(logger.isErrorEnabled());
        assertFalse(logger.isErrorEnabled(marker));
        when(mavenLog.isErrorEnabled()).thenReturn(true);

        assertTrue(logger.isErrorEnabled());
        assertTrue(logger.isErrorEnabled(marker));
    }

    @Test
    void isWarnEnabled() {
        assertFalse(logger.isWarnEnabled());
        assertFalse(logger.isWarnEnabled(marker));

        when(mavenLog.isWarnEnabled()).thenReturn(true);

        assertTrue(logger.isWarnEnabled());
        assertTrue(logger.isWarnEnabled(marker));
    }

    @Test
    void isTraceEnabled() {
        assertFalse(logger.isTraceEnabled());
        assertFalse(logger.isTraceEnabled(marker));
    }

    @ParameterizedTest
    @MethodSource("getInvocations")
    void log(LoggerInvocation loggerInvocation) {
        when(mavenLog.isInfoEnabled()).thenReturn(true);
        when(mavenLog.isDebugEnabled()).thenReturn(true);
        when(mavenLog.isErrorEnabled()).thenReturn(true);
        when(mavenLog.isWarnEnabled()).thenReturn(true);

        List<Class<?>> argumentTypes = new ArrayList<>();
        List<Object> invocationArgs = new ArrayList<>();

        if (loggerInvocation.marker != null) {
            argumentTypes.add(Marker.class);
            invocationArgs.add(loggerInvocation.marker);
        }

        argumentTypes.add(String.class);
        invocationArgs.add(loggerInvocation.message);

        if (loggerInvocation.args != null) {
            if (loggerInvocation.args.length == 1) {
                argumentTypes.add(Object.class);
                invocationArgs.add(loggerInvocation.args[0]);
            } else if (loggerInvocation.args.length == 2) {
                argumentTypes.add(Object.class);
                argumentTypes.add(Object.class);
                invocationArgs.add(loggerInvocation.args[0]);
                invocationArgs.add(loggerInvocation.args[1]);
            } else if (loggerInvocation.args.length > 2) {
                argumentTypes.add(Object[].class);
                invocationArgs.add(loggerInvocation.args);
            }
        }

        if (loggerInvocation.t != null) {
            argumentTypes.add(Throwable.class);
            invocationArgs.add(loggerInvocation.t);
        }

        try {
            Method declaredMethod = Logger.class.getDeclaredMethod(loggerInvocation.level, argumentTypes.toArray(new Class<?>[0]));
            declaredMethod.invoke(logger, invocationArgs.toArray(new Object[0]));

            List<Class<?>> targetArgTypes = new ArrayList<>();
            List<Object> targetArgValues = new ArrayList<>();

            targetArgTypes.add(CharSequence.class);
            targetArgValues.add(loggerInvocation.expectedMessage);
            if (loggerInvocation.t != null) {
                targetArgTypes.add(Throwable.class);
                targetArgValues.add(loggerInvocation.t);
            }

            Method mavenLoggerMethod = Log.class.getDeclaredMethod(loggerInvocation.level, targetArgTypes.toArray(new Class<?>[0]));

            mavenLoggerMethod.invoke(verify(mavenLog, timeout(1)), targetArgValues.toArray(new Object[0]));
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }

    }

    private static class LoggerInvocation {
        String expectedMessage;
        String level;
        Marker marker;
        String message;
        Object[] args;
        Throwable t;

        public LoggerInvocation(String expectedMessage, String level, Marker marker, String message, Object[] args, Throwable t) {
            this.expectedMessage = expectedMessage;
            this.level = level;
            this.marker = marker;
            this.message = message;
            this.args = args;
            this.t = t;
        }

        @Override
        public String toString() {
            return "LoggerInvocation{" +
                    "expectedMessage='" + expectedMessage + '\'' +
                    ", level='" + level + '\'' +
                    ", throwable='" + (t != null) + '\'' +
                    '}';
        }
    }


}
