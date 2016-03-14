package forcedtodo

import utest._
import TestUtils.{ make, makeFail }

object Tests extends TestSuite {

  // Tests could be better, we are just detecing files with problems, which is meh ...

  def tests = TestSuite {
    'fail{
      'basic - makeFail("fail/basic", Seq("Problem1.scala", "Problem2.scala", "Problem3.scala", "Problem4.scala"))
      'mixed - makeFail("fail/mixed", Seq("Problem.scala"))
      'mixed2 - makeFail("fail/mixed2", Seq("Problem.scala"))
      'invalid_date - makeFail("fail/invaliddate", Seq("Problem.scala"))
    }
    'success{
      'all - make("success")
    }
    'self - make("../../main/scala", extraIncludes = Nil)
  }
}

