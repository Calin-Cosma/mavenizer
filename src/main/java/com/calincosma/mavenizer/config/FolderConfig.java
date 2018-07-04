package com.calincosma.mavenizer.config;

public class FolderConfig {
	
	private String path;
	private String groupPrefix;
	private String group;
	private String groupSuffix;
	private String artifactPrefix;
	private String artifactPattern;
	private String artifactSuffix;
	private String version;
	
	public String getPath() {
		return path;
	}
	
	public void setPath(String path) {
		this.path = path;
	}
	
	public String getGroupPrefix() {
		return groupPrefix;
	}
	
	public void setGroupPrefix(String groupPrefix) {
		this.groupPrefix = groupPrefix;
	}
	
	public String getGroup() {
		return group;
	}
	
	public void setGroup(String group) {
		this.group = group;
	}
	
	public String getGroupSuffix() {
		return groupSuffix;
	}
	
	public void setGroupSuffix(String groupSuffix) {
		this.groupSuffix = groupSuffix;
	}
	
	public String getArtifactPrefix() {
		return artifactPrefix;
	}
	
	public void setArtifactPrefix(String artifactPrefix) {
		this.artifactPrefix = artifactPrefix;
	}
	
	public String getArtifactPattern() {
		return artifactPattern;
	}
	
	public void setArtifactPattern(String artifactPattern) {
		this.artifactPattern = artifactPattern;
	}
	
	public String getArtifactSuffix() {
		return artifactSuffix;
	}
	
	public void setArtifactSuffix(String artifactSuffix) {
		this.artifactSuffix = artifactSuffix;
	}
	
	public String getVersion() {
		return version;
	}
	
	public void setVersion(String version) {
		this.version = version;
	}
}
