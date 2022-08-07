package com.link_intersystems.maven.plugin.test;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.layout.ArtifactRepositoryLayout;
import org.apache.maven.repository.LocalArtifactRepository;

import java.io.File;

/**
 * @author René Link {@literal <rene.link@link-intersystems.com>}
 */
class TestLocalArtifactRepository extends LocalArtifactRepository {

    private final File localRepositoryPath;

    public TestLocalArtifactRepository(File localRepositoryPath, ArtifactRepositoryLayout artifactRepositoryLayout) {
        setLayout(artifactRepositoryLayout);
        setId("local");
        this.localRepositoryPath = localRepositoryPath;
    }

    @Override
    public String getBasedir() {
        return localRepositoryPath.getAbsolutePath();
    }

    @Override
    public Artifact find(Artifact artifact) {
        File artifactFile = new File(localRepositoryPath, pathOf(artifact));

        // We need to set the file here or the resolver will fail with an NPE, not fully equipped to deal
        // with multiple local repository implementations yet.
        artifact.setFile(artifactFile);

        return artifact;
    }

    @Override
    public boolean hasLocalMetadata() {
        return true;
    }
}
