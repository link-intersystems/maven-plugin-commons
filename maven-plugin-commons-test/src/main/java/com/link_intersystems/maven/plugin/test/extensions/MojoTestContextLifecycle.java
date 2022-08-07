package com.link_intersystems.maven.plugin.test.extensions;

import com.link_intersystems.maven.plugin.test.MavenTestProject;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.util.AnnotationUtils;

import java.io.IOException;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Executable;
import java.util.Optional;

/**
 * @author René Link {@literal <rene.link@link-intersystems.com>}
 */
class MojoTestContextLifecycle implements BeforeAllCallback, BeforeEachCallback, AfterEachCallback, AfterAllCallback {

    static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(MojoTestContextLifecycle.class);


    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        before(context);
    }

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        before(context);
    }

    private void before(ExtensionContext context) {
        Optional<AnnotatedElement> element = context.getElement();
        MavenTestProject mavenTestProject = element.map(this::findMavenTestProject).orElse(null);
        if (mavenTestProject != null) {
            initMavenTestProject(context, mavenTestProject, element.get().toString());
        }
    }

    @Override
    public void afterEach(ExtensionContext context) throws Exception {
        after(context);
    }

    @Override
    public void afterAll(ExtensionContext context) throws Exception {
        after(context);
    }

    private void after(ExtensionContext context) throws IOException {
        Optional<AnnotatedElement> element = context.getElement();
        if (element.isPresent()) {
            ExtensionContext.Store store = context.getStore(NAMESPACE);
            MojoTestContext mojoTestContext = store.get(element.get().toString(), MojoTestContext.class);
            if (mojoTestContext != null) {
                mojoTestContext.tearDown();
            }
        }
    }

    MojoTestContext initMavenTestProject(ExtensionContext extensionContext, MavenTestProject mavenTestProject, String name) {
        ExtensionContext.Store store = extensionContext.getStore(NAMESPACE);

        MojoTestContext mojoTestContext = store.get(mavenTestProject.value(), MojoTestContext.class);
        if (mojoTestContext == null) {
            mojoTestContext = setupMojoTestContext(mavenTestProject);
            store.put(name, mojoTestContext);

        }
        return mojoTestContext;
    }

    private MojoTestContext setupMojoTestContext(MavenTestProject mavenTestProject) {
        MojoTestContext mojoTestContext;
        mojoTestContext = new MojoTestContext();

        try {
            mojoTestContext.setUp(mavenTestProject);

        } catch (Exception e) {
            throw new ParameterResolutionException("Unable to setup " + MavenTestProject.class.getName(), e);
        }
        return mojoTestContext;
    }


    private MavenTestProject findMavenTestProject(AnnotatedElement annotatedElement) {
        return AnnotationUtils.findAnnotation(annotatedElement, MavenTestProject.class).orElseGet(
                () -> {
                    if (annotatedElement instanceof Class<?>) {
                        Class<?> aClass = (Class<?>) annotatedElement;
                        Class<?> superclass = aClass.getSuperclass();
                        if (Object.class.equals(superclass)) {
                            return null;
                        }
                        return findMavenTestProject(superclass);
                    }
                    return null;
                });
    }

    public static MojoTestContext getMojoTestContext(ExtensionContext extensionContext, ParameterContext parameterContext) {
        ExtensionContext.Store store = extensionContext.getStore(MojoTestContextLifecycle.NAMESPACE);
        Executable declaringExecutable = parameterContext.getDeclaringExecutable();
        MojoTestContext mojoTestContext = store.get(declaringExecutable.toString(), MojoTestContext.class);
        if (mojoTestContext == null) {
            mojoTestContext = store.get(declaringExecutable.getDeclaringClass().toString(), MojoTestContext.class);
        }
        return mojoTestContext;
    }
}
