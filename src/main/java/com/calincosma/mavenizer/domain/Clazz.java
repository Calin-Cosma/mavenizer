package com.calincosma.mavenizer.domain;

import java.util.HashSet;
import java.util.Set;

public class Clazz {
	
	private String packageName;
	private String className;
	private Jar source;
	private Set<Jar> candidates = new HashSet<>();
	
	
	public Clazz(String packageName, String className) {
		this.packageName = packageName;
		this.className = className;
	}
	
	public Clazz(String packageName, String className, Jar source) {
		this.packageName = packageName;
		this.className = className;
		this.source = source;
	}
	
	public void addCandidate(Jar jar) {
		candidates.add(jar);
	}
	
	@Override
	public String toString() {
		return "Clazz{" +
				"name='" + getFullName() + '\'' +
				'}';
	}
	
	public String getFullName() {
		return packageName + "." + className;
	}
	
	public String getPackageName() {
		return packageName;
	}
	
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	
	public String getClassName() {
		return className;
	}
	
	public void setClassName(String className) {
		this.className = className;
	}
	
	public Jar getSource() {
		return source;
	}
	
	public void setSource(Jar source) {
		this.source = source;
	}
}
