package com.link_intersystems.maven.plugin.test;

import org.codehaus.plexus.util.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author René Link {@literal <rene.link@link-intersystems.com>}
 */
public class ClasspathMavenTestProject implements MavenTestProjectInstance {
    private File pomFile;
    private File projectBasedir;
    private final URL pomUrl;

    public ClasspathMavenTestProject(URL pomUrl) {
        this.pomUrl = pomUrl;
    }

    @Override
    public void init(File basedir) throws IOException {
        try {
            Path pomPath = Paths.get(pomUrl.toURI());
            pomFile = pomPath.toFile();
            projectBasedir = pomFile.getParentFile();
        } catch (URISyntaxException e) {
            throw new IOException(e);
        }

        FileUtils.copyDirectory(projectBasedir, basedir);
    }

    @Override
    public File getPomFile() {
        return pomFile;
    }
}
