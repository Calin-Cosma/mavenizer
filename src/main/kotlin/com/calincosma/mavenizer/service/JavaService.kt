package com.calincosma.mavenizer.service

import com.calincosma.mavenizer.exception.JarProcessingException
import com.calincosma.mavenizer.model.Claxx
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.util.regex.Matcher
import java.util.regex.Pattern


class JavaService {

	private val JAR_PATTERN_CLASS = Pattern.compile("(.*)/(.+?)\\.class")

	private val LOGGER = LoggerFactory.getLogger(JavaService::class.java)

	fun readJarContents(path: String): Set<Claxx> {
		val Claxxes: MutableSet<Claxx> = HashSet<Claxx>()
		try {
			val command = "jar -tf $path"
			LOGGER.info("Analyzing jar $path")
			val processBuilder = ProcessBuilder("/bin/bash", "-c", command)
			val process = processBuilder.start()
			val br = BufferedReader(InputStreamReader(process.inputStream))
			var line: String?
			while (br.readLine().also { line = it } != null) {
				LOGGER.debug(line)
				val matcher: Matcher = JAR_PATTERN_CLASS.matcher(line)
				while (matcher.find()) {
					val packageName = matcher.group(1).replace("/".toRegex(), ".")
					val className = matcher.group(2)
					val Claxx = Claxx(packageName, className)
					Claxxes.add(Claxx)
				}
			}
		} catch (e: IOException) {
			throw JarProcessingException("Error processing jar: $path", e)
		}
		return Claxxes
	}
}


