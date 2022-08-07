package com.link_intersystems.maven.plugin.test.extensions;

import org.apache.maven.project.MavenProject;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

import java.lang.reflect.Parameter;

/**
 * @author René Link {@literal <rene.link@link-intersystems.com>}
 */
class MavenProjectResolver implements ParameterResolver {

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        Parameter parameter = parameterContext.getParameter();
        Class<?> type = parameter.getType();
        return type.isAssignableFrom(MavenProject.class);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        MojoTestContext mojoTestContext = MojoTestContextLifecycle.getMojoTestContext(extensionContext, parameterContext);

        if (mojoTestContext == null) {
            throw new ParameterResolutionException("Can not resolve parameter " + parameterContext.getParameter() + ". No MojoTestContext available. Maybe there is no @MavenTestProject annotation on either the test method, class or superclass.");
        }

        return mojoTestContext.getMavenProject();
    }

}
