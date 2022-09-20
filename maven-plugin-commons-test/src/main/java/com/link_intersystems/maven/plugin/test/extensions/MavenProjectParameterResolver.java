package com.link_intersystems.maven.plugin.test.extensions;

import org.apache.maven.project.MavenProject;

/**
 * @author René Link {@literal <rene.link@link-intersystems.com>}
 */
class MavenProjectParameterResolver extends MojoTestContextResolver {

    public MavenProjectParameterResolver() {
        super(MavenProject.class);
    }

    @Override
    protected Object resolve(MojoTestContext mojoTestContext) {

        return mojoTestContext.getMavenProject();
    }

}
