package com.link_intersystems.maven.mojo;

public class GoalParameterException extends GoalExecutionException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1895607730321675481L;

	public GoalParameterException() {
		super();
	}

	public GoalParameterException(String message, Throwable cause) {
		super(message, cause);
	}

	public GoalParameterException(String message) {
		super(message);
	}

	public GoalParameterException(Throwable cause) {
		super(cause);
	}

}
