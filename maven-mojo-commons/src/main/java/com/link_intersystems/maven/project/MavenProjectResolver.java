package com.link_intersystems.maven.project;

import org.apache.maven.RepositoryUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.project.*;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.repository.LocalRepositoryManager;

import java.io.File;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author René Link {@literal <rene.link@link-intersystems.com>}
 */
@Component(role = MavenProjectResolver.class)
public class MavenProjectResolver {

    @Requirement
    private ProjectDependenciesResolver dependencyResolver;

    public DependencyResolutionResult resolveDependencies(MavenProject project, RepositorySystemSession session) {
        DependencyResolutionResult resolutionResult;

        try {
            DefaultDependencyResolutionRequest resolution = new DefaultDependencyResolutionRequest(project, session);
            resolutionResult = dependencyResolver.resolve(resolution);
        } catch (DependencyResolutionException e) {
            resolutionResult = e.getResult();
        }

        Set<Artifact> artifacts = new LinkedHashSet<>();
        if (resolutionResult.getDependencyGraph() != null) {
            RepositoryUtils.toArtifacts(artifacts, resolutionResult.getDependencyGraph().getChildren(),
                    Collections.singletonList(project.getArtifact().getId()), null);

            // Maven 2.x quirk: an artifact always points at the local repo, regardless whether resolved or not
            LocalRepositoryManager lrm = session.getLocalRepositoryManager();
            for (Artifact artifact : artifacts) {
                if (!artifact.isResolved()) {
                    String path = lrm.getPathForLocalArtifact(RepositoryUtils.toArtifact(artifact));
                    artifact.setFile(new File(lrm.getRepository().getBasedir(), path));
                }
            }
        }
        project.setResolvedArtifacts(artifacts);
        project.setArtifacts(artifacts);

        return resolutionResult;
    }
}
