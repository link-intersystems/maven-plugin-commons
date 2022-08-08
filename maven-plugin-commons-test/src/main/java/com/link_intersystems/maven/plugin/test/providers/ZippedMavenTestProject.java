package com.link_intersystems.maven.plugin.test.providers;

import com.link_intersystems.io.Unzip;
import com.link_intersystems.maven.plugin.test.MavenTestProjectInstance;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import static java.util.Objects.requireNonNull;

/**
 * @author René Link {@literal <rene.link@link-intersystems.com>}
 */
class ZippedMavenTestProject implements MavenTestProjectInstance {

    private URL resource;
    private File basedir;

    public ZippedMavenTestProject(URL resource) {
        this.resource = requireNonNull(resource);
    }

    @Override
    public void init(File basedir) throws IOException {
        this.basedir = basedir;
        InputStream source = resource.openStream();
        if (source == null) {
            throw new IllegalArgumentException(resource + " does not exist.");
        }
        Unzip.unzip(source, basedir.toPath());
    }

    @Override
    public File getPomFile() {
        return new File(basedir, "pom.xml");
    }

}
