import com.calincosma.jargs.Parser
import com.calincosma.mavenizer.Args
import java.io.File
import java.io.FileFilter

fun main(args: Array<String>) {

	val args = Parser.getInstance().parse(args, Args::class.java)
	println("Reading folder: ${args.folder.absolutePath}")

//	args.folder.walk().forEach { println(it.absolutePath) }

	args.folder.listFiles { file -> file.absolutePath.lowercase().endsWith(".jar") }
		.forEach {
			println(it.absolutePath)

		}

//	args.folder.walk()

//	File("/Users/calin/work/clients/dasi/dasi & expert/expert/lib").walk().forEach {
//		println(it)
//	}

}


fun checkFile(file: File) : Boolean {
	println(file.absolutePath)
	println(file.absolutePath.lowercase().endsWith(".jar"))

	return true
}

