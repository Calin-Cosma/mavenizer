package com.calincosma.mavenizer.exception;

public class ClassDependencyNotFoundException extends RuntimeException {
	
	public ClassDependencyNotFoundException(String message) {
		super(message);
	}
	
	public ClassDependencyNotFoundException() {
		super();
	}
	
	public ClassDependencyNotFoundException(String message, Throwable e) {
		super(message, e);
	}
}
