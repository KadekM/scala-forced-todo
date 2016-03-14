package forcedtodo.plugin

import forcedtodo.todo

import scala.collection.mutable
import scala.tools.nsc.Global

object TodoExtraction {

  def apply(global: Global)(unit: global.CompilationUnit): Seq[(global.Tree, global.AnnotationInfo)] = {
    import global._
    val todoTypeTag = global.typeTag[todo]

    class AnnotationTraverser extends Traverser {
      val todos = mutable.ArrayBuffer.empty[(global.Tree, global.AnnotationInfo)]

      override def traverse(tree: global.Tree): Unit = {
        tree match {
          case t: ValOrDefDef ⇒
            t.symbol.annotations.filter(isTodo).foreach(a ⇒ todos += ((tree, a)))

          case t: ImplDef ⇒
            t.symbol.annotations.filter(isTodo).foreach(a ⇒ todos += ((tree, a)))

          case _ ⇒
        }

        super.traverse(tree)
      }

      def isTodo(anno: AnnotationInfo): Boolean = anno.tpe <:< todoTypeTag.tpe
    }

    val traverser = new AnnotationTraverser()
    traverser.traverse(unit.body)
    traverser.todos.toSeq
  }
}
