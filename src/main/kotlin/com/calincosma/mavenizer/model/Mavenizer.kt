package com.calincosma.mavenizer.model

import java.io.File

data class Artifact(
	val group: String,
	val artifact: String,
	val version: String,
) {

	companion object {
		fun NexusArtifact.toArtifact() : Artifact = Artifact(
			group,
			artifact,
			version,
		)
	}
}

data class Jar (
	val file: File,
	var sha1: String? = null,
	var nexusArtifacts: List<NexusArtifact> = listOf(),
	var artifact: Artifact? = null,
	var classes : Set<Claxx>? = null,
	var packages: Set<String> = HashSet(),
	var dependencies: Set<Jar> = HashSet(),
	var reverseDependencies: Set<Jar> = HashSet(),
	var missingClassDependencies: Set<Claxx> = HashSet(),
	var classDependencies: Set<Claxx> = HashSet(),
	var warnings: MutableList<String> = mutableListOf(),
	var errors: MutableList<String> = mutableListOf(),
) {
	val path: String get() = file.absolutePath
}