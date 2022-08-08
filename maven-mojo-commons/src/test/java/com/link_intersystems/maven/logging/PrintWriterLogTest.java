package com.link_intersystems.maven.logging;

import com.link_intersystems.maven.OutputAssertion;
import org.apache.maven.plugin.logging.Log;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author René Link {@literal <rene.link@link-intersystems.com>}
 */
class PrintWriterLogTest {

    private PrintWriterLog log;
    private StringWriter sw;
    private OutputAssertion outputAssertion;

    @BeforeEach
    private void setUp() {
        sw = new StringWriter();
        log = new PrintWriterLog(sw);

        outputAssertion = new OutputAssertion(sw.getBuffer());
    }

    @ParameterizedTest
    @ValueSource(strings = {"info", "debug", "error", "warn"})
    public void log(String level) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method logMethod = Log.class.getDeclaredMethod(level, CharSequence.class);
        String enableMethodName = "set" + level.substring(0, 1).toUpperCase() + level.substring(1) + "Enabled";
        Method enableMethod = AbstractLog.class.getDeclaredMethod(enableMethodName, boolean.class);

        enableMethod.invoke(log, false);
        logMethod.invoke(log, "Test");
        outputAssertion.assertNoLine();
        outputAssertion.reset();


        enableMethod.invoke(log, true);
        logMethod.invoke(log, "Test");

        outputAssertion.assertLine("[" + level + "] Test");
    }

    @ParameterizedTest
    @ValueSource(strings = {"info", "debug", "error", "warn"})
    public void logException(String level) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method logMethod = Log.class.getDeclaredMethod(level, Throwable.class);
        String enableMethodName = "set" + level.substring(0, 1).toUpperCase() + level.substring(1) + "Enabled";
        Method enableMethod = AbstractLog.class.getDeclaredMethod(enableMethodName, boolean.class);

        RuntimeException e = new RuntimeException();
        enableMethod.invoke(log, false);
        logMethod.invoke(log, e);
        outputAssertion.assertNoLine();
        outputAssertion.reset();


        enableMethod.invoke(log, true);
        logMethod.invoke(log, e);

        outputAssertion.assertLine("[" + level + "] java.lang.RuntimeException");
    }

    @ParameterizedTest
    @ValueSource(strings = {"info", "debug", "error", "warn"})
    public void logMessageWithException(String level) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method logMethod = Log.class.getDeclaredMethod(level, CharSequence.class, Throwable.class);
        String enableMethodName = "set" + level.substring(0, 1).toUpperCase() + level.substring(1) + "Enabled";
        Method enableMethod = AbstractLog.class.getDeclaredMethod(enableMethodName, boolean.class);

        RuntimeException e = new RuntimeException();
        enableMethod.invoke(log, false);
        logMethod.invoke(log, "Test", e);
        outputAssertion.assertNoLine();
        outputAssertion.reset();


        enableMethod.invoke(log, true);
        logMethod.invoke(log, "Test", e);

        outputAssertion.assertLine("[" + level + "] Test");
        outputAssertion.assertEmptyLine();
        outputAssertion.assertLine("java.lang.RuntimeException");
    }


}