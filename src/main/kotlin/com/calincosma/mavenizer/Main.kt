import com.calincosma.jargs.Parser
import com.calincosma.mavenizer.Args
import com.calincosma.mavenizer.model.Artifact.Companion.toArtifact
import com.calincosma.mavenizer.model.Jar
import com.calincosma.mavenizer.service.JavaService
import com.calincosma.mavenizer.service.NexusService
import freemarker.template.Configuration
import freemarker.template.Template
import jakarta.xml.bind.annotation.adapters.HexBinaryAdapter
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import java.io.*
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

var cfg: Configuration = Configuration(Configuration.VERSION_2_3_29)

val nexusService = NexusService()
val javaService = JavaService()

suspend fun main(args: Array<String>) {

	val params = Parser.getInstance().parse(args, Args::class.java)
	println("Reading folder: ${params.folder.absolutePath}")

	cfg.setClassForTemplateLoading(NexusService::class.java, "/templates/")

	val jars = params.folder.walk()
		.asFlow()
		.filter { it.isFile && it.absolutePath.lowercase().endsWith(".jar") }
		.map {
			Jar(it)
		}.toList()


	jars.forEach {

		processJar(it)
	}



//	jars.sortedWith(nullsFirst(compareBy { it.nexusArtifact?.artifact }))

	// TODO treat candidates

	writePom(params, jars)


}


private suspend fun processJar(jar: Jar) {
	jar.sha1 = calcSHA1(jar.file)
	jar.nexusArtifacts = nexusService.findNexusArtifacts(jar.sha1!!)

	if (jar.nexusArtifacts.size == 1)
		jar.artifact = jar.nexusArtifacts.first().toArtifact()
	else if (jar.nexusArtifacts.size > 1) {
		jar.artifact = jar.nexusArtifacts.sortedBy { it.timestamp }.first().toArtifact()
		jar.warnings.add("Multiple artifacts found on Nexus, the oldest one was selected")
	} else {
		jar.errors.add("No corresponding artifact found in Nexus")
		jar.classes = javaService.readJarContents(jar.path)
	}
}

private fun writePom(params: Args, jars: List<Jar>) {
	val temp: Template = cfg.getTemplate("pom.xml.ftl")

	val templateParams = mapOf(
		"group" to params.group,
		"artifact" to params.artifact,
		"version" to params.version,
		"dependencies" to jars
	)

	val out: Writer = OutputStreamWriter(System.out)
	temp.process(templateParams, out)
}

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