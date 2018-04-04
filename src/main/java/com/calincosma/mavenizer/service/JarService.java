package com.calincosma.mavenizer.service;

import com.calincosma.mavenizer.Main;
import com.calincosma.mavenizer.config.Config;
import com.calincosma.mavenizer.config.FolderConfig;
import com.calincosma.mavenizer.domain.Clazz;
import com.calincosma.mavenizer.domain.Jar;
import com.calincosma.mavenizer.domain.Matrix;
import com.calincosma.mavenizer.exception.ClassDependencyNotFoundException;
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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
				
//		for (FolderConfig folderConfig : config.getFolders()) {
//			jars.addAll(readJars(Paths.get(folderConfig.getPath()), folderConfig, folderConfig.getGroup()));
//		}
		
		/* read jar content, add list of classes jar object */
		jars.stream()
		    .forEach(j -> j.addAll(readJarContents(j)));
		
		Map<String, List<Clazz>> knownClasses = jars.stream()
		                                            .flatMap(j -> j.getClasses().values().stream())
		                                            .collect(Collectors.groupingBy(Clazz::getFullName));
	
		jars.stream().forEach(j -> j.setClassDependencies(findClassDependencies(j)));
		
		Map<Jar, List<Clazz>> dependencies = jars.stream()
			.collect(Collectors.toMap(j -> j, j -> j.getClassDependencies().stream()
			                                                                .map(d -> findJarDependencies(d, knownClasses))
																			.collect(Collectors.toList())));
		
		dependencies.entrySet().stream().forEach();
	}
	
	
	private Set<Jar> readJars(Path path, FolderConfig config, String group) {
		Set<Jar> jars = new HashSet<>();
		try {
			jars.addAll(Files.walk(path, 1)
			                 .filter(p -> p.toFile().getAbsolutePath().endsWith(".jar"))
			                 .map(p -> new Jar(p, config, group))
			                 .collect(Collectors.toSet()));
			
			jars.addAll(Files.walk(path, 1)
			                 .filter(p -> p.toFile().isDirectory())
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
	
	
	private Set<String> findClassDependencies(Jar jar) {
		String jdeps = "jdeps -v " + jar.getFullPath();
		LOGGER.info("Executing " + jdeps);
		
		Set<String> missingDependencies = new HashSet<>();
		
		try {
			ProcessBuilder pb = new ProcessBuilder("/bin/bash", "-c", jdeps);
			final Process p = pb.start();
			BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line;
			while ((line = br.readLine()) != null) {
				LOGGER.debug("\t\t" + line);
				
				Matcher m = JDEPS_PATTERN_NOT_FOUND.matcher(line);
				if (m.find()) {
					String pack = m.group(1);
					missingDependencies.add(pack);
				}
			}
		} catch (IOException e) {
			throw new JarProcessingException("Error while analyzing jar dependencies for jar: " + jar, e);
		}
		
		return missingDependencies;
	}
	
	
	public void findJarDependencies(Jar jar, Map<String, Set<Clazz>> classes) throws IOException {
		Set<String> missingClasses = findClassDependencies(jar);
		
		for (String missingDependency : jar.getClassDependencies()) {
			findJarDependencies(missingDependency, classes);
		}
	}
	
	
	private <T extends Collection<Clazz>> Clazz findJarDependencies(String missingDependency, Map<String, T> classes) {
		LOGGER.debug("Finding dependency for " + missingDependency);
		T clazzes = classes.get(missingDependency);
		if (clazzes == null || clazzes.size() <= 0)
			throw new ClassDependencyNotFoundException("Dependency not found for " + missingDependency);
		
		if (clazzes.size() > 1) {
			throw new ClassDependencyNotFoundException("Multiple dependencies found for "
					+ missingDependency
					+ " ("
					+ String.join(", ", clazzes.stream()
					                                    .map(c -> c.getSource().getFullPath())
					                                    .collect(Collectors.toList())) +
					")");
			
		Clazz clazz = clazzes.iterator().next();
		LOGGER.debug("Dependency found for " + missingDependency + " -> " + clazz.getSource().getFullPath());
//		jar.addDependency(clazz.getSource());
//		clazz.getSource().addReverseDependency(jar);
		return clazz;
		}
		
		
	}
	
//	private void findJarDependencies(Jar jar, String missingDependency, Map<String, Set<Clazz>> classes) {
//		LOGGER.debug("Finding dependency for " + missingDependency);
//		Set<Clazz> clazzes = classes.get(missingDependency);
//		if (clazzes == null || clazzes.size() <= 0) {
//			LOGGER.warn("Dependency not found for " + missingDependency);
//		} else if (clazzes.size() > 1) {
//			LOGGER.warn("Multiple dependencies found for " + missingDependency);
//			for (Clazz dependencyJar : clazzes) {
//				LOGGER.warn("\t\t" + dependencyJar.getSource().getFullPath());
//			}
//		} else {
//			Clazz clazz = clazzes.iterator().next();
//			LOGGER.debug("Dependency found for " + missingDependency + " -> " + clazz.getSource().getFullPath());
//			jar.addDependency(clazz.getSource());
//			clazz.getSource().addReverseDependency(jar);
//		}
//	}
	
	
	
	public Matrix buildMatrix(List<Path> jarFiles) throws IOException {
		Matrix matrix = new Matrix();
		for (Path path : jarFiles) {
			Jar jar = new Jar(path);
			Main.LOGGER.debug("Processing jar " + jar.getName() + " @ " + jar.getFullPath());
			Set<Clazz> clazzes = readJarContents(jar);
			matrix.add(jar, clazzes);
		}
		
		for (Path path : jarFiles) {
			Jar jar = matrix.getJar(path);
			Main.LOGGER.debug("Jdeps " + jar.getName() + " @ " + jar.getFullPath());
			findJarDependencies(jar, matrix.getClasses());
		}
		
		return matrix;
	}
	
	
	public void extractArtifactInfo(Jar jar) {
	
	}
}
