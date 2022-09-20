package com.link_intersystems.maven.plugin.test.extensions;

import com.link_intersystems.maven.plugin.test.TestMojo;
import org.apache.maven.plugin.Mojo;
import org.junit.jupiter.api.extension.ParameterResolutionException;

/**
 * @author René Link {@literal <rene.link@link-intersystems.com>}
 */
class MojoParameterResolver extends AnnotatedMojoTestContextResolver<Mojo, TestMojo> {

    public MojoParameterResolver() {
        super(Mojo.class, TestMojo.class);
    }

    @Override
    protected Object resolve(MojoTestContext mojoTestContext, TestMojo annotation) {
        try {
            return mojoTestContext.getMojo(annotation);
        } catch (Exception e) {
            throw new ParameterResolutionException("", e);
        }
    }
}
