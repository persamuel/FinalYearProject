import java.io.FileReader

import Lexer.Lexer
import Node.Program
import Parser.Parser
import Visitors.TypeCheckVisitor
import org.junit._
import org.junit.Assert.assertEquals

class TypeCheckTester {
  @Test def `type checking programs as they're written`: Unit = {
    val scanner = new Lexer(new FileReader("src/test/resources/CodegenTestPrograms/concat.rop"))
    val parser = new Parser(scanner)
    val ast = parser.parse.value.asInstanceOf[Program]
    val typechecker = new TypeCheckVisitor()

    ast.accept(typechecker)

    print("hello")
  }
}
