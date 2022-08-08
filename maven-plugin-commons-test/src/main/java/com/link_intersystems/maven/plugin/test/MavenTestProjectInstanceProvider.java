package com.link_intersystems.maven.plugin.test;

import java.net.URISyntaxException;
import java.net.URL;

/**
 * @author René Link {@literal <rene.link@link-intersystems.com>}
 */
public interface MavenTestProjectInstanceProvider {

    public boolean canHandle(URL resource) ;

    public MavenTestProjectInstance create(URL resource);
}
