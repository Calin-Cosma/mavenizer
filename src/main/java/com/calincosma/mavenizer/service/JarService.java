package com.calincosma.mavenizer.service;

import com.calincosma.mavenizer.config.Config;
import com.calincosma.mavenizer.config.FolderConfig;
import com.calincosma.mavenizer.domain.Clazz;
import com.calincosma.mavenizer.domain.Jar;
import com.calincosma.mavenizer.domain.nexus.Artifact;
import com.calincosma.mavenizer.exception.JarProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class JarService {
	
	public static final Logger LOGGER = LoggerFactory.getLogger(JarService.class);
	
	public static final Pattern JDEPS_PATTERN_NOT_FOUND = Pattern.compile("^\\s+([a-zA-Z._\\-0-9]+)\\s+->\\s+([a-zA-Z._\\-0-9]+)\\W+not found$");
	public static final Pattern JAR_PATTERN_CLASS = Pattern.compile("(.*)/(.+?)\\.class");
	
	
	private NexusService nexusService = new NexusService();
	private TemplateService templateService = new TemplateService();
	
	
	
	public void process(Config config) {
		/* read jars recursively */
		Set<Jar> jars = config.getFolders().stream()
		                                    .flatMap(f -> readJars(Paths.get(f.getPath()), f, f.getGroup()).stream())
		                                    .collect(Collectors.toSet());
		
		/* read jar content, add list of classes jar object */
		jars.stream()
		    .forEach(j -> j.addAll(readJarContents(j)));
		
		/* read all known classes from all jars */
		Map<String, List<Clazz>> knownClasses = jars.stream()
		                                            .flatMap(j -> j.getClasses().values().stream())
		                                            .collect(Collectors.groupingBy(Clazz::getFullName));
		
		/* find all the class dependencies */
		jars.stream().forEach(j -> j.setMissingClassDependencies(findClassDependencies(j)));
		
		jars.stream().forEach(j -> findJarDependencies(j, knownClasses));
		
//		/* try to find the jars corresponding to the class dependencies */
//		Map<Jar, Set<Jar>> dependencies = jars.stream()
//			.collect(Collectors.toMap(j -> j, j -> j.getMissingClassDependencies().stream()
//			                                                                .map(d -> findJarDependencies(d, knownClasses))
//			                                                                .filter(o -> o.isPresent())
//			                                                                .map(c -> c.get().getSource())
//																			.collect(Collectors.toSet())));
//
//		/* set dependencies on jars */
//		dependencies.entrySet().stream()
//		                        .forEach(entry -> entry.getKey().addDependencies(entry.getValue()));
		
		jars.stream().forEach(j -> j.setArtifact(nexusService.findMavenArtifact(j)));
		
		Set<Jar> jarsToUpload = jars.stream()
		                            .filter(j -> j.getArtifact() == null)
		                            .collect(Collectors.toSet());
		
		printResults(jars);
	}
	
	
	private Set<Jar> readJars(Path path, FolderConfig config, String group) {
		Set<Jar> jars = new HashSet<>();
		try {
			jars.addAll(Files.walk(path, 1)
			                 .filter(p -> p.toFile().getAbsolutePath().endsWith(".jar"))
			                 .map(p -> new Jar(p, config, group))
			                 .collect(Collectors.toSet()));
			
			jars.addAll(Files.walk(path, 1)
			                 .filter(p -> p.toFile().isDirectory() && !p.equals(path))
			                 .flatMap(p -> readJars(p, config, group + "." + path.toFile().getName()).stream())
			                 .collect(Collectors.toSet()));
			
		} catch (IOException e) {
			throw new JarProcessingException("Error while processing folder: " + path.toString(), e);
		}
		
		return jars;
	}
	
	public Set<Clazz> readJarContents(Jar jar) {
		Set<Clazz> clazzes = new HashSet<>();
		
		try {
			String command = "jar -tf " + jar.getFullPath();
			LOGGER.info("Analyzing jar " + jar.getFullPath());
			
			ProcessBuilder processBuilder = new ProcessBuilder("/bin/bash", "-c", command);
			final Process process = processBuilder.start();
			BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line;
			while ((line = br.readLine()) != null) {
				LOGGER.debug(line);
				
				Matcher matcher = JAR_PATTERN_CLASS.matcher(line);
				while (matcher.find()) {
					String packageName = matcher.group(1).replaceAll("/", ".");
					String className = matcher.group(2);
					Clazz clazz = new Clazz(packageName, className, jar);
					clazzes.add(clazz);
				}
			}
		} catch (IOException e) {
			throw new JarProcessingException("Error processing jar: " + jar, e);
		}
		
		return clazzes;
	}
	
	private Set<Clazz> findClassDependencies(Jar jar) {
		String jdeps = "jdeps -v " + jar.getFullPath();
		LOGGER.info("Executing " + jdeps);
		
		Set<Clazz> missingDependencies = new HashSet<>();
		
		try {
			ProcessBuilder pb = new ProcessBuilder("/bin/bash", "-c", jdeps);
			final Process p = pb.start();
			BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line;
			while ((line = br.readLine()) != null) {
				LOGGER.debug("\t\t" + line);
				
				Matcher m = JDEPS_PATTERN_NOT_FOUND.matcher(line);
				if (m.find()) {
					String sourceClass = m.group(1);
					String dependencyClass = m.group(2);
					String packageName = dependencyClass.substring(0, dependencyClass.lastIndexOf("."));
					String className = dependencyClass.substring(dependencyClass.lastIndexOf(".") + 1);
					missingDependencies.add(new Clazz(packageName, className));
				}
			}
		} catch (IOException e) {
			throw new JarProcessingException("Error while analyzing jar dependencies for jar: " + jar, e);
		}
		
		return missingDependencies;
	}
	
	private <T extends Collection<Clazz>> void findJarDependencies(Jar jar, Map<String, T> classes) {
		Map<Clazz, List<Artifact>> cache = new HashMap<>();
		
		for (Clazz missingClass : jar.getMissingClassDependencies()) {
			String className = missingClass.getFullName();
			LOGGER.debug("Finding dependency for " + className);
			
			
			T clazzes = classes.get(missingClass.getFullName());
			if (clazzes == null || clazzes.size() <= 0) {
				LOGGER.warn("Dependency not found in local jars for " + missingClass);
				
				
				List<Artifact> candidates = cache.get(missingClass);
				if (candidates == null) {
					candidates = nexusService.findByClass(missingClass.getFullName());
					cache.put(missingClass, candidates);
				}
				
				candidates.stream().forEach(c -> jar.addCandidateArtifact(c.getGroup(), c.getArtifact(), c.getVersion(), missingClass));
			} else if (clazzes.size() > 1) {
				clazzes.stream().forEach(c -> jar.addCandidateJar(c.getSource(), c));
				LOGGER.error("Multiple dependencies found for "
						+ missingClass
						+ " ("
						+ String.join(", ", clazzes.stream()
						                           .map(c -> c.getSource().getFullPath())
						                           .collect(Collectors.toList())) +
						")");
			} else {
				Clazz clazz = clazzes.iterator().next();
				jar.getClassDependencies().add(clazz);
				LOGGER.debug("Dependency found for " + missingClass + " -> " + clazz.getSource().getFullPath());
			}
		}
		
		jar.getMissingClassDependencies().removeAll(jar.getClassDependencies());
	}
	
	
	
	private void processCandidates(Set<Jar> jars) {
		
	}
	
	
	
	private <T extends Collection<Clazz>> Optional<Clazz> findJarDependencies(String missingDependency, Map<String, T> classes) {
		LOGGER.debug("Finding dependency for " + missingDependency);
		T clazzes = classes.get(missingDependency);
		if (clazzes == null || clazzes.size() <= 0) {
			LOGGER.error("Dependency not found for " + missingDependency);
			return Optional.empty();
		}
		
		if (clazzes.size() > 1) {
			LOGGER.error("Multiple dependencies found for "
					+ missingDependency
					+ " ("
					+ String.join(", ", clazzes.stream()
					                           .map(c -> c.getSource().getFullPath())
					                           .collect(Collectors.toList())) +
					")");
			return Optional.empty();
		}
			
		Clazz clazz = clazzes.iterator().next();
		LOGGER.debug("Dependency found for " + missingDependency + " -> " + clazz.getSource().getFullPath());
		return Optional.of(clazz);
	}
	
	
	
	
	
	private void printDependencies(Jar jar) {
		LOGGER.info(jar.toString());
		jar.getDependencies().stream().map(d -> "\t\t|-> " + d).forEach(LOGGER::info);
	}
	
	
	
	private void printResults(Set<Jar> jars) {
		LOGGER.info("Jars containing themselves as dependencies");
		jars.stream().filter(j -> j.getDependencies().contains(j)).map(Jar::toString).forEach(LOGGER::info);
		
		LOGGER.info("Dependencies found");
		jars.stream().forEach(this::printDependencies);
		
		LOGGER.info("Bye bye");
	}
}
