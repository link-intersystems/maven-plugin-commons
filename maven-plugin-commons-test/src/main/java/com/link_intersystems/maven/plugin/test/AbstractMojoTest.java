package com.link_intersystems.maven.plugin.test;

import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.repository.layout.ArtifactRepositoryLayout;
import org.apache.maven.execution.*;
import org.apache.maven.plugin.Mojo;
import org.apache.maven.plugin.logging.SystemStreamLog;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.apache.maven.plugin.testing.stubs.MavenProjectStub;
import org.apache.maven.project.DefaultProjectBuildingRequest;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuilder;
import org.apache.maven.project.ProjectBuildingResult;
import org.apache.maven.repository.LocalArtifactRepository;
import org.apache.maven.repository.internal.MavenRepositorySystemUtils;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;
import org.junit.platform.commons.util.AnnotationUtils;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Optional;
import java.util.ServiceLoader;

/**
 * @author René Link {@literal <rene.link@link-intersystems.com>}
 */
public class AbstractMojoTest {

    private MojoTestCaseAdapter testCaseAdapter = new MojoTestCaseAdapter();
    private MavenTestProjectInstance mavenTestProject;
    private MavenSession mavenSession;
    private MavenProject mavenProject;
    private Path repositoryPath;
    private Path projectPath;

    @BeforeEach
    protected void setUp(@TempDir Path tmpDir) throws Exception {
        testCaseAdapter.setUp();

        projectPath = tmpDir.resolve("project");
        repositoryPath = tmpDir.resolve(".m2/repository");

        mavenTestProject = createTestMavenProject();
        mavenTestProject.init(projectPath.toFile());

        File pomFile = mavenTestProject.getPomFile();

        ProjectBuilder projectBuilder = testCaseAdapter.doLookup(ProjectBuilder.class);

        File localRepositoryPath = repositoryPath.toFile();
        ArtifactRepositoryLayout layout = testCaseAdapter.doLookup(ArtifactRepositoryLayout.class);
        LocalArtifactRepository localArtifactRepository = new TestLocalArtifactRepository(localRepositoryPath, layout);
        mavenSession = testCaseAdapter.newMavenSession(new MavenProjectStub(), localArtifactRepository);


        DefaultProjectBuildingRequest request = new DefaultProjectBuildingRequest();
        request.setRepositorySession(mavenSession.getRepositorySession());
        request.setLocalRepository(mavenSession.getLocalRepository());
        ProjectBuildingResult projectBuildingResult = projectBuilder.build(pomFile, request);

        mavenProject = projectBuildingResult.getProject();
        mavenSession.setCurrentProject(mavenProject);
    }

    @AfterEach
    protected void tearDown() throws Exception {
        testCaseAdapter.tearDown();
    }

    public Path getProjectPath() {
        return projectPath;
    }

    public MavenProject getMavenProject() {
        return mavenProject;
    }

    public MavenSession getMavenSession() {
        return mavenSession;
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

    protected <T extends Mojo> T lookupConfiguredMojo(Class<T> mojoClass) throws Exception {
        return lookupConfiguredMojo(mojoClass, false);
    }

    protected <T extends Mojo> T lookupConfiguredMojo(Class<T> mojoClass, boolean debugEnabled) throws Exception {
        Optional<org.apache.maven.plugins.annotations.Mojo> mojoAnnotation = AnnotationUtils.findAnnotation(mojoClass, org.apache.maven.plugins.annotations.Mojo.class);
        String goal = mojoAnnotation.map(org.apache.maven.plugins.annotations.Mojo::name).orElseThrow(() -> new IllegalStateException(mojoClass + " does not have a @Mojo annotaiton."));
        return lookupConfiguredMojo(goal, debugEnabled);
    }

    protected <T extends Mojo> T lookupConfiguredMojo(String goal) throws Exception {
        return lookupConfiguredMojo(goal, false);
    }

    @SuppressWarnings("unchecked")
    protected <T extends Mojo> T lookupConfiguredMojo(String goal, boolean debugEnabled) throws Exception {
        MavenProject mavenProject = getMavenProject();
        Mojo mojo = testCaseAdapter.lookupConfiguredMojo(mavenProject, goal);
        mojo.setLog(new SystemStreamLog() {
            @Override
            public boolean isDebugEnabled() {
                return debugEnabled;
            }
        });
        return (T) mojo;
    }

    private class MojoTestCaseAdapter extends AbstractMojoTestCase {
        @Override
        public void setUp() throws Exception {
            super.setUp();
        }

        public <T> T doLookup(Class<T> role) throws ComponentLookupException {
            return lookup(role);
        }

        public MavenSession newMavenSession(MavenProject project, ArtifactRepository localArtifactRepository) {
            MavenExecutionRequest request = new DefaultMavenExecutionRequest();
            request.setLocalRepository(localArtifactRepository);
            MavenExecutionResult result = new DefaultMavenExecutionResult();

            MavenSession session = new MavenSession(getContainer(), MavenRepositorySystemUtils.newSession(), request, result);
            session.setCurrentProject(project);
            session.setProjects(Arrays.asList(project));
            return session;
        }

        @Override
        public Mojo lookupConfiguredMojo(MavenProject project, String goal) throws Exception {
            return super.lookupConfiguredMojo(mavenSession, newMojoExecution(goal));
        }

        @Override
        public void tearDown() throws Exception {
            getContainer().dispose();
        }
    }
}
