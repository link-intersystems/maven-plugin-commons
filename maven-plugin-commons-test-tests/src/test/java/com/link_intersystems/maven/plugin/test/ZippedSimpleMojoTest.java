package com.link_intersystems.maven.plugin.test;

import com.link_intersystems.maven.plugin.test.extensions.MojoTestExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * @author René Link {@literal <rene.link@link-intersystems.com>}
 */

@MavenTestProject("com/link_intersystems/maven/plugin/test/simpleProject.zip")
@ExtendWith(MojoTestExtension.class)
class ZippedSimpleMojoTest {

    @Test
    void resolveMojo(@TestMojo(goal = "goal") SimpleMojo mojo) {
        assertNotNull(mojo, "Mojo should be resolved.");
    }

}