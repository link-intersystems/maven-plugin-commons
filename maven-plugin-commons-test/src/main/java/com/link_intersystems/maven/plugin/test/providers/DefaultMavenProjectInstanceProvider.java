package com.link_intersystems.maven.plugin.test.providers;

import com.link_intersystems.maven.plugin.test.MavenTestProjectInstance;
import com.link_intersystems.maven.plugin.test.MavenTestProjectInstanceProvider;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author René Link {@literal <rene.link@link-intersystems.com>}
 */
public class DefaultMavenProjectInstanceProvider implements MavenTestProjectInstanceProvider {
    @Override
    public boolean canHandle(URL resource) {
        try {
            Path path = Paths.get(resource.toURI());
            File file = path.toFile();
            return file.exists() && (file.isDirectory() || "pom.xml".equals(file.getName()));
        } catch (URISyntaxException e) {
            return false;
        }
    }

    @Override
    public MavenTestProjectInstance create(URL resource) {
        return new DefaultMavenTestProject(resource);
    }
}
