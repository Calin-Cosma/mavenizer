package com.calincosma.dependencymatrix.service;

import com.calincosma.dependencymatrix.Main;
import com.calincosma.dependencymatrix.domain.Clazz;
import com.calincosma.dependencymatrix.domain.Matrix;
import com.calincosma.dependencymatrix.domain.Jar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JarService {
	
	public static final Logger LOGGER = LoggerFactory.getLogger(JarService.class);
	
	public static final Pattern JDEPS_PATTERN_NOT_FOUND = Pattern.compile("^\\s+([a-zA-Z._\\-0-9]+)\\s+->\\s+([a-zA-Z._\\-0-9]+)\\W+not found$");
	public static final Pattern JAR_PATTERN_CLASS = Pattern.compile("(.*)/(.+?)\\.class");
	
	public Matrix buildMatrix(List<Path> jarFiles) throws IOException {
		Matrix matrix = new Matrix();
		for (Path path : jarFiles) {
//			if (!path.toFile().getName().startsWith("com.vordel.circuit"))
//				continue;
			
			Jar jar = new Jar(path);
			Main.LOGGER.debug("Processing jar " + jar.getName() + " @ " + jar.getFullPath());
			Set<Clazz> clazzes = readJarContents(jar);
//			jar.addAll(clazzes);
//
//			//				jar.add(clazz);
//							matrix.addClass(clazz);
			matrix.add(jar, clazzes);
		}
		
		
		//		for (Path path : jarFiles) {
		//			Jar jar = new Jar(path);
		//			LOGGER.debug("Unzipping " + jar.getName() + " @ " + jar.getFullPath());
		//			jarService.unzip(jar);
		//			matrix.addJar(jar);
		//		}
		//
		for (Path path : jarFiles) {
			Jar jar = matrix.getJar(path);
			Main.LOGGER.debug("Jdeps " + jar.getName() + " @ " + jar.getFullPath());
			jdeps(jar, matrix);
		}
		
		return matrix;
	}
	
	
	
	public Set<Clazz> readJarContents(Jar jar) throws IOException {
		Set<Clazz> clazzes = new HashSet<>();
		
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
				Clazz clazz = new Clazz(packageName, className);
				clazzes.add(clazz);
				
//				jar.add(clazz);
//				matrix.addClass(clazz);
//				LOGGER.debug(clazz.getFullName());
			}
		}
		
		return clazzes;
	}
	
	
	
	
//	public void unzip(Jar jar) throws IOException {
//
//		String unzipDir = jar.getFullPath().substring(0, jar.getFullPath().length() - 4);
//		String unzip = "unzip -u " + jar.getFullPath() + " -d " + unzipDir;
//		LOGGER.debug("Executing " + unzip);
//
//		ProcessBuilder processBuilder = new ProcessBuilder("/bin/bash", "-c", unzip);
//		final Process process = processBuilder.start();
//		BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
//		String line;
//		while ((line = br.readLine()) != null) {
//			LOGGER.debug(line);
//		}
//
//		Path folder = Paths.get(unzipDir);
//		String pack = "";
//		processSubfolder(folder, pack, jar);
//
//		LOGGER.debug("Packages for " + jar.getFullPath());
//		for (String packk : jar.getPackages()) {
//			LOGGER.debug(packk);
//		}
//	}
	
	
	
//	public boolean processSubfolder(Path folder, String pack, Jar jar) throws IOException {
//		boolean isPackage = Files.find(folder, 1, (path, attr) ->
//				String.valueOf(path).endsWith(".class")).count() > 0;
//
//		if (isPackage)
//			jar.getPackages().add(pack);
//
//		DirectoryStream<Path> subfolders = Files.newDirectoryStream(folder, new DirectoryStream.Filter<Path>() {
//			public boolean accept(Path file) throws IOException {
//				return (file.toFile().isDirectory());
//			}
//		});
//
//		for (Path subfolder : subfolders) {
//			String subPackage = pack + (pack.length() > 0 ? "." : "") + subfolder.toFile().getName();
//
//			boolean isSubPackage = processSubfolder(subfolder, subPackage, jar);
//
//			if (isSubPackage) {
//				isPackage = true;
//			}
//		}
//
//		return isPackage;
//	}
	
	
	
	public void jdeps(Jar jar, Matrix matrix) throws IOException {
		String jdeps = "jdeps -v " + jar.getFullPath();
		Set<String> missingClasses = jdeps(jdeps);
		
		for (String missingDependency : missingClasses) {
			LOGGER.debug("Finding dependency for " + missingDependency);
			Set<Clazz> clazzes = matrix.getDependencies(missingDependency);
			if (clazzes == null || clazzes.size() <= 0) {
				LOGGER.warn("Dependency not found for " + missingDependency);
			} else if (clazzes.size() > 1) {
				LOGGER.warn("Multiple dependencies found for " + missingDependency);
				for (Clazz dependencyJar : clazzes) {
					LOGGER.warn("\t\t" + dependencyJar.getSource().getFullPath());
				}
			} else {
				Clazz clazz = clazzes.iterator().next();
				LOGGER.debug("Dependency found for " + missingDependency + " -> " + clazz.getSource().getFullPath());
				jar.addDependency(clazz.getSource());
				clazz.getSource().addReverseDependency(jar);
			}
		}
	}
	
	
	
	public Set<String> jdeps(String jdeps) throws IOException {
		LOGGER.info("Executing " + jdeps);
		
		Set<String> missingDependecies = new HashSet<>();
		
		ProcessBuilder pb = new ProcessBuilder("/bin/bash", "-c", jdeps);
		final Process p = pb.start();
		BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
		String line;
		while ((line = br.readLine()) != null) {
//			LOGGER.info("\t\t" + line);
			
			Matcher m = JDEPS_PATTERN_NOT_FOUND.matcher(line);
			if (m.find()) {
//				LOGGER.info("\t\t" + line);
				String pack = m.group(1);
				missingDependecies.add(pack);
			} else {
//				LOGGER.error("\t\t" + line);
			}
		}
		
		return missingDependecies;
	}
}
