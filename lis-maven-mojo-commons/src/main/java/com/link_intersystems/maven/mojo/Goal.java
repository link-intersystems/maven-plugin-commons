package com.link_intersystems.maven.mojo;

import org.apache.maven.plugin.Mojo;

/**
 * A {@link Goal} represents a specific task within a build process that can be
 * executed. A {@link Goal} should be independent of the way it's parameters are
 * configured. Thus each {@link Goal} only takes a specific PARAMS object. This
 * might be an interface that is adapted by a maven mojo, but since it is not
 * tied to a {@link Mojo} it can be easily invoked by other components in a
 * maven build, e.g. other {@link Mojo}s.
 * 
 * @author René Link [rene.link@link-intersystems.com]
 * 
 * @param <PARAMS>
 */
public interface Goal<PARAMS> {

	public void execute(MavenContext mavenContext, PARAMS executionParams) throws GoalExecutionException;
}
