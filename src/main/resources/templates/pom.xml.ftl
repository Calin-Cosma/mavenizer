<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd"
         xmlns:th="http://www.thymeleaf.org">
	<modelVersion>4.0.0</modelVersion>
	
	<groupId>${group}</groupId>
	<artifactId>${artifact}</artifactId>
	<version>${version}</version>

	<dependencies>
    <#list dependencies as dependency>
	    <dependency>
		    <groupId>${dependency.group}</groupId>
		    <artifactId>${dependency.artifact}</artifactId>
		    <version>${dependency.version}</version>
	    </dependency>
    </#list>
	</dependencies>


</project>