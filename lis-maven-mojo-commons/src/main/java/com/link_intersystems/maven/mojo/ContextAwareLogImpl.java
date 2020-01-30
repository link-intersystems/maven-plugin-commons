package com.link_intersystems.maven.mojo;

import org.apache.maven.plugin.logging.Log;

class ContextAwareLogImpl extends LogAdapter implements ContextAwareLog {

	private String context;

	ContextAwareLogImpl(Log log) {
		this(log, null);
	}

	ContextAwareLogImpl(Log log, String context) {
		super(log);
		this.context = context;
	}

	private String applyContext(CharSequence content) {
		if (context == null) {
			return content.toString();
		} else {
			String contextLog = "[" + context + "] ";
			return contextLog + content.toString();
		}
	}

	public void debug(CharSequence content) {
		getLog().debug(applyContext(content));
	}

	public void debug(CharSequence content, Throwable error) {
		getLog().debug(applyContext(content), error);
	}

	public void debug(Throwable error) {
		getLog().debug(applyContext(""), error);
	}

	public void info(CharSequence content) {
		getLog().info(applyContext(content));
	}

	public void info(CharSequence content, Throwable error) {
		getLog().info(applyContext(content), error);
	}

	public void info(Throwable error) {
		getLog().info(applyContext(""), error);
	}

	public void warn(CharSequence content) {
		getLog().warn(applyContext(content));
	}

	public void warn(CharSequence content, Throwable error) {
		getLog().warn(applyContext(content), error);
	}

	public void warn(Throwable error) {
		getLog().warn(applyContext(""), error);
	}

	public void error(CharSequence content) {
		getLog().error(applyContext(content));
	}

	public void error(CharSequence content, Throwable error) {
		getLog().error(applyContext(content), error);
	}

	public void error(Throwable error) {
		getLog().error(applyContext(""), error);
	}

	public ContextAwareLog createSubcontextLog(String context) {
		return new ContextAwareLogImpl(this, context);
	}

}
