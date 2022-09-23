package com.link_intersystems.maven.plugin.test.component;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import org.apache.maven.artifact.InvalidRepositoryException;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.execution.DefaultMavenExecutionRequest;
import org.apache.maven.execution.DefaultMavenExecutionResult;
import org.apache.maven.execution.MavenExecutionRequest;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.internal.aether.DefaultRepositorySystemSessionFactory;
import org.apache.maven.model.Model;
import org.apache.maven.model.Repository;
import org.apache.maven.model.RepositoryPolicy;
import org.apache.maven.project.DefaultProjectBuildingRequest;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuildingRequest;
import org.apache.maven.repository.RepositorySystem;
import org.apache.maven.repository.internal.MavenRepositorySystemUtils;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.testing.PlexusTest;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.internal.impl.SimpleLocalRepositoryManagerFactory;
import org.eclipse.aether.repository.LocalRepository;

import javax.inject.Inject;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import static org.codehaus.plexus.testing.PlexusExtension.getBasedir;

@PlexusTest
public abstract class AbstractCoreMavenComponentTestCase {

    @Inject
    protected PlexusContainer container;

    @Inject
    protected RepositorySystem repositorySystem;

    @Inject
    protected org.apache.maven.project.ProjectBuilder projectBuilder;

    protected PlexusContainer getContainer() {
        return container;
    }

    protected MavenExecutionRequest createMavenExecutionRequest(File pom)
            throws Exception {
        Properties systemProperties = System.getProperties();
        MavenExecutionRequest request = new DefaultMavenExecutionRequest()
                .setPom(pom)
                .setProjectPresent(true)
                .setShowErrors(true)
                .setPluginGroups(Arrays.asList("org.apache.maven.plugins"))
                .setLocalRepository(getLocalRepository())
                .setRemoteRepositories(getRemoteRepositories())
                .setPluginArtifactRepositories(getPluginArtifactRepositories())
                .setSystemProperties(systemProperties)
                .setGoals(Arrays.asList("package"));

        if (pom != null) {
            request.setMultiModuleProjectDirectory(pom.getParentFile());
        }

        return request;
    }

    protected MavenSession createMavenSession(File pom, Properties executionProperties, boolean includeModules, boolean resolveDependencies)
            throws Exception {
        MavenExecutionRequest request = createMavenExecutionRequest(pom);

        DefaultRepositorySystemSessionFactory repositorySystemSessionFactory = container.lookup(DefaultRepositorySystemSessionFactory.class);
        DefaultRepositorySystemSession repositorySystemSession = repositorySystemSessionFactory.newRepositorySession(request);

        ProjectBuildingRequest configuration = new DefaultProjectBuildingRequest()
                .setLocalRepository(request.getLocalRepository())
                .setRemoteRepositories(request.getRemoteRepositories())
                .setPluginArtifactRepositories(request.getPluginArtifactRepositories())
                .setSystemProperties(executionProperties)
                .setResolveDependencies(resolveDependencies)
                .setRepositorySession(repositorySystemSession)
                .setUserProperties(new Properties());

        List<MavenProject> projects = new ArrayList<>();

        if (pom != null) {
            MavenProject project = projectBuilder.build(pom, configuration).getProject();

            projects.add(project);
            if (includeModules) {
                for (String module : project.getModules()) {
                    File modulePom = new File(pom.getParentFile(), module);
                    if (modulePom.isDirectory()) {
                        modulePom = new File(modulePom, "pom.xml");
                    }
                    projects.add(projectBuilder.build(modulePom, configuration).getProject());
                }
            }
        } else {
            MavenProject project = createStubMavenProject();
            project.setRemoteArtifactRepositories(request.getRemoteRepositories());
            project.setPluginArtifactRepositories(request.getPluginArtifactRepositories());
            projects.add(project);
        }

        initRepoSession(configuration);

        MavenSession session =
                new MavenSession(getContainer(), configuration.getRepositorySession(), request,
                        new DefaultMavenExecutionResult());
        session.setProjects(projects);
        session.setAllProjects(session.getProjects());

        return session;
    }

    protected void initRepoSession(ProjectBuildingRequest request)
            throws Exception {
        File localRepoDir = new File(request.getLocalRepository().getBasedir());
        LocalRepository localRepo = new LocalRepository(localRepoDir);
        DefaultRepositorySystemSession session = MavenRepositorySystemUtils.newSession();
        session.setLocalRepositoryManager(new SimpleLocalRepositoryManagerFactory().newInstance(session, localRepo));
        request.setRepositorySession(session);
    }

    protected MavenProject createStubMavenProject() {
        Model model = new Model();
        model.setGroupId("org.apache.maven.test");
        model.setArtifactId("maven-test");
        model.setVersion("1.0");
        return new MavenProject(model);
    }

    protected List<ArtifactRepository> getRemoteRepositories()
            throws InvalidRepositoryException {
        File repoDir = new File(getBasedir(), "src/test/remote-repo").getAbsoluteFile();

        RepositoryPolicy policy = new RepositoryPolicy();
        policy.setEnabled(true);
        policy.setChecksumPolicy("ignore");
        policy.setUpdatePolicy("always");

        Repository repository = new Repository();
        repository.setId(RepositorySystem.DEFAULT_REMOTE_REPO_ID);
        repository.setUrl("file://" + repoDir.toURI().getPath());
        repository.setReleases(policy);
        repository.setSnapshots(policy);

        return Arrays.asList(repositorySystem.buildArtifactRepository(repository));
    }

    protected List<ArtifactRepository> getPluginArtifactRepositories()
            throws InvalidRepositoryException {
        return getRemoteRepositories();
    }

    protected ArtifactRepository getLocalRepository()
            throws InvalidRepositoryException {
        File repoDir = new File(getBasedir(), "target/local-repo").getAbsoluteFile();

        return repositorySystem.createLocalRepository(repoDir);
    }

}
