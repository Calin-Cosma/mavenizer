package com.calincosma.mavenizer.domain;

import com.calincosma.mavenizer.domain.nexus.Artifact;

import java.util.HashSet;
import java.util.Set;

public class ArtifactCandidate extends Artifact {
	
	private Set<Clazz> clazzes = new HashSet<>();
	
	public ArtifactCandidate() {
	}
	
	public ArtifactCandidate(String group, String artifact, String version, Set<Clazz> clazzes) {
		super(group, artifact, version);
		this.clazzes = clazzes;
	}
	
	public void addClazz(Clazz clazz) {
		clazzes.add(clazz);
	}
	
	public Set<Clazz> getClazzes() {
		return clazzes;
	}
	
	public void setClazzes(Set<Clazz> clazzes) {
		this.clazzes = clazzes;
	}
}
