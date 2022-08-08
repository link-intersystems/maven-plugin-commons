package com.link_intersystems.maven.plugin.test.extensions;

import com.link_intersystems.maven.plugin.test.TestMojo;
import org.apache.maven.plugin.Mojo;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

import java.lang.reflect.Executable;
import java.lang.reflect.Parameter;

/**
 * @author René Link {@literal <rene.link@link-intersystems.com>}
 */
class MojoResolver implements ParameterResolver {

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        Parameter parameter = parameterContext.getParameter();
        Class<?> type = parameter.getType();
        return parameterContext.isAnnotated(TestMojo.class) && Mojo.class.isAssignableFrom(type);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        Parameter parameter = parameterContext.getParameter();
        MojoTestContext mojoTestContext = MojoTestContextLifecycle.getMojoTestContext(extensionContext, parameterContext);
        if (mojoTestContext == null) {
            throw new ParameterResolutionException("Can not resolve parameter " + parameterContext.getParameter() + ". No MojoTestContext available. Maybe there is no @MavenTestProject annotation on either the test method, class or superclass.");
        }
        TestMojo testMojo = parameter.getAnnotation(TestMojo.class);
        try {
            return mojoTestContext.getMojo(testMojo);
        } catch (Exception e) {
            throw new ParameterResolutionException("", e);
        }
    }

    public Parameter findTestMojoParameter(ParameterContext parameterContext) {
        Executable declaringExecutable = parameterContext.getDeclaringExecutable();
        Parameter[] parameters = declaringExecutable.getParameters();
        for (Parameter parameter : parameters) {
            if (parameter.isAnnotationPresent(TestMojo.class)) {
                return parameter;
            }
        }
        return null;
    }
}
