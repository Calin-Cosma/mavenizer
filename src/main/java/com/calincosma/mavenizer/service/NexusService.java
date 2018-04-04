package com.calincosma.mavenizer.service;

import com.calincosma.mavenizer.Main;
import com.calincosma.mavenizer.domain.Jar;
import com.calincosma.mavenizer.domain.nexus.Artifact;
import com.calincosma.mavenizer.domain.nexus.NexusResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.annotation.adapters.HexBinaryAdapter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.MessageFormat;

public class NexusService {
	
	public static final Logger LOGGER = LoggerFactory.getLogger(NexusService.class);
	
	public String calcSHA1(File file) throws IOException, NoSuchAlgorithmException {
		MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
		try (InputStream input = new FileInputStream(file)) {
			
			byte[] buffer = new byte[8192];
			int len = input.read(buffer);
			
			while (len != -1) {
				sha1.update(buffer, 0, len);
				len = input.read(buffer);
			}
			
			return new HexBinaryAdapter().marshal(sha1.digest());
		}
	}
	
	
	
	public String sha1Search(String sha1) {
		Client client = Client.create();
		WebResource webResource = client.resource(MessageFormat.format(Main.NEXUS_URL, sha1));
		ClientResponse response = webResource.accept("application/json").get(ClientResponse.class);
		if (response.getStatus() != 200) {
			throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
		}
		return response.getEntity(String.class);
	}
	
	
	
	public Artifact findMavenArtifact(Jar jar) {
		try {
			String sha1 = calcSHA1(new File(jar.getFullPath()));
			String json = sha1Search(sha1);
			Gson gson = new GsonBuilder().create();
			NexusResponse nexusResponse = gson.fromJson(json, NexusResponse.class);
			
			if (nexusResponse.getResponseHeader().getStatus() != 0)
				throw new RuntimeException("SHA1 checksum search failed with status: " + nexusResponse.getResponseHeader().getStatus());
			
			if (nexusResponse.getResponse().getNumFound() > 1)
				throw new RuntimeException("SHA1 checksum search returned too many results");
			
			if (nexusResponse.getResponse().getNumFound() == 0) {
				LOGGER.info("No Maven artifact found for " + jar.getFullPath());
				return null;
			}
			
			LOGGER.info("Found Maven artifact for " + jar.getFullPath() + "; " + nexusResponse.getResponse().getArtifacts().get(0));
			return nexusResponse.getResponse().getArtifacts().get(0);
		} catch (Exception e) {
			LOGGER.error("Error while searching Maven artifact for " + jar.getFullPath(), e);
			return null;
		}
	}
}
