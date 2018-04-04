package com.calincosma.mavenizer.domain.nexus;

import com.google.gson.annotations.SerializedName;

public class Artifact {
	
	private String id;
	
	@SerializedName("g")
	private String group;
	
	@SerializedName("a")
	private String artifact;
	
	@SerializedName("v")
	private String version;
	
	@SerializedName("p")
	private String packaging;
	
	@Override
	public String toString() {
		return "Artifact{" +
				"id='" + id + '\'' +
				", group='" + group + '\'' +
				", artifact='" + artifact + '\'' +
				", version='" + version + '\'' +
				", packaging='" + packaging + '\'' +
				'}';
	}
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getGroup() {
		return group;
	}
	
	public void setGroup(String group) {
		this.group = group;
	}
	
	public String getArtifact() {
		return artifact;
	}
	
	public void setArtifact(String artifact) {
		this.artifact = artifact;
	}
	
	public String getVersion() {
		return version;
	}
	
	public void setVersion(String version) {
		this.version = version;
	}
	
	public String getPackaging() {
		return packaging;
	}
	
	public void setPackaging(String packaging) {
		this.packaging = packaging;
	}
}
