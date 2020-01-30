package com.link_intersystems.maven.mojo;

import org.apache.maven.plugin.logging.Log;

public class LogAdapter implements Log {

	private Log log;

	public LogAdapter(Log log) {
		this.log = log;
	}

	protected Log getLog() {
		return log;
	}

	public void debug(CharSequence arg0, Throwable arg1) {
		log.debug(arg0, arg1);
	}

	public void debug(CharSequence arg0) {
		log.debug(arg0);
	}

	public void debug(Throwable arg0) {
		log.debug(arg0);
	}

	public void error(CharSequence arg0, Throwable arg1) {
		log.error(arg0, arg1);
	}

	public void error(CharSequence arg0) {
		log.error(arg0);
	}

	public void error(Throwable arg0) {
		log.error(arg0);
	}

	public void info(CharSequence arg0, Throwable arg1) {
		log.info(arg0, arg1);
	}

	public void info(CharSequence arg0) {
		log.info(arg0);
	}

	public void info(Throwable arg0) {
		log.info(arg0);
	}

	public boolean isDebugEnabled() {
		return log.isDebugEnabled();
	}

	public boolean isErrorEnabled() {
		return log.isErrorEnabled();
	}

	public boolean isInfoEnabled() {
		return log.isInfoEnabled();
	}

	public boolean isWarnEnabled() {
		return log.isWarnEnabled();
	}

	public void warn(CharSequence arg0, Throwable arg1) {
		log.warn(arg0, arg1);
	}

	public void warn(CharSequence arg0) {
		log.warn(arg0);
	}

	public void warn(Throwable arg0) {
		log.warn(arg0);
	}

}
