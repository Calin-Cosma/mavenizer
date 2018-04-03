package com.calincosma.dependencymatrix.service;

import com.calincosma.dependencymatrix.domain.Jar;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.io.StringWriter;

public class TemplateService {
	
	private TemplateEngine engine;
	
	public TemplateService() {
		ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
		resolver.setTemplateMode("XML");
		resolver.setSuffix(".xml");
		engine = new TemplateEngine();
		engine.setTemplateResolver(resolver);
	}
	
	
	public void processPom(Jar jar) {
		StringWriter writer = new StringWriter();
		Context context = new Context();
		context.setVariable("group", jar.getArtifact().getGroup());
		context.setVariable("artifact", jar.getArtifact().getArtifact());
		context.setVariable("version", jar.getArtifact().getVersion());
		engine.process("pom.template", context, writer);
		
		System.out.println(writer.toString());
	}
}
