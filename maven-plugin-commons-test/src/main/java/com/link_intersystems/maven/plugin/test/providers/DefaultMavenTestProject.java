package com.link_intersystems.maven.plugin.test.providers;

import com.link_intersystems.maven.plugin.test.MavenTestProjectInstance;
import org.codehaus.plexus.util.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.util.Objects.requireNonNull;

/**
 * @author René Link {@literal <rene.link@link-intersystems.com>}
 */
class DefaultMavenTestProject implements MavenTestProjectInstance {
    private File pomFile;
    private File projectBasedir;
    private final URL pomUrl;
    private File basedir;

    public DefaultMavenTestProject(URL pomUrl) {
        this.pomUrl = requireNonNull(pomUrl);
    }

    @Override
    public void init(File basedir) throws IOException {
        this.basedir = basedir;
        try {
            Path pomPath = Paths.get(pomUrl.toURI());
            pomFile = pomPath.toFile();
            if (pomFile.isDirectory()) {
                pomFile = new File(pomFile, "pom.xml");
            }

            projectBasedir = pomFile.getParentFile();
        } catch (URISyntaxException e) {
            throw new IOException(e);
        }

        FileUtils.copyDirectoryStructure(projectBasedir, basedir);
    }

    @Override
    public File getPomFile() {
        return new File(basedir, "pom.xml");
    }
}
