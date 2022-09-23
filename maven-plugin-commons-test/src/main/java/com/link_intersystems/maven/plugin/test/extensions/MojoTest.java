package com.link_intersystems.maven.plugin.test.extensions;

import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author René Link {@literal <rene.link@link-intersystems.com>}
 */
@ExtendWith(MojoTestExtension.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface MojoTest {
}
