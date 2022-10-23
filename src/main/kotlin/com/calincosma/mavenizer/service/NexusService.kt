package com.calincosma.mavenizer.service

import com.calincosma.mavenizer.model.NexusArtifact
import com.calincosma.mavenizer.model.NexusResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory

class NexusService {

	private val NEXUS_URL_SEARCH = "https://search.maven.org/solrsearch/select"

	val LOGGER = LoggerFactory.getLogger(NexusService::class.java)




	private suspend fun nexusSearchBySha1(sha1: String) : NexusResponse = nexusSearch("1", sha1)

	private suspend fun nexusSearchByClassName(className: String) : NexusResponse = nexusSearch("fc", className)

	private suspend fun nexusSearch(paramPrefix: String, param: String): NexusResponse {

		val client = HttpClient(CIO) {
			install(ContentNegotiation) {
				json(Json {
					prettyPrint = true
					isLenient = true
					ignoreUnknownKeys = true
				})
			}
			install(Logging) {
				logger = Logger.DEFAULT
				level = LogLevel.ALL
			}
		}
		val response: HttpResponse = client.get {
			url(NEXUS_URL_SEARCH)
			parameter("q", "$paramPrefix:$param")
			parameter("rows", "50")
			parameter("wt", "json")
		}
		if (response.status.isSuccess())
			return response.body()


		LOGGER.error(response.bodyAsText())
		throw RuntimeException("Nexus search failed. HTTP error code: ${response.status}")
	}


	suspend fun findNexusArtifacts(sha1: String): List<NexusArtifact> {
		LOGGER.debug("Searching Maven Central by SHA1 for $sha1")
		try {
			val nexusResponse: NexusResponse = nexusSearchBySha1(sha1)
			if (nexusResponse.responseHeader.status != 0) throw RuntimeException("SHA1 checksum search failed with status: ${nexusResponse.responseHeader.status}")
			if (nexusResponse.response.numFound > 0) {
				LOGGER.debug("Found Maven artifact ${nexusResponse.response.artifacts.get(0)} for $sha1")
				return nexusResponse.response.artifacts
			}
			LOGGER.warn("No Maven artifact found for $sha1")
		} catch (e: Exception) {
			LOGGER.error("Error while searching Maven artifact for $sha1", e)
		}

		return listOf()
	}


	suspend fun findByClass(className: String): List<NexusArtifact?>? {
		LOGGER.debug("Searching Maven Central by class name for $className")
		return try {
			val nexusResponse: NexusResponse = nexusSearchByClassName(className)
			if (nexusResponse.responseHeader.status != 0) throw RuntimeException("Class name search failed with status: " + nexusResponse.responseHeader.status)
			LOGGER.info("Found Maven " + nexusResponse.response.numFound + " artifacts for " + className)
			nexusResponse.response.artifacts
		} catch (e: java.lang.Exception) {
			LOGGER.error("Error while searching Maven artifact for $className", e)
			null
		}
	}
}