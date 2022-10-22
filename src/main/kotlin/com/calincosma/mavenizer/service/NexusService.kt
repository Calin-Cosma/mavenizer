package com.calincosma.mavenizer.service

import com.calincosma.mavenizer.model.Artifact
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
import jakarta.xml.bind.annotation.adapters.HexBinaryAdapter
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

class NexusService {

	private val NEXUS_URL_SEARCH = "https://search.maven.org/solrsearch/select"

	val LOGGER = LoggerFactory.getLogger(NexusService::class.java)

	@Throws(IOException::class, NoSuchAlgorithmException::class)
	fun calcSHA1(file: File): String {
		val sha1 = MessageDigest.getInstance("SHA-1")
		FileInputStream(file).use { input ->
			val buffer = ByteArray(8192)
			var len = input.read(buffer)
			while (len != -1) {
				sha1.update(buffer, 0, len)
				len = input.read(buffer)
			}
			return HexBinaryAdapter().marshal(sha1.digest())
		}
	}


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
		throw RuntimeException("Failed. HTTP error code: " + response.status)
	}


	suspend fun findMavenArtifact(path: String): Artifact? {
		LOGGER.debug("Searching Maven Central by SHA1 for $path")
		return try {
			val sha1 = calcSHA1(File(path))
			val nexusResponse: NexusResponse = nexusSearchBySha1(sha1)
			if (nexusResponse.responseHeader.status != 0) throw RuntimeException("SHA1 checksum search failed with status: ${nexusResponse.responseHeader.status}")
			if (nexusResponse.response.numFound > 1) throw RuntimeException("SHA1 checksum search returned too many results")
			if (nexusResponse.response.numFound == 0) {
				LOGGER.info("No Maven artifact found for $path")
				return null
			}
			LOGGER.info("Found Maven artifact for $path; ${nexusResponse.response.artifacts.get(0)}")
			nexusResponse.response.artifacts.get(0)
		} catch (e: Exception) {
			LOGGER.error("Error while searching Maven artifact for $path", e)
			null
		}
	}


	suspend fun findByClass(className: String): List<Artifact?>? {
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