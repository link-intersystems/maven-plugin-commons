package com.link_intersystems.maven.plugin.test;

import org.apache.maven.plugins.annotations.ResolutionScope;

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

    String goal();

    boolean debugEnabled() default false;

    ResolutionScope requiresDependencyResolution() default ResolutionScope.NONE;

    ResolutionScope requiresDependencyCollection() default ResolutionScope.NONE;
}
