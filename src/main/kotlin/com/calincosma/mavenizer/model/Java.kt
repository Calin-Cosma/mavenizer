package com.calincosma.mavenizer.model

//data class Jar(
//	val path: String,
//	val classes: Map<String, Claxx> = HashMap(),
//	val packages: Set<String> = HashSet(),
//	val dependencies: Set<Jar> = HashSet(),
//	val reverseDependencies: Set<Jar> = HashSet(),
//	val missingClassDependencies: Set<Claxx> = HashSet(),
//	val classDependencies: Set<Claxx> = HashSet(),
//	val nexusArtifact: NexusArtifact,
//)


data class Claxx(
	val packageName: String,
	val className: String,
)