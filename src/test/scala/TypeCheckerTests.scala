import java.io.FileReader

import Lexer.Lexer
import Node.Program
import Parser.Parser
import Visitors.TypeCheckVisitor

import org.junit._
import org.junit.Assert.assertEquals

class TypeCheckerTests {

  @Test def `checking bad argv usage #1`: Unit = testForFailure("src/test/resources/TypeCheckerTestPrograms/checkBadArgvUsage1.rope")
  @Test def `checking bad argv usage #2`: Unit = testForFailure("src/test/resources/TypeCheckerTestPrograms/checkBadArgvUsage2.rope")
  @Test def `checking bad argv usage #3`: Unit = testForFailure("src/test/resources/TypeCheckerTestPrograms/checkBadArgvUsage3.rope")
  @Test def `checking bad argv usage #4`: Unit = testForFailure("src/test/resources/TypeCheckerTestPrograms/checkBadArgvUsage4.rope")

  @Test def `checking bad array usage #1`: Unit = testForFailure("src/test/resources/TypeCheckerTestPrograms/checkBadArrayUsage1.rope")
  @Test def `checking bad array usage #2`: Unit = testForFailure("src/test/resources/TypeCheckerTestPrograms/checkBadArrayUsage2.rope")
  @Test def `checking bad array usage #3`: Unit = testForFailure("src/test/resources/TypeCheckerTestPrograms/checkBadArrayUsage3.rope")
  @Test def `checking bad array usage #4`: Unit = testForFailure("src/test/resources/TypeCheckerTestPrograms/checkBadArrayUsage4.rope")

  @Test def `checking bad function call`: Unit = testForFailure("src/test/resources/TypeCheckerTestPrograms/checkBadCall.rope")

  @Test def `checking bad casting`: Unit = testForFailure("src/test/resources/TypeCheckerTestPrograms/checkBadCasts.rope")

  @Test def `checking bad free usage #1`: Unit = testForFailure("src/test/resources/TypeCheckerTestPrograms/checkBadFree1.rope")
  @Test def `checking bad free usage #2`: Unit = testForFailure("src/test/resources/TypeCheckerTestPrograms/checkBadFree2.rope")
  @Test def `checking bad free usage #3`: Unit = testForFailure("src/test/resources/TypeCheckerTestPrograms/checkBadFree3.rope")
  @Test def `checking bad free usage #4`: Unit = testForFailure("src/test/resources/TypeCheckerTestPrograms/checkBadFree4.rope")
  @Test def `checking bad free usage #5`: Unit = testForFailure("src/test/resources/TypeCheckerTestPrograms/checkBadFree5.rope")

  @Test def `checking bad logic #1`: Unit = testForFailure("src/test/resources/TypeCheckerTestPrograms/checkBadLogic1.rope")
  @Test def `checking bad logic #2`: Unit = testForFailure("src/test/resources/TypeCheckerTestPrograms/checkBadLogic2.rope")
  @Test def `checking bad logic #3`: Unit = testForFailure("src/test/resources/TypeCheckerTestPrograms/checkBadLogic3.rope")
  @Test def `checking bad logic #4`: Unit = testForFailure("src/test/resources/TypeCheckerTestPrograms/checkBadLogic4.rope")
  @Test def `checking bad logic #5`: Unit = testForFailure("src/test/resources/TypeCheckerTestPrograms/checkBadLogic5.rope")
  @Test def `checking bad logic #6`: Unit = testForFailure("src/test/resources/TypeCheckerTestPrograms/checkBadLogic6.rope")
  @Test def `checking bad logic #7`: Unit = testForFailure("src/test/resources/TypeCheckerTestPrograms/checkBadLogic7.rope")
  @Test def `checking bad logic #8`: Unit = testForFailure("src/test/resources/TypeCheckerTestPrograms/checkBadLogic8.rope")
  @Test def `checking bad logic #9`: Unit = testForFailure("src/test/resources/TypeCheckerTestPrograms/checkBadLogic9.rope")
  @Test def `checking bad logic #10`: Unit = testForFailure("src/test/resources/TypeCheckerTestPrograms/checkBadLogic10.rope")
  @Test def `checking bad logic #11`: Unit = testForFailure("src/test/resources/TypeCheckerTestPrograms/checkBadLogic11.rope")
  @Test def `checking bad logic #12`: Unit = testForFailure("src/test/resources/TypeCheckerTestPrograms/checkBadLogic12.rope")

  @Test def `checking bad math #1`: Unit = testForFailure("src/test/resources/TypeCheckerTestPrograms/checkBadMath1.rope")
  @Test def `checking bad math #2`: Unit = testForFailure("src/test/resources/TypeCheckerTestPrograms/checkBadMath2.rope")
  @Test def `checking bad math #3`: Unit = testForFailure("src/test/resources/TypeCheckerTestPrograms/checkBadMath3.rope")
  @Test def `checking bad math #4`: Unit = testForFailure("src/test/resources/TypeCheckerTestPrograms/checkBadMath4.rope")
  @Test def `checking bad math #5`: Unit = testForFailure("src/test/resources/TypeCheckerTestPrograms/checkBadMath5.rope")
  @Test def `checking bad math #6`: Unit = testForFailure("src/test/resources/TypeCheckerTestPrograms/checkBadMath6.rope")
  @Test def `checking bad math #7`: Unit = testForFailure("src/test/resources/TypeCheckerTestPrograms/checkBadMath7.rope")

