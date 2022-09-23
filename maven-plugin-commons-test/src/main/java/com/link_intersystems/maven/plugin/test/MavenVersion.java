package com.link_intersystems.maven.plugin.test;

import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author René Link {@literal <rene.link@link-intersystems.com>}
 */
public class MavenVersion {

    public static final MavenVersion INSTANCE = new MavenVersion();

    public ArtifactVersion getMavenCoreVersion() {
        DefaultArtifactVersion version = null;
        String path = "/META-INF/maven/org.apache.maven/maven-core/pom.properties";
        try (InputStream is = MavenVersion.class.getResourceAsStream(path)) {
            Properties properties = new Properties();
            if (is != null) {
                properties.load(is);
            }
            String property = properties.getProperty("version");
            if (property != null) {
                version = new DefaultArtifactVersion(property);
            }
        } catch (IOException e) {
            // odd, where did this come from
        }
        return version;
    }

    public void assertAtLeast(String expectedMavenVersion) {
        ArtifactVersion expectedMavenArtifactVersion = new DefaultArtifactVersion(expectedMavenVersion);
        ArtifactVersion mavenCoreVersion = getMavenCoreVersion();

        assertTrue(mavenCoreVersion == null || mavenCoreVersion.compareTo(expectedMavenArtifactVersion) >= 0, () -> "Maven " + expectedMavenVersion + " or better is required");
    }
}
