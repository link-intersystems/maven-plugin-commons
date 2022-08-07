package com.link_intersystems.maven.plugin.test;

import com.link_intersystems.maven.plugin.test.extensions.MojoTest;
import org.apache.maven.project.MavenProject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author René Link {@literal <rene.link@link-intersystems.com>}
 */

@ExtendWith(MojoTest.class)
class SimpleMojoTest {

    @MavenTestProject("com/link_intersystems/maven/plugin/test/simpleProject")
    @Test
    void resolveMojo(@TestMojo(gaol = "goal") SimpleMojo mojo) {
        assertNotNull(mojo, "Mojo should be resolved.");
    }

    @MavenTestProject("com/link_intersystems/maven/plugin/test/simpleProject")
    @Test
    void resolveMojoAndProject(@TestMojo(gaol = "goal") SimpleMojo mojo, MavenProject mavenProject) {
        resolveMojo(mojo);
        assertSame(mavenProject, mojo.mavenProject, "Injected project should be the same as the MavenProject parameter.");
    }
}