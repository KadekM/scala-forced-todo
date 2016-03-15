package forcedtodo

import java.net.URLClassLoader

import utest._

import scala.reflect.io.VirtualDirectory
import scala.tools.nsc.plugins.Plugin
import scala.tools.nsc.reporters.ConsoleReporter
import scala.tools.nsc.util.ClassPath
import scala.tools.nsc.{Global, Settings}

object TestUtils {
  def getFilePaths(src: String): List[String] = {
    val f = new java.io.File(src)
    if (f.isDirectory) f.list.toList.flatMap(x ⇒ getFilePaths(src + "/" + x))
    else List(src)
  }

  /**
    * Attempts to compile a resource folder as a compilation run, in order
    * to test whether it succeeds or fails correctly.
    */
  def make(path: String, extraIncludes: Seq[String] = Seq("src/main/scala/forcedtodo/package.scala")) = {
    val src = "src/test/resources/" + path
    val sources = getFilePaths(src) ++ extraIncludes

    val vd = new VirtualDirectory("(memory)", None)
    lazy val settings = new Settings
    val loader = getClass.getClassLoader.asInstanceOf[URLClassLoader]
    val entries = loader.getURLs map (_.getPath)
    settings.outputDirs.setSingleOutput(vd)

    // annoyingly, the Scala library is not in our classpath, so we have to add it manually
    val sclpath = entries.map(
      _.replaceAll("scala-compiler.jar", "scala-library.jar"))

    settings.classpath.value = ClassPath.join(entries ++ sclpath: _*)

    var cycles = Option.empty[Seq[String]]

    lazy val compiler = new Global(settings, new ConsoleReporter(settings)) {
      override protected def loadRoughPluginsList(): List[Plugin] = {
        List(new plugin.TestPlugin(this, foundCycles ⇒ cycles = cycles match {
          case None            ⇒ Some(foundCycles)
          case Some(oldCycles) ⇒ Some(oldCycles ++ foundCycles)
        }))
      }
    }
    val run = new compiler.Run()
    run.compile(sources)

    if (vd.toList.isEmpty) throw CompilationException(cycles.get)
  }

  def makeFail(path: String, expected: Seq[String]) = {
    val ex = intercept[CompilationException] { make(path) }
    val problems = ex.problemFiles.toSet
    assert(expected.length == problems.size)
    assert(expected.forall(problems.contains))
  }

  case class CompilationException(problemFiles: Seq[String]) extends Exception

}
