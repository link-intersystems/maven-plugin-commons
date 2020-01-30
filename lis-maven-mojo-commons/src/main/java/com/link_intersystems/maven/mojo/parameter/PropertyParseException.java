package com.link_intersystems.maven.mojo.parameter;

public class PropertyParseException extends RuntimeException {

	/**
	 *
	 */
	private static final long serialVersionUID = 937241436578701982L;
	private String property;
	private String message;

	public PropertyParseException(String property, String message) {
		this.property = property;
		this.message = message;
	}

	public PropertyParseException(String property, String message, Throwable cause) {
		super(cause);
		this.property = property;
		this.message = message;
	}

	@Override
	public String getMessage() {
		return "Exception occurred parsing property " + property + ": " + message;
	}
}
