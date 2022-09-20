package com.link_intersystems.maven.plugin.test.extensions;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;

/**
 * @author René Link {@literal <rene.link@link-intersystems.com>}
 */
abstract class AnnotatedMojoTestContextResolver<T, A extends Annotation> implements ParameterResolver {


    private Class<?> type;
    private Class<A> annotationType;

    public AnnotatedMojoTestContextResolver(Class<?> type, Class<A> annotationType) {
        this.type = type;
        this.annotationType = annotationType;
    }

    public AnnotatedMojoTestContextResolver(Class<?> type) {
        this.type = type;
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        Parameter parameter = parameterContext.getParameter();
        Class<?> type = parameter.getType();
        return this.type.isAssignableFrom(type) &&  parameter.isAnnotationPresent(annotationType);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        MojoTestContext mojoTestContext = MojoTestContextLifecycle.getMojoTestContext(extensionContext, parameterContext);

        Parameter parameter = parameterContext.getParameter();

        if (mojoTestContext == null) {
            throw new ParameterResolutionException("Can not resolve parameter " + parameter + ". No MojoTestContext available. Maybe there is no @MavenTestProject annotation on either the test method, class or superclass.");
        }

        A annotation = parameter.getAnnotation(annotationType);
        Class<?> type = parameter.getType();

        return resolve(mojoTestContext, annotation);
    }

    protected abstract Object resolve(MojoTestContext mojoTestContext, A annotation);
}
