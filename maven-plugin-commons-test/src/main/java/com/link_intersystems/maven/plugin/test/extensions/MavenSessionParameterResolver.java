package com.link_intersystems.maven.plugin.test.extensions;

import org.apache.maven.execution.MavenSession;

/**
 * @author René Link {@literal <rene.link@link-intersystems.com>}
 */
class MavenSessionParameterResolver extends MojoTestContextResolver {

    public MavenSessionParameterResolver() {
        super(MavenSession.class);
    }

    @Override
    protected MavenSession resolve(MojoTestContext mojoTestContext) {
        return mojoTestContext.getMavenSession();
    }
}
