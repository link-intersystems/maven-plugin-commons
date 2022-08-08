package com.link_intersystems.maven.plugin.test;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author René Link {@literal <rene.link@link-intersystems.com>}
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface TestMojo {

    String gaol();

    boolean debugEnabled() default false;
}
