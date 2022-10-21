import com.calincosma.jargs.Parser
import com.calincosma.mavenizer.Args
import com.calincosma.mavenizer.model.Artifact
import com.calincosma.mavenizer.model.ArtifactCandidate
import com.calincosma.mavenizer.service.JavaService
import com.calincosma.mavenizer.service.NexusService
import freemarker.template.Configuration
import freemarker.template.Template
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import java.io.File
import java.io.OutputStreamWriter
import java.io.Writer

var cfg: Configuration = Configuration(Configuration.VERSION_2_3_29)

suspend fun main(args: Array<String>) {

	val params = Parser.getInstance().parse(args, Args::class.java)
	println("Reading folder: ${params.folder.absolutePath}")

	val nexusService = NexusService()
	val javaService = JavaService()


//	cfg.setDirectoryForTemplateLoading(File("templates"))
	cfg.setClassForTemplateLoading(NexusService::class.java, "/templates/")



	val candidates = params.folder.walk()
		.asFlow()
		.filter { it.isFile && it.absolutePath.lowercase().endsWith(".jar") }
		.map{
			println(it.absolutePath)
			val artifact : Artifact? = nexusService.findMavenArtifact(it.absolutePath)

			return@map if (artifact != null) {
				println("Found artifact: ${artifact.group}:${artifact.name}:${artifact.version}")
				ArtifactCandidate(artifact, null, null)
			} else {
				println("Artifact not found")
				val classes = javaService.readJarContents(it.absolutePath)
				ArtifactCandidate(null, it.absolutePath, classes)
			}
		}
		.toList()
		.sortedWith(nullsFirst(compareBy { it.artifact?.name }))

	// TODO treat candidates

//	println(candidates)

	val temp: Template = cfg.getTemplate("pom.xml.ftl")

	candidates.forEach {
		val root: MutableMap<String, Artifact> = HashMap()
		root["artifact"] = it.artifact!!
		val out: Writer = OutputStreamWriter(System.out)
		temp.process(root, out)
	}


}

