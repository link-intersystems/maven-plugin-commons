package com.link_intersystems.maven.plugin.test;

import com.link_intersystems.maven.plugin.test.extensions.MojoTestExtension;
import org.apache.maven.project.MavenProject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * @author René Link {@literal <rene.link@link-intersystems.com>}
 */

@ExtendWith(MojoTestExtension.class)
class SimpleMojoSuperClassLevelTest extends AbstractSimpleMojoSuperClassLevelTest {

    @Test
    void resolveMojo(@TestMojo(goal = "goal") SimpleMojo mojo) {
        assertNotNull(mojo, "Mojo should be resolved.");
    }

    @Test
    void resolveMojoAndProject(@TestMojo(goal = "goal") SimpleMojo mojo, MavenProject mavenProject) {
        resolveMojo(mojo);
        assertSame(mavenProject, mojo.mavenProject, "Injected project should be the same as the MavenProject parameter.");
    }
}