package com.calincosma.dependencymatrix.domain;

import com.calincosma.dependencymatrix.domain.nexus.Artifact;

import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Jar {
	
	private Path path;
	private Map<String, Clazz> classes = new HashMap<>();
	private Set<String> packages = new HashSet<>();
	private Set<Jar> dependencies = new HashSet<>();
	private Set<Jar> reverseDependencies = new HashSet<>();
	private Artifact artifact;
	
	public Jar(Path path) {
		this.path = path;
	}
	
	public Jar() {
	}
	
	public void addAll(Collection<Clazz> clazzes) {
		clazzes.stream().forEach(c -> add(c));
	}
	
	public void add(Clazz clazz) {
		classes.put(clazz.getFullName(), clazz);
		packages.add(clazz.getPackageName());
		clazz.setSource(this);
	}
	
	public void addDependency(Jar jar) {
		dependencies.add(jar);
	}
	
	
	public void addReverseDependency(Jar jar) {
		reverseDependencies.add(jar);
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		
		Jar jar = (Jar)o;
		
		return path != null ? path.equals(jar.path) : jar.path == null;
	}
	
	@Override
	public int hashCode() {
		return path != null ? path.hashCode() : 0;
	}
	
	public String getFullPath() {
		return path.toFile().getAbsolutePath();
	}
	
	public String getName() {
		return path.toFile().getName();
	}
	
	public Path getPath() {
		return path;
	}
	
	public void setPath(Path path) {
		this.path = path;
	}
	
//	public Map<String, Jar> getMissingDependencies() {
//		return missingDependencies;
//	}
//
//	public void setMissingDependencies(Map<String, Jar> missingDependencies) {
//		this.missingDependencies = missingDependencies;
//	}
	
	public Artifact getArtifact() {
		return artifact;
	}
	
	public void setArtifact(Artifact artifact) {
		this.artifact = artifact;
	}
	
	public Set<Jar> getDependencies() {
		return dependencies;
	}
	
	public Set<Jar> getReverseDependencies() {
		return reverseDependencies;
	}
}
