package com.link_intersystems.maven.plugin.test.extensions;

import org.junit.jupiter.api.extension.*;

/**
 * @author René Link {@literal <rene.link@link-intersystems.com>}
 */
public class MojoTest implements ParameterResolver, BeforeAllCallback, BeforeEachCallback, AfterEachCallback, AfterAllCallback {

    private MojoResolver mojoResolver = new MojoResolver();
    private MavenProjectResolver mavenProjectResolver = new MavenProjectResolver();
    private MojoTestContextLifecycle mojoTestContextLifecycle = new MojoTestContextLifecycle();

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return mojoResolver.supportsParameter(parameterContext, extensionContext) ||
                mavenProjectResolver.supportsParameter(parameterContext, extensionContext);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        Object arg = null;
        if (mojoResolver.supportsParameter(parameterContext, extensionContext)) {
            arg = mojoResolver.resolveParameter(parameterContext, extensionContext);
        } else if (mavenProjectResolver.supportsParameter(parameterContext, extensionContext)) {
            arg = mavenProjectResolver.resolveParameter(parameterContext, extensionContext);
        }

        return arg;
    }

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        mojoTestContextLifecycle.beforeAll(context);
    }

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        mojoTestContextLifecycle.beforeEach(context);
    }

    @Override
    public void afterEach(ExtensionContext context) throws Exception {
        mojoTestContextLifecycle.afterEach(context);
    }

    @Override
    public void afterAll(ExtensionContext context) throws Exception {
        mojoTestContextLifecycle.afterAll(context);
    }
}
