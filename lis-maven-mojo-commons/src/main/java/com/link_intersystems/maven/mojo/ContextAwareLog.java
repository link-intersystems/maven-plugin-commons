package com.link_intersystems.maven.mojo;

import org.apache.maven.plugin.logging.Log;

public interface ContextAwareLog extends Log {

	public ContextAwareLog createSubcontextLog(String context);
}
