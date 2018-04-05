package com.calincosma.mavenizer;

import com.calincosma.mavenizer.config.Config;
import com.calincosma.mavenizer.exception.ConfigNotFoundException;
import com.calincosma.mavenizer.service.JarService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
	
	public static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
	
	//	public static final String BASE_PATH = "/work/oniryx/Axway";
	//	public static final String AXWAY_PATH = "/apigateway/system/lib";
	
	public static final String ARTIFACT_GROUP = "com.vordel";
	public static final String ARTIFACT_VERSION = "7.5.3-8";
	
	public static final Pattern ARTIFACT_NAME_PATTERN = Pattern.compile("^(?:\\Q" + ARTIFACT_GROUP + ".\\E)?(.+?)(-\\d.*)?\\Q.jar\\E$");
	
	public static final String NEXUS_URL = "http://search.maven.org/solrsearch/select?q=1:{0}&rows=20&wt=json";
	
	
	
	public static void main(String[] args) {
		Config config = loadConfig(args.length >= 1 ? args[0] : null);
		
		JarService jarService = new JarService();
		jarService.process(config);
		
		
		
	
		
//		List<Path> jarFiles = getJarFiles(args[0]);


//		Matrix matrix = jarService.buildMatrix(jarFiles);


		
		/*
		System.out.println("\n\n\n");
		System.out.println("Top level jars");
		List<Jar> jars = matrix.getJars().stream().filter(j -> j.getReverseDependencies().isEmpty()).collect(Collectors.toList());
		jars.stream().map(j -> j.getFullPath()).forEach(System.out::println);


		System.out.println("\n\n\n");
		System.out.println("Jars with dependencies");
		jars = matrix.getJars().stream().filter(j -> !j.getReverseDependencies().isEmpty()).collect(Collectors.toList());
		jars.stream().map(j -> j.getFullPath()).forEach(System.out::println);

		matrix.getJars().stream().forEach(j -> j.setArtifact(nexusService.findMavenArtifact(j)));
		matrix.getJars().stream().filter(j -> j.getArtifact() != null).map(j -> j.getArtifact()).forEach(System.out::println);
		
		*/
	}
	
	
	
	
	

//	private static List<Path> getJarFiles(String... folders) throws IOException {
//		List<Path> jarFiles = new ArrayList<>();
//
//		for (String folder : folders) {
//			jarFiles.addAll(Files.walk(Paths.get(folder), 1)
//			                     .filter(p -> p.toFile().getAbsolutePath().endsWith(".jar"))
//			                     .collect(Collectors.toList()));
//		}
//
//		return jarFiles;
//	}
	
	
	
	private static Config loadConfig(String configFileName) {
		try {
			if (configFileName == null || configFileName.trim().length() == 0)
				throw new ConfigNotFoundException();
			
			Path configFile = Paths.get(configFileName);
			if (!configFile.toFile().exists())
				throw new ConfigNotFoundException();
			
			String content = new String(Files.readAllBytes(configFile));
			
			Gson gson = new GsonBuilder().create();
			Config config = gson.fromJson(content, Config.class);
			return config;
		} catch (ConfigNotFoundException e) {
			LOGGER.warn("No config found");
			return new Config();
		} catch (IOException e) {
			LOGGER.warn("Couldn't read config file", e);
			return new Config();
		}
	}
	
	
	
	private static void extractArtifactDetails(String t1) {
		System.out.println(t1);
		Matcher m = ARTIFACT_NAME_PATTERN.matcher(t1);
		if (m.find()) {
			System.out.println("Group: " + ARTIFACT_GROUP);
			System.out.println("Artifact: " + m.group(1).replaceAll("\\.", "-"));
			String version = m.group(2);
			version = version != null && version.trim().length() > 0 ? version.substring(1) : ARTIFACT_VERSION;
			System.out.println("Version: " + version);
		}
	}
	
	
	private static void test() {
		
		//		Config config = new Config();
		//
		//		FolderConfig f1 = new FolderConfig();
		//		f1.setPath("/work/oniryx/Axway/apigateway/system/lib");
		//		f1.setGroup("com.vordel");
		//		f1.setArtifactPattern("^(?:\\Q" + ARTIFACT_GROUP + ".\\E)?(.+?)(-\\d.*)?\\Q.jar\\E$");
		//		f1.setVersion("7.5.3-8");
		//
		//		config.add(f1);
		//
		//		FolderConfig f2 = new FolderConfig();
		//		f2.setPath("/work/oniryx/Axway/apigateway/system/lib/plugins");
		//		f2.setGroupSuffix(".plugins");
		//		f2.setGroup("com.vordel");
		//		f2.setArtifactPattern("^(?:\\Q" + ARTIFACT_GROUP + ".\\E)?(.+?)(-\\d.*)?\\Q.jar\\E$");
		//		f2.setVersion("7.5.3-8");
		//
		//		config.add(f2);
		//
		//
		//		Gson gson = new GsonBuilder().create();
		//		System.out.println(gson.toJson(config));
		
		
		
		//		String t1 = "pkcs11-java-client-7.5.3-8.jar";
		//		extractArtifactDetails(t1);
		//
		//		System.out.println("\n\n\n");
		//
		//
		//		t1 = "com.vordel.circuit.authn.jar";
		//		extractArtifactDetails(t1);
		//
		//		System.out.println("\n\n\n");
		//
		//
		//		t1 = "circuit.jar";
		//		extractArtifactDetails(t1);
		//
		//		System.out.println("\n\n\n");
		//
		//
		//		t1 = "zdd-api-importer-1.0.0-3.jar";
		//		extractArtifactDetails(t1);
		
		
	}
	
}
