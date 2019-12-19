package Compiler
import java.io.FileReader

import Lexer.Lexer
import Node.Program
import Parser.Parser
import Visitors.TypeCheckVisitor
import java_cup.runtime.Scanner

object Compiler extends App {
  val scanner = new Lexer(new FileReader("/home/peter/IdeaProjects/FinalYearProject/src/main/resources/Example/Programs/strcpy.rop"))

  val parser = new Parser(scanner)

  val ast = parser.parse.value.asInstanceOf[Program]

  val visitor = new TypeCheckVisitor()

  ast.accept(visitor)

  println("Hello")
}