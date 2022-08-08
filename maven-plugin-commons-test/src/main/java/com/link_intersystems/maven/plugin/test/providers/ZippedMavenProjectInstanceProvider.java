package com.link_intersystems.maven.plugin.test.providers;

import com.link_intersystems.maven.plugin.test.MavenTestProjectInstance;
import com.link_intersystems.maven.plugin.test.MavenTestProjectInstanceProvider;

import java.net.URL;

/**
 * @author René Link {@literal <rene.link@link-intersystems.com>}
 */
public class ZippedMavenProjectInstanceProvider implements MavenTestProjectInstanceProvider {
    @Override
    public boolean canHandle(URL resource) {
        return resource.getPath().endsWith(".zip");
    }

    @Override
    public MavenTestProjectInstance create(URL resource) {
        return new ZippedMavenTestProject(resource);
    }
}
