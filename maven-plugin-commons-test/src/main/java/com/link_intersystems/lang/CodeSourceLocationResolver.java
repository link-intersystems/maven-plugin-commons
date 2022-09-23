package com.link_intersystems.lang;

import java.net.URL;
import java.util.Optional;

/**
 * @author René Link {@literal <rene.link@link-intersystems.com>}
 */
public interface CodeSourceLocationResolver {

    public Optional<URL> resolve(Class<?> clazz);
}
