package com.link_intersystems.maven.plugin.test.extensions;

import com.link_intersystems.maven.plugin.test.project.MavenProjectMetadata;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.project.MavenProject;

/**
 * @author René Link {@literal <rene.link@link-intersystems.com>}
 */
class MavenProjectMetadataResolver extends MojoTestContextResolver {

    public MavenProjectMetadataResolver() {
        super(MavenProjectMetadata.class);
    }

    @Override
    protected Object resolve(MojoTestContext mojoTestContext) {

        MavenProject mavenProject = mojoTestContext.getMavenProject();
        MavenSession mavenSession = mojoTestContext.getMavenSession();
        return new MavenProjectMetadata(mavenProject, mavenSession, mojoTestContext.createLookupProxy(MavenProjectMetadata.Deps.class));
    }

}
