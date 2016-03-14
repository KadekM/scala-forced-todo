package forcedtodo.plugin
import tools.nsc.Global

class RuntimePlugin(global: Global) extends TestPlugin(global)
class TestPlugin(val global: Global, todoReport: Seq[String] => Unit = _ => ()) extends tools.nsc.plugins.Plugin {

  val name = "Forcedtodo"
  val description = "Allows developer to specify checked TODOs"

  val components = List[tools.nsc.plugins.PluginComponent](
    new PluginPhase(this.global, todoReport)
  )
}
