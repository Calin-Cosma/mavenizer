<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
	<modelVersion>4.0.0</modelVersion>
	
	<groupId>${group}</groupId>
	<artifactId>${artifact}</artifactId>
	<version>${version}</version>

	<dependencies>
    <#list dependencies as dependency>
	    <#if dependency.artifact??>
		    <dependency>
			    <#if dependency.warnings?size gt 0>
				    <!--
				        Warnings:
						<#list dependency.warnings as warning>
						    ${warning}
						</#list>
				    -->
			    </#if>
			    <groupId>${dependency.artifact.group}</groupId>
			    <artifactId>${dependency.artifact.artifact}</artifactId>
			    <version>${dependency.artifact.version}</version>
		    </dependency>
		<#else>
		    <!--
		        TODO: Missing dependency for ${dependency.file.name}
				<#list dependency.errors as error>
				    ${error}
				</#list>
		    -->
	    </#if>
    </#list>
	</dependencies>


</project>