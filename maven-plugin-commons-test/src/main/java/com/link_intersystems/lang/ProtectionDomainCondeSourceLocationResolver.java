package com.link_intersystems.lang;

import java.net.URL;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.Optional;

/**
 * @author René Link {@literal <rene.link@link-intersystems.com>}
 */
public class ProtectionDomainCondeSourceLocationResolver implements CodeSourceLocationResolver {
    @Override
    public Optional<URL> resolve(Class<?> clazz) {
        ProtectionDomain protectionDomain = clazz.getProtectionDomain();
        CodeSource codeSource = protectionDomain.getCodeSource();
        Optional<CodeSource> codeSourceOptional = Optional.ofNullable(codeSource);
        return codeSourceOptional.map(CodeSource::getLocation);
    }
}
