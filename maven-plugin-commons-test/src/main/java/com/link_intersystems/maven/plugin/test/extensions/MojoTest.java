package com.link_intersystems.maven.plugin.test.extensions;

import org.junit.jupiter.api.extension.*;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;

/**
 * @author René Link {@literal <rene.link@link-intersystems.com>}
 */
public class MojoTest implements ParameterResolver, BeforeAllCallback, BeforeEachCallback, AfterEachCallback, AfterAllCallback {

    private MojoResolver mojoResolver = new MojoResolver();
    private MavenProjectResolver projectResolver = new MavenProjectResolver();
    private MavenSessionResolver sessionResolver = new MavenSessionResolver();
    private MojoTestContextLifecycle testContextLifecycle = new MojoTestContextLifecycle();

    private List<Extension> extensions = asList(
            mojoResolver,
            projectResolver,
            sessionResolver,
            testContextLifecycle
    );

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        for (ParameterResolver parameterResolver : getExtensions(ParameterResolver.class)) {
            if (parameterResolver.supportsParameter(parameterContext, extensionContext)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {

        for (ParameterResolver parameterResolver : getExtensions(ParameterResolver.class)) {
            if (parameterResolver.supportsParameter(parameterContext, extensionContext)) {
                return parameterResolver.resolveParameter(parameterContext, extensionContext);
            }
        }

        throw new ParameterResolutionException("Unresolvable parameter " + parameterContext.getParameter());
    }

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        for (BeforeAllCallback callback : getExtensions(BeforeAllCallback.class)) {
            callback.beforeAll(context);
        }
    }

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        for (BeforeEachCallback callback : getExtensions(BeforeEachCallback.class)) {
            callback.beforeEach(context);
        }
    }

    @Override
    public void afterEach(ExtensionContext context) throws Exception {
        for (AfterEachCallback callback : getExtensions(AfterEachCallback.class)) {
            callback.afterEach(context);
        }
    }

    @Override
    public void afterAll(ExtensionContext context) throws Exception {
        for (AfterAllCallback callback : getExtensions(AfterAllCallback.class)) {
            callback.afterAll(context);
        }
    }

    private <T extends Extension> List<T> getExtensions(Class<T> extensionType) {
        return extensions.stream()
                .filter(extensionType::isInstance)
                .map(extensionType::cast)
                .collect(Collectors.toList());
    }
}