  @Test def `checking ordering of declarations and definitions don't matter`: Unit = testForSuccess("src/test/resources/TypeCheckerTestPrograms/checkDefDeclOrdering.rope")

  @Test def `checking declarations must have corresponding definitions`: Unit = testForFailure("src/test/resources/TypeCheckerTestPrograms/checkDefsAreMatched.rope")

  @Test def `checking declarations must match definitions`: Unit = testForFailure("src/test/resources/TypeCheckerTestPrograms/checkDefsMatchDecls.rope")

  @Test def `checking function arguments are respected`: Unit = testForFailure("src/test/resources/TypeCheckerTestPrograms/checkFunctionArgsRespected.rope")

  @Test def `checking argv access gives char[]`: Unit = testForSuccess("src/test/resources/TypeCheckerTestPrograms/checkGoodArgvUsage.rope")

  @Test def `checking good free usage #1`: Unit = testForSuccess("src/test/resources/TypeCheckerTestPrograms/checkGoodFree1.rope")
  @Test def `checking good free usage #2`: Unit = testForSuccess("src/test/resources/TypeCheckerTestPrograms/checkGoodFree2.rope")

  @Test def `checking good math #1`: Unit = testForSuccess("src/test/resources/TypeCheckerTestPrograms/checkGoodMath1.rope")
  @Test def `checking good math #2`: Unit = testForSuccess("src/test/resources/TypeCheckerTestPrograms/checkGoodMath2.rope")

  @Test def `checking reassign of heap array`: Unit = testForSuccess("src/test/resources/TypeCheckerTestPrograms/checkHeapArrayReassign.rope")

  @Test def `checking parameters correctly preserve type #1`: Unit = testForSuccess("src/test/resources/TypeCheckerTestPrograms/checkParamTypePreserve1.rope")
  @Test def `checking parameters correctly preserve type #2`: Unit = testForSuccess("src/test/resources/TypeCheckerTestPrograms/checkParamTypePreserve2.rope")
  @Test def `checking parameters correctly preserve type #3`: Unit = testForSuccess("src/test/resources/TypeCheckerTestPrograms/checkParamTypePreserve3.rope")
  @Test def `checking parameters correctly preserve type #4`: Unit = testForSuccess("src/test/resources/TypeCheckerTestPrograms/checkParamTypePreserve4.rope")
  @Test def `checking parameters correctly preserve type #5`: Unit = testForSuccess("src/test/resources/TypeCheckerTestPrograms/checkParamTypePreserve5.rope")

  @Test def `checking redefinition violations #1`: Unit = testForFailure("src/test/resources/TypeCheckerTestPrograms/checkRedefinition1.rope")
  @Test def `checking redefinition violations #2`: Unit = testForFailure("src/test/resources/TypeCheckerTestPrograms/checkRedefinition2.rope")
  @Test def `checking redefinition violations #3`: Unit = testForFailure("src/test/resources/TypeCheckerTestPrograms/checkRedefinition3.rope")

  @Test def `checking returns correctly preserve type #1`: Unit = testForSuccess("src/test/resources/TypeCheckerTestPrograms/checkReturnTypePreserve1.rope")
  @Test def `checking returns correctly preserve type #2`: Unit = testForSuccess("src/test/resources/TypeCheckerTestPrograms/checkReturnTypePreserve2.rope")
  @Test def `checking returns correctly preserve type #3`: Unit = testForSuccess("src/test/resources/TypeCheckerTestPrograms/checkReturnTypePreserve3.rope")
  @Test def `checking returns correctly preserve type #4`: Unit = testForSuccess("src/test/resources/TypeCheckerTestPrograms/checkReturnTypePreserve4.rope")
  @Test def `checking returns correctly preserve type #5`: Unit = testForSuccess("src/test/resources/TypeCheckerTestPrograms/checkReturnTypePreserve5.rope")

  @Test def `checking that namespaces are separated`: Unit = testForSuccess("src/test/resources/TypeCheckerTestPrograms/checkSeparationOfNamespaces.rope")

  @Test def `checking that stack arrays can't be formals`: Unit = testForFailure("src/test/resources/TypeCheckerTestPrograms/checkStackArrayArgs.rope")

  @Test def `checking that stack arrays can't be reassigned #1`: Unit = testForFailure("src/test/resources/TypeCheckerTestPrograms/checkStackArrayReassign1.rope")
  @Test def `checking that stack arrays can't be reassigned #2`: Unit = testForFailure("src/test/resources/TypeCheckerTestPrograms/checkStackArrayReassign2.rope")

  @Test def `checking valid cast usage`: Unit = testForSuccess("src/test/resources/TypeCheckerTestPrograms/checkValidCasts.rope")

  private def testForSuccess(filename: String): Unit = {
    try {
      val scanner = new Lexer(new FileReader(filename))
      val parser = new Parser(scanner)
      val ast = parser.parse.value.asInstanceOf[Program]
      val typechecker = new TypeCheckVisitor()

      ast.accept(typechecker)
    } catch {
      case e: Exception => Assert.fail()
    }
  }

  private def testForFailure(filename: String): Unit = {
    val scanner = new Lexer(new FileReader(filename))
    
    try {
      val parser = new Parser(scanner)
      val ast = parser.parse.value.asInstanceOf[Program]
      val typechecker = new TypeCheckVisitor()

      ast.accept(typechecker)
      Assert.fail()
    } catch {
      case e: Exception =>
    }
  }
}
