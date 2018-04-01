package com.calincosma.dependencymatrix.domain;

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
	
	Map<Path, Jar> jars = new HashMap<>();
	Map<String, Set<Clazz>> dependencies = new HashMap<>();
	
	
	
	public void add(Jar jar, Collection<Clazz> clazzes) {
		addJar(jar);
		jar.addAll(clazzes);
		clazzes.stream().forEach(this::addClass);
	}
	
	
	public void addClass(Clazz clazz) {
		Set<Clazz> classes = dependencies.get(clazz.getFullName());
		if (classes == null) {
			classes = new HashSet<>();
			dependencies.put(clazz.getFullName(), classes);
		}
		classes.add(clazz);
	}
	
	
	
	public void addJar(Jar jar) {
		jars.put(jar.getPath(), jar);
		
//		for (String pack : jar.getPackages()) {
//			List<Jar> packages = dependencies.get(pack);
//			if (packages == null) {
//				packages = new ArrayList<>();
//				dependencies.put(pack, packages);
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
		return dependencies.get(clazzName);
//		List<Jar> jars = dependencies.get(pack);
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
//		return dependencies;
//	}
//
//	public void setDependencies(Map<String, List<Jar>> dependencies) {
//		this.dependencies = dependencies;
//	}
}
