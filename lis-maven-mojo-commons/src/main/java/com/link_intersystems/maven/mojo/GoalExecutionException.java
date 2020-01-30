package com.link_intersystems.maven.mojo;

public class GoalExecutionException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7739985752801796610L;

	public GoalExecutionException() {
		super();
	}

	public GoalExecutionException(String message, Throwable cause) {
		super(message, cause);
	}

	public GoalExecutionException(String message) {
		super(message);
	}

	public GoalExecutionException(Throwable cause) {
		super(cause);
	}

}
