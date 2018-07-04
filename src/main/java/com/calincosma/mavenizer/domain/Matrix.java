package com.calincosma.mavenizer.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Matrix {
	
	public static final Logger LOGGER = LoggerFactory.getLogger(Matrix.class);
	
	private Map<Path, Jar> jars = new HashMap<>();
	private Map<String, Set<Clazz>> classes = new HashMap<>();
	
	
	
	public void add(Jar jar, Collection<Clazz> clazzes) {
		addJar(jar);
		jar.addAll(clazzes);
		clazzes.stream().forEach(this::addClass);
	}
	
	
	public void addClass(Clazz clazz) {
		Set<Clazz> classes = this.classes.get(clazz.getFullName());
		if (classes == null) {
			classes = new HashSet<>();
			this.classes.put(clazz.getFullName(), classes);
		}
		classes.add(clazz);
	}
	
	
	
	public void addJar(Jar jar) {
		jars.put(jar.getPath(), jar);
		
//		for (String pack : jar.getPackages()) {
//			List<Jar> packages = classes.get(pack);
//			if (packages == null) {
//				packages = new ArrayList<>();
//				classes.put(pack, packages);
//			} else {
//				LOGGER.warn("Multiple jars for package " + pack);
//			}
//			packages.add(jar);
//		}
	}
	
	public Jar getJar(Path path) {
		return jars.get(path);
	}
	
	
	public Set<Clazz> getDependencies(String clazzName) {
		return classes.get(clazzName);
//		List<Jar> jars = classes.get(pack);
//		if (jars == null) {
//			LOGGER.warn("No jars for package " + pack);
//		} else if (jars.size() > 1) {
//			LOGGER.warn("Multiple jars for package " + pack);
//		} else {
//			return jars.get(0);
//		}
//		return null;
	}
	
	public Collection<Jar> getJars() {
		return jars.values();
	}

//	public void setJars(List<Jar> jars) {
//		this.jars = jars;
//	}
//
//	public Map<String, List<Jar>> getDependencies() {
//		return classes;
//	}
//
//	public void setDependencies(Map<String, List<Jar>> classes) {
//		this.classes = classes;
//	}
	
	
	public void setJars(Map<Path, Jar> jars) {
		this.jars = jars;
	}
	
	public Map<String, Set<Clazz>> getClasses() {
		return classes;
	}
	
	public void setClasses(Map<String, Set<Clazz>> classes) {
		this.classes = classes;
	}
}
