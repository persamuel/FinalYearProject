package Compiler
import java.io.{FileNotFoundException, FileReader, PrintWriter}

import Lexer.Lexer
import Node.Program
import Parser.Parser
import Visitors.{CodegenVisitor, TypeCheckVisitor}
import java_cup.runtime.Scanner

object Compiler {

  private def usage(): Unit = {
    System.out.println("Usage: java -jar jarfilename.jar pathtosrc -o pathtoout")
  }

  def main(args: Array[String]): Unit = {
    if (args.length < 3 || args(1) != "-o") {
      usage()
    }
    else {
      var in: FileReader = null
      var out: PrintWriter = null

      try {
        in = new FileReader(args(0))
        val scanner = new Lexer(in)
        val parser = new Parser(scanner)
        val ast = parser.parse.value.asInstanceOf[Program]
        val typechecker = new TypeCheckVisitor()
        val codegenerator = new CodegenVisitor(typechecker.rootEnv)

        ast.accept(typechecker)
        ast.accept(codegenerator)

        val code = ast.getAttachedAssembly
        out = new PrintWriter(args(2))

        out.write(code)
      } catch {
        case fne: FileNotFoundException => System.err.println(s"Error: Couldn't open file $args(0)")
        case e: Exception => System.err.println(e.getMessage)
      } finally {
        in.close()
        out.close()
      }
    }
  }
}