package com.link_intersystems.maven.plugin.test.project;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.lifecycle.internal.MojoDescriptorCreator;
import org.apache.maven.plugin.*;
import org.apache.maven.plugin.descriptor.MojoDescriptor;
import org.apache.maven.plugin.prefix.NoPluginFoundForPrefixException;
import org.apache.maven.plugin.version.PluginVersionResolutionException;
import org.apache.maven.project.MavenProject;

/**
 * @author René Link {@literal <rene.link@link-intersystems.com>}
 */
public class MavenProjectMetadata {

    public interface Deps {
        MojoDescriptorCreator getMojoDescriptorCreator();
    }

    private final MavenProject mavenProject;
    private final MavenSession mavenSession;
    private Deps deps;

    public MavenProjectMetadata(MavenSession mavenSession, Deps deps) {
        this(mavenSession.getCurrentProject(), mavenSession, deps);
    }

    public MavenProjectMetadata(MavenProject mavenProject, MavenSession mavenSession, Deps deps) {
        this.mavenProject = mavenProject;
        this.mavenSession = mavenSession;
        this.deps = deps;
    }

    public MojoDescriptor getMojoDescriptor(String task) throws NoPluginFoundForPrefixException, PluginVersionResolutionException, PluginResolutionException, PluginNotFoundException, InvalidPluginDescriptorException, PluginDescriptorParsingException, MojoNotFoundException {
        MojoDescriptorCreator mojoDescriptorCreator = deps.getMojoDescriptorCreator();
        return mojoDescriptorCreator.getMojoDescriptor(task, mavenSession, mavenProject);
    }


}
