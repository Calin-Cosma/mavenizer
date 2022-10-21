import com.calincosma.jargs.Parser
import com.calincosma.mavenizer.Args
import com.calincosma.mavenizer.NexusService
import com.calincosma.mavenizer.model.Artifact

suspend fun main(args: Array<String>) {

	val params = Parser.getInstance().parse(args, Args::class.java)
	println("Reading folder: ${params.folder.absolutePath}")

	val nexusService = NexusService()

	params.folder.listFiles { file -> file.absolutePath.lowercase().endsWith(".jar") }
		?.forEach {
			println(it.absolutePath)
			val artifact : Artifact? = nexusService.findMavenArtifact(it.absolutePath)
			println(if (artifact != null ) "Found artifact: ${artifact.group}:${artifact.name}:${artifact.version}" else "Artifact not found")
		}

}

