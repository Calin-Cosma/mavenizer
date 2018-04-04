package com.calincosma.mavenizer.exception;

public class ConfigNotFoundException extends RuntimeException {
	
	public ConfigNotFoundException(String message) {
		super(message);
	}
	
	public ConfigNotFoundException() {
		super();
	}
	
	public ConfigNotFoundException(String message, Throwable e) {
		super(message, e);
	}
}
