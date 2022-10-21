<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd"
         xmlns:th="http://www.thymeleaf.org">
	<modelVersion>4.0.0</modelVersion>
	
	<groupId>${artifact.group}</groupId>
	<artifactId>${artifact.name}</artifactId>
	<version>${artifact.version}</version>
	
<#--	<dependencies th:if="${not #lists.isEmpty(dependencies)}">-->
<#--		<dependency th:each="dependency : ${dependencies}">-->
<#--			<groupId th:text="${dependency.group}">dependency.group</groupId>-->
<#--			<artifactId th:text="${dependency.artifact}">dependency.artifact</artifactId>-->
<#--			<version th:text="${dependency.version}">dependency.version</version>-->
<#--		</dependency>-->
<#--	</dependencies>-->


</project>