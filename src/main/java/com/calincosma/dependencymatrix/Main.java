package com.calincosma.dependencymatrix;

import com.calincosma.dependencymatrix.domain.Jar;
import com.calincosma.dependencymatrix.domain.Matrix;
import com.calincosma.dependencymatrix.service.JarService;
import com.calincosma.dependencymatrix.service.NexusService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Main {
	
	public static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
	
	//	public static final String BASE_PATH = "/work/oniryx/Axway";
	//	public static final String AXWAY_PATH = "/apigateway/system/lib";
	
	public static final String VERSION = "7.5.3-8";
	
	public static final String NEXUS_URL = "http://search.maven.org/solrsearch/select?q=1:{0}&rows=20&wt=json";
	
	
	
	public static void main(String[] args) throws Exception {
		List<Path> jarFiles = getJarFiles(args[1]);
		
		JarService jarService = new JarService();
		NexusService nexusService = new NexusService();
		
		Matrix matrix = jarService.buildMatrix(jarFiles);
		
		
		System.out.println("\n\n\n");
		System.out.println("Top level jars");
		
		
		List<Jar> jars = matrix.getJars().stream().filter(j -> j.getReverseDependencies().isEmpty()).collect(Collectors.toList());
		jars.stream().map(j -> j.getFullPath()).forEach(System.out::println);
		
		matrix.getJars().stream().forEach(j -> j.setArtifact(nexusService.findMavenArtifact(j)));
		matrix.getJars().stream().filter(j -> j.getArtifact() != null).map(j -> j.getArtifact()).forEach(System.out::println);
	}
	
	
	
	private static List<Path> getJarFiles(String... folders) throws IOException {
		List<Path> jarFiles = new ArrayList<>();
		
		for (String folder : folders) {
			jarFiles.addAll(Files.walk(Paths.get(folder), 1)
			                     .filter(p -> p.toFile().getAbsolutePath().endsWith(".jar"))
			                     .collect(Collectors.toList()));
		}
		
		return jarFiles;
	}
	
	
}
