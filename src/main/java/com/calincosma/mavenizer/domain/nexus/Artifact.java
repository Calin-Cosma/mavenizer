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
	
	public Artifact() {
	}
	
	public Artifact(String group, String artifact, String version) {
		this.group = group;
		this.artifact = artifact;
		this.version = version;
	}
	
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
	
	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		
		Artifact artifact1 = (Artifact)o;
		
		if (id != null ? !id.equals(artifact1.id) : artifact1.id != null)
			return false;
		if (!group.equals(artifact1.group))
			return false;
		if (!artifact.equals(artifact1.artifact))
			return false;
		if (!version.equals(artifact1.version))
			return false;
		return packaging != null ? packaging.equals(artifact1.packaging) : artifact1.packaging == null;
	}
	
	@Override
	public int hashCode() {
		int result = id != null ? id.hashCode() : 0;
		result = 31 * result + group.hashCode();
		result = 31 * result + artifact.hashCode();
		result = 31 * result + version.hashCode();
		result = 31 * result + (packaging != null ? packaging.hashCode() : 0);
		return result;
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
