package com.link_intersystems.maven.plugin.test.extensions;

import org.apache.maven.project.MavenProject;

/**
 * @author René Link {@literal <rene.link@link-intersystems.com>}
 */
class MavenProjectResolver extends MojoTestContextResolver {

    public MavenProjectResolver() {
        super(MavenProject.class);
    }

    @Override
    protected Object resolve(MojoTestContext mojoTestContext) {
        return mojoTestContext.getMavenProject();
    }

}
