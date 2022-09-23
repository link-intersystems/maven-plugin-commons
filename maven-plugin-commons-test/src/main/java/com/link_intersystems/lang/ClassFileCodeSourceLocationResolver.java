package com.link_intersystems.lang;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;

/**
 * @author René Link {@literal <rene.link@link-intersystems.com>}
 */
public class ClassFileCodeSourceLocationResolver implements CodeSourceLocationResolver {
    @Override
    public Optional<URL> resolve(Class<?> clazz) {
        URL codeSourceLocation = null;

        String canonicalName = clazz.getCanonicalName();
        String classResourcePath = canonicalName.replaceAll("\\.", "/") + ".class";
        URL classResource = clazz.getResource("/" + classResourcePath);

        if (classResource != null) {
            String path = classResource.getPath();
            String basepath = path.substring(0, path.length() - classResourcePath.length());
            try {
                codeSourceLocation = new URL(classResource.getProtocol(), classResource.getHost(), classResource.getPort(), basepath);
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
        }

        return Optional.ofNullable(codeSourceLocation);
    }
}
