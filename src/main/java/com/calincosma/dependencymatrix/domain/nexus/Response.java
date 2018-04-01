package com.calincosma.dependencymatrix.domain.nexus;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Response {
	
	public int numFound;
	
	@SerializedName("docs")
	private List<Artifact> artifacts;
	
	public int getNumFound() {
		return numFound;
	}
	
	public void setNumFound(int numFound) {
		this.numFound = numFound;
	}
	
	public List<Artifact> getArtifacts() {
		return artifacts;
	}
	
	public void setArtifacts(List<Artifact> artifacts) {
		this.artifacts = artifacts;
	}
}
