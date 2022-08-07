package com.link_intersystems.maven.plugin.test;

import com.link_intersystems.io.Unzip;

import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * @author René Link {@literal <rene.link@link-intersystems.com>}
 */
public class ZippedMavenTestProject implements MavenTestProjectInstance {


    private URL resource;
    private File basedir;

    public ZippedMavenTestProject(URL resource) {
        this.resource = resource;
    }

    @Override
    public void init(File basedir) throws IOException {
        this.basedir = basedir;
        Unzip.unzip(resource.openStream(), basedir.toPath());
    }

    @Override
    public File getPomFile() {
        return new File(basedir, "pom.xml");
    }

    private ClassLoader getClassLoader() {
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        if (contextClassLoader != null) {
            return contextClassLoader;
        }
        return getClass().getClassLoader();
    }
}
