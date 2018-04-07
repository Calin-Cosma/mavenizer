package com.calincosma.mavenizer.domain;

import com.calincosma.mavenizer.config.FolderConfig;
import com.calincosma.mavenizer.domain.nexus.Artifact;

import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class Jar {
	
	private Path path;
	private Map<String, Clazz> classes = new HashMap<>();
	private Set<String> packages = new HashSet<>();
	private Set<Jar> dependencies = new HashSet<>();
	private Set<Jar> reverseDependencies = new HashSet<>();
	private Set<Clazz> missingClassDependencies = new HashSet<>();
	private Set<Clazz> classDependencies = new HashSet<>();
	private Artifact artifact;
	private Map<String, Map<String, Map<String, Set<Clazz>>>> candidateArtifacts = new HashMap<>();
	private Map<Jar, Set<Clazz>> candidateJars = new HashMap<>();
	private FolderConfig folderConfig;
	private String group;
	private String sha1;
	
	public Jar(Path path) {
		this.path = path;
	}
	
	public Jar(Path path, String group) {
		this.path = path;
		this.group = group;
	}
	
	public Jar(Path path, FolderConfig folderConfig, String group) {
		this.path = path;
		this.folderConfig = folderConfig;
		this.group = group;
	}
	
	
	public void addCandidateJar(Jar jar, Clazz clazz) {
		Set<Clazz> clazzes = candidateJars.get(jar);
		if (clazzes == null) {
			clazzes = new HashSet<>();
			candidateJars.put(jar, clazzes);
		}
		
		clazzes.add(clazz);
	}
	
	
	public void addCandidateArtifact(String group, String name, String version, Clazz clazz) {
		Map<String, Map<String, Set<Clazz>>> artifacts = candidateArtifacts.get(group);
		if (artifacts == null) {
			artifacts = new HashMap<>();
			candidateArtifacts.put(group, artifacts);
		}
		
		Map<String, Set<Clazz>> versions = artifacts.get(name);
		if (versions == null) {
			versions = new HashMap<>();
			artifacts.put(name, versions);
		}
		
		Set<Clazz> clazzes = versions.get(version);
		if (clazzes == null) {
			clazzes = new HashSet<>();
			versions.put(name, clazzes);
		}
		
		clazzes.add(clazz);
	}
	
	
	public Set<Artifact> getCandidateArtifactsSet() {
		return candidateArtifacts.entrySet()
		                  .stream()
		                  .flatMap(entry1 -> entry1.getValue().entrySet()
		                                           .stream()
		                                           .flatMap(entry2 -> entry2.getValue().entrySet()
		                                                                    .stream()
		                                                                    .map(entry3 -> new Artifact(entry1.getKey(), entry2.getKey(), entry3.getKey()))))
		                  .collect(Collectors.toSet());
	}
	
	
	public Set<ArtifactCandidate> getCandidateArtifactsMap() {
		return candidateArtifacts.entrySet()
		                         .stream()
		                         .flatMap(entry1 -> entry1.getValue().entrySet()
		                                                  .stream()
		                                                  .flatMap(entry2 -> entry2.getValue().entrySet()
		                                                                           .stream()
		                                                                           .map(entry3 -> new ArtifactCandidate(entry1.getKey(), entry2.getKey(), entry3.getKey(),
				                                                                           entry3.getValue()))))
				.collect(Collectors.toSet());
				                                                                         
				                                                                         
//				                                                                           new ArtifactCandidate(entry1.getKey(), entry2.getKey(), entry3.getKey(),
//				                                                                           entry3.getValue()))))
//		                         ..collect(
//				Collectors.toMap();
	}
	
	
	public void addAll(Collection<Clazz> clazzes) {
		clazzes.stream().forEach(c -> add(c));
	}
	
	public void add(Clazz clazz) {
		classes.put(clazz.getFullName(), clazz);
		packages.add(clazz.getPackageName());
		clazz.setSource(this);
	}
	
	
	public <C extends Collection<Jar>> void addDependencies(C jars) {
		dependencies.addAll(jars);
		jars.stream().forEach(j -> j.addReverseDependency(this));
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
	public String toString() {
		return "Jar{" +
				"file=" + path.toFile().getName() +
				", path=" + path +
				", artifact=" + artifact +
				'}';
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
	
	public FolderConfig getFolderConfig() {
		return folderConfig;
	}
	
	public void setFolderConfig(FolderConfig folderConfig) {
		this.folderConfig = folderConfig;
	}
	
	public String getGroup() {
		return group;
	}
	
	public void setGroup(String group) {
		this.group = group;
	}
	
	public Map<String, Clazz> getClasses() {
		return classes;
	}
	
	public void setClasses(Map<String, Clazz> classes) {
		this.classes = classes;
	}
	
	public Set<String> getPackages() {
		return packages;
	}
	
	public void setPackages(Set<String> packages) {
		this.packages = packages;
	}
	
	public void setDependencies(Set<Jar> dependencies) {
		this.dependencies = dependencies;
	}
	
	public void setReverseDependencies(Set<Jar> reverseDependencies) {
		this.reverseDependencies = reverseDependencies;
	}
	
	public Set<Clazz> getMissingClassDependencies() {
		return missingClassDependencies;
	}
	
	public void setMissingClassDependencies(Set<Clazz> missingClassDependencies) {
		this.missingClassDependencies = missingClassDependencies;
	}
	
	public Set<Clazz> getClassDependencies() {
		return classDependencies;
	}
	
	public void setClassDependencies(Set<Clazz> classDependencies) {
		this.classDependencies = classDependencies;
	}
	
	public Map<Jar, Set<Clazz>> getCandidateJars() {
		return candidateJars;
	}
	
	public void setCandidateJars(Map<Jar, Set<Clazz>> candidateJars) {
		this.candidateJars = candidateJars;
	}
	
	public void setCandidateArtifacts(
			Map<String, Map<String, Map<String, Set<Clazz>>>> candidateArtifacts) {
		this.candidateArtifacts = candidateArtifacts;
	}
	
	public String getSha1() {
		return sha1;
	}
	
	public void setSha1(String sha1) {
		this.sha1 = sha1;
	}
	
	public Map<String, Map<String, Map<String, Set<Clazz>>>> getCandidateArtifacts() {
		return candidateArtifacts;
	}
}
