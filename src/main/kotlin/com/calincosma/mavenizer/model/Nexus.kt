package com.calincosma.mavenizer.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class NexusResponse(
	var responseHeader: ResponseHeader,
	var response: Response
)


@Serializable
data class ResponseHeader(val status: Int)


@Serializable
data class Response(
	val numFound: Int,
	@SerialName("docs") val artifacts: List<NexusArtifact>
)

@Serializable
data class NexusArtifact(
	val id: String,
	@SerialName("g") val group: String,
	@SerialName("a") val artifact: String,
	@SerialName("v") val version: String,
	@SerialName("p") val packaging: String,
	val timestamp: Long,
)