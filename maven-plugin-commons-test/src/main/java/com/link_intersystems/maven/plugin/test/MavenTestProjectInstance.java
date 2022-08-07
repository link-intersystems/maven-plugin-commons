package com.link_intersystems.maven.plugin.test;

import java.io.File;
import java.io.IOException;

/**
 * @author René Link {@literal <rene.link@link-intersystems.com>}
 */
public interface MavenTestProjectInstance {
    void init(File basedir) throws IOException;

    File getPomFile();
}
