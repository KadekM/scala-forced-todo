package forcedtodo.plugin

import java.time.LocalDate
import java.time.format.DateTimeParseException

import scala.tools.nsc.plugins.PluginComponent
import scala.tools.nsc.{Global, Phase}

class PluginPhase(val global: Global, todoReport: Seq[String] ⇒ Unit)
    extends PluginComponent {
  override val phaseName: String = "forcedtodo"
  override val runsAfter = List("typer")
  override val runsBefore = List("patmat")

  override def newPhase(prev: Phase): Phase = {
    new TodoExtractPhase(global, prev, todoReport)
  }
}

class TodoExtractPhase(global: Global, prev: Phase, todoReport: Seq[String] ⇒ Unit) extends Phase(prev) {
  override def name: String = "forcedtodo"
  val now = LocalDate.now

  private def units = global
    .currentRun
    .units
    .toSeq
    .sortBy(_.source.content.mkString.hashCode)

  override def run(): Unit = {
    val todos = for {
      unit ← units
      todo ← TodoExtraction(global)(unit)
    } yield todo

    val problems = todos
      .map { case (tree, anno) ⇒ (tree, anno, getTodoInfo(anno)) }
      .filter {
        case x @ (_, _, info) ⇒
          info.isInstanceOf[DateParsingFailed] || info == TodoIsOverdue
      }

    if (problems.nonEmpty) {
      if (problems.length == 1) global.error("1 TODO has problem!")
      else global.error(problems.length + " TODOs have problems!")

      problems.foreach {
        case (tree, anno, TodoIsOverdue) ⇒
          global.inform("Is overdue date at: " + tree.pos.source.path)
          global.inform(tree.pos.source.lineToString(anno.pos.line - 1))
          global.inform(tree.pos.source.lineToString(anno.pos.line))

          todoReport(problems.map(_._1.pos.source.file.name))

        case (tree, anno, DateParsingFailed(date)) ⇒
          global.inform(s"`$date` is date in invalid format at: " + tree.pos.source.path)
          global.inform(tree.pos.source.lineToString(anno.pos.line - 1))
          global.inform(tree.pos.source.lineToString(anno.pos.line))

          todoReport(problems.map(_._1.pos.source.file.name))

        case _ ⇒
      }
    }
  }

  private def getTodoInfo(anno: Global#Annotation): TodoInfo = {
    val untilArgument = anno.stringArg(1)
    untilArgument match {
      case Some(t) ⇒
        try {
          val dt = LocalDate.parse(t)
          if (dt.isBefore(now)) TodoIsOverdue
          else TodoHasTime

        } catch {
          case e: DateTimeParseException ⇒
            DateParsingFailed(t.toString)
        }

      case None ⇒ TodoHasNoRestriction
    }
  }
}

sealed trait TodoInfo

case object TodoHasNoRestriction extends TodoInfo
case object TodoHasTime extends TodoInfo

final case class DateParsingFailed(date: String) extends TodoInfo
case object TodoIsOverdue extends TodoInfo
