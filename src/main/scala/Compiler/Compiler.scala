package Compiler
import java.io.FileReader

import Lexer.Lexer
import Node.Program
import Parser.Parser
import Visitors.{CodegenVisitor, TypeCheckVisitor}
import java_cup.runtime.Scanner

object Compiler extends App {
  val scanner = new Lexer(new FileReader("src/main/resources/Example/Programs/factorial.rop"))

  val parser = new Parser(scanner)

  val ast = parser.parse.value.asInstanceOf[Program]

  val typechecker = new TypeCheckVisitor()
  val codegenerator = new CodegenVisitor(typechecker.rootTable)

  ast.accept(typechecker)
  ast.accept(codegenerator)

  println("Hello")
}