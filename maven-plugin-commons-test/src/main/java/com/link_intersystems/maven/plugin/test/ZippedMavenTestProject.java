package com.link_intersystems.maven.plugin.test;

import java.io.File;
import java.io.IOException;

/**
 * @author René Link {@literal <rene.link@link-intersystems.com>}
 */
public class ZippedMavenTestProject implements MavenTestProjectInstance {


    @Override
    public void init(File basedir) throws IOException {

    }

    @Override
    public File getPomFile() {
        return null;
    }

    private ClassLoader getClassLoader() {
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        if (contextClassLoader != null) {
            return contextClassLoader;
        }
        return getClass().getClassLoader();
    }
}
