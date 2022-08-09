package com.link_intersystems.maven.plugin.test;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

@Mojo(name = "goal")
public class SimpleMojo extends AbstractMojo {

    @Parameter(property = "project", readonly = true, required = true)
    MavenProject mavenProject;

    @Parameter(defaultValue = "${project.basedir}")
    String dir;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().debug("TEST DEBUG");
    }
}
