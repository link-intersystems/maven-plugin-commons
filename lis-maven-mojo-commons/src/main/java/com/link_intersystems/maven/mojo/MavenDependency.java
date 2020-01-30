package com.link_intersystems.maven.mojo;

import static org.apache.commons.lang3.Validate.notNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.collection.CollectRequest;
import org.eclipse.aether.collection.DependencyCollectionException;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.graph.DependencyFilter;
import org.eclipse.aether.metadata.DefaultMetadata;
import org.eclipse.aether.metadata.Metadata;
import org.eclipse.aether.metadata.Metadata.Nature;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.ArtifactResult;
import org.eclipse.aether.resolution.DependencyRequest;
import org.eclipse.aether.resolution.DependencyResolutionException;
import org.eclipse.aether.resolution.DependencyResult;
import org.eclipse.aether.resolution.MetadataRequest;
import org.eclipse.aether.resolution.MetadataResult;
import org.eclipse.aether.resolution.VersionRangeRequest;
import org.eclipse.aether.resolution.VersionRangeResolutionException;
import org.eclipse.aether.resolution.VersionRangeResult;
import org.eclipse.aether.version.Version;

/**
 * Enriches a "normal" {@link Dependency} with more sophisticated methods than
 * just data access. This class puts common dependency related logic on top of a
 * {@link Dependency}.
 * 
 * 
 */
public class MavenDependency {

	private RepositorySystem repoSystem;
	private RepositorySystemSession repoSession;
	private List<RemoteRepository> remoteRepos;
	private Dependency dependency;

	MavenDependency(Dependency dependency, RepositorySystem repoSystem, RepositorySystemSession repoSession,
			List<RemoteRepository> remoteRepos) {
		notNull(dependency, "dependency must not be null");
		notNull(repoSession, "repoSession must not be null");
		notNull(remoteRepos, "remoteRepos must not be null");
		this.dependency = dependency;
		this.repoSystem = repoSystem;
		this.repoSession = repoSession;
		this.remoteRepos = remoteRepos;
	}

	/**
	 * 
	 * @return a copy of the {@link Dependency} of this {@link MavenDependency}
	 *         so that modifications on the returned {@link Dependency} does not
	 *         affect this {@link MavenDependency}.
	 */
	public Dependency getDependency() {
		Artifact artifact = dependency.getArtifact();
		String groupId = artifact.getGroupId();
		String artifactId = artifact.getArtifactId();
		String classifier = artifact.getClassifier();
		String extension = artifact.getExtension();
		String version = artifact.getVersion();
		DefaultArtifact artifactCopy = new DefaultArtifact(groupId, artifactId, classifier, extension, version);
		Dependency dependencyCopy = new Dependency(artifactCopy, dependency.getScope());
		return dependencyCopy;
	}

	public List<Artifact> getArtifacts(DependencyFilter transitiveFilter)
			throws DependencyCollectionException, DependencyResolutionException {
		CollectRequest request = new CollectRequest(dependency, remoteRepos);
		DependencyRequest dependencyRequest = new DependencyRequest(request, transitiveFilter);
		DependencyResult dependencyResult = repoSystem.resolveDependencies(repoSession, dependencyRequest);
		List<ArtifactResult> artifactResults = dependencyResult.getArtifactResults();

		List<Artifact> artifacts = new ArrayList<Artifact>();
		for (ArtifactResult artifactResult : artifactResults) {
			Artifact artifact = artifactResult.getArtifact();
			artifacts.add(artifact);
		}
		return artifacts;
	}

	public Metadata getMetadata() {
		Artifact artifact = dependency.getArtifact();
		String groupId = artifact.getGroupId();
		String artifactId = artifact.getArtifactId();
		String type = artifact.getExtension();
		String version = artifact.getVersion();
		Nature nature = version.contains(Nature.SNAPSHOT.name()) ? Nature.SNAPSHOT : Nature.RELEASE;
		Metadata metadata = new DefaultMetadata(groupId, artifactId, version, type, nature);
		MetadataRequest metadataRequest = new MetadataRequest(metadata);
		Collection<MetadataRequest> requests = new ArrayList<MetadataRequest>();
		requests.add(metadataRequest);
		List<MetadataResult> resolveMetadata = repoSystem.resolveMetadata(repoSession, requests);
		if (!resolveMetadata.isEmpty()) {
			MetadataResult metadataResult = resolveMetadata.get(0);
			return metadataResult.getMetadata();
		} else {
			return null;
		}
	}

	public List<Version> getVersions() throws VersionRangeResolutionException {
		Artifact artifact = dependency.getArtifact();
		VersionRangeRequest request = new VersionRangeRequest(artifact, remoteRepos, null);
		VersionRangeResult resolveVersionRange = repoSystem.resolveVersionRange(repoSession, request);
		List<Version> versions = resolveVersionRange.getVersions();
		return versions;

	}

	public String getClasspath(DependencyFilter transitiveFilter)
			throws DependencyCollectionException, DependencyResolutionException {
		List<Artifact> artifacts = getArtifacts(transitiveFilter);
		Iterator<Artifact> artifactIterator = artifacts.iterator();

		StringBuilder classpathBuilder = new StringBuilder();
		while (artifactIterator.hasNext()) {
			Artifact artifact = artifactIterator.next();
			File file = artifact.getFile();
			classpathBuilder.append(file.toString());
			if (artifactIterator.hasNext()) {
				classpathBuilder.append(File.pathSeparatorChar);
			}
		}

		return classpathBuilder.toString();
	}
}
