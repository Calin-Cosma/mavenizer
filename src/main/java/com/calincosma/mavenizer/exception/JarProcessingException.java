package com.calincosma.mavenizer.exception;

public class JarProcessingException extends RuntimeException {
	
	public JarProcessingException(String message) {
		super(message);
	}
	
	public JarProcessingException() {
		super();
	}
	
	public JarProcessingException(String message, Throwable e) {
		super(message, e);
	}
}
