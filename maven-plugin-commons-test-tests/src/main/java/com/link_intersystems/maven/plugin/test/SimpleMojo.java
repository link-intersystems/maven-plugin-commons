package com.link_intersystems.maven.plugin.test;

import com.link_intersystems.maven.plugin.PluginMetadata;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.descriptor.MojoDescriptor;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

import java.util.Map;

@Mojo(name = "goal", requiresDependencyResolution = ResolutionScope.COMPILE)
public class SimpleMojo extends AbstractMojo {

    @Parameter(property = "project", readonly = true, required = true)
    MavenProject mavenProject;

    @Parameter(defaultValue = "${project.basedir}")
    String dir;

    @Component
    private PluginMetadata pluginMetadata;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().debug("TEST DEBUG");
    }
}
