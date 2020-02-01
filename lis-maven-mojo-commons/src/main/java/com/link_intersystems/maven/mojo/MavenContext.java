package com.link_intersystems.maven.mojo;

import java.util.Collection;
import java.util.List;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.apache.maven.settings.Server;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluator;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.graph.DependencyNode;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.ArtifactResolutionException;
import org.eclipse.aether.resolution.ArtifactResult;

public interface MavenContext {

	public List<RemoteRepository> getRemoteRepositories();

	public RepositorySystem getRepositorySystem();

	public RepositorySystemSession getRepositorySystemSession();

	public Log getLog();

	public ContextAwareLog getLog(String context);

	public MojoExecution getMojoExecution();

	public MavenSession getMavenSession();

	public String getTargetFolder();

	public ExpressionEvaluator getExpressionEvaluator();

	public MavenDependency getMavenDependency(Dependency dependency);

	public Server getServerSettings(String serverId);

	public MavenProject getMavenProject();

	List<ArtifactResult> resolveArtifacts(Collection<DependencyNode> nodes) throws ArtifactResolutionException;
}
