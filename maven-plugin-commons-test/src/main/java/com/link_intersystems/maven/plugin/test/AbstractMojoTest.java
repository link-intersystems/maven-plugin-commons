package com.link_intersystems.maven.plugin.test;

import com.link_intersystems.maven.plugin.test.component.AbstractCoreMavenComponentTestCase;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.Mojo;
import org.apache.maven.plugin.logging.SystemStreamLog;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;
import org.junit.platform.commons.util.AnnotationUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.util.Optional;
import java.util.Properties;
import java.util.ServiceLoader;

/**
 * @author René Link {@literal <rene.link@link-intersystems.com>}
 */
public class AbstractMojoTest extends AbstractCoreMavenComponentTestCase {

    private MavenTestProjectInstance mavenTestProject;
    private MavenSession mavenSession;
    private MavenProject mavenProject;
    private Path projectPath;
    private ResolutionScope resolutionScope;
    private MojoUtil mojoUtil;

    @BeforeEach
    protected void setUp(@TempDir Path tmpDir) throws Exception {

        projectPath = tmpDir.resolve("project");

        mavenTestProject = createTestMavenProject();
        mavenTestProject.init(projectPath.toFile());
    }

    protected MojoUtil getMojoUtil() {
        if (mojoUtil == null) {
            mojoUtil = new MojoUtil(getContainer());
        }
        return mojoUtil;
    }

    public Path getProjectPath() {
        return projectPath;
    }

    public MavenProject getMavenProject() {
        if (mavenProject == null) {
            mavenProject = getMavenSession().getCurrentProject();
        }
        return mavenProject;
    }

    public MavenSession getMavenSession() {
        if (mavenSession == null) {
            try {
                Properties executionProperties = System.getProperties();
                ResolutionScope requiredResolution = getResolutionScope();
                boolean resolveDependencies = !ResolutionScope.NONE.equals(requiredResolution);
                mavenSession = createMavenSession(mavenTestProject.getPomFile(), executionProperties, false, resolveDependencies);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return mavenSession;
    }

    public void setResolutionScope(ResolutionScope resolutionScope) {
        this.resolutionScope = resolutionScope;
    }

    public ResolutionScope getResolutionScope() {
        return resolutionScope;
    }

    protected MavenTestProjectInstance createTestMavenProject() {
        Optional<MavenTestProject> mavenTestProjectOptional = getMavenTestProjectAnnotation();
        return mavenTestProjectOptional.map(this::createMavenTestProjectInstance).orElseThrow(() -> new IllegalStateException("Missing MavenTestProject annotation."));
    }

    protected Optional<MavenTestProject> getMavenTestProjectAnnotation() {
        return AnnotationUtils.findAnnotation(getClass(), MavenTestProject.class);
    }

    protected MavenTestProjectInstance createMavenTestProjectInstance(MavenTestProject annotation) {
        String resource = annotation.value();

        URL resourceUrl;

        if (resource.startsWith("file://")) {
            try {
                resourceUrl = new URL(resource);
            } catch (MalformedURLException e) {
                throw new IllegalStateException("Unable to create maven test project for resource " + resource, e);
            }
        } else {
            if (resource.startsWith("/")) {
                resource = resource.substring(1);
            }
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            resourceUrl = classLoader.getResource(resource);
        }


        MavenTestProjectInstanceProvider provider = findProvider(resourceUrl);
        return provider.create(resourceUrl);
    }

    private MavenTestProjectInstanceProvider findProvider(URL mavenTestProjectResource) {
        ServiceLoader<MavenTestProjectInstanceProvider> providers = ServiceLoader.load(MavenTestProjectInstanceProvider.class);

        for (MavenTestProjectInstanceProvider provider : providers) {
            if (provider.canHandle(mavenTestProjectResource)) {
                return provider;
            }
        }
        throw new IllegalStateException("No " + MavenTestProjectInstanceProvider.class.getName() + " found by ServiceLoader." +
                " Make sure that a valid provider is registerd via META-INF/services/" + MavenTestProjectInstanceProvider.class.getName());
    }

    protected <T extends Mojo> T lookupConfiguredMojo(Class<T> mojoClass, boolean debugEnabled) throws Exception {
        Optional<org.apache.maven.plugins.annotations.Mojo> mojoAnnotation = AnnotationUtils.findAnnotation(mojoClass, org.apache.maven.plugins.annotations.Mojo.class);
        String goal = mojoAnnotation.map(org.apache.maven.plugins.annotations.Mojo::name).orElseThrow(() -> new IllegalStateException(mojoClass + " does not have a @Mojo annotaiton."));
        return lookupConfiguredMojo(goal, debugEnabled);
    }

    @SuppressWarnings("unchecked")
    protected <T extends Mojo> T lookupConfiguredMojo(String goal, boolean debugEnabled) throws Exception {
        MavenProject mavenProject = getMavenProject();
        Mojo mojo = mojoUtil.lookupConfiguredMojo(mavenProject, goal);
        mojo.setLog(new SystemStreamLog() {
            @Override
            public boolean isDebugEnabled() {
                return debugEnabled;
            }
        });
        return (T) mojo;
    }

}
