package Visitors

import Enviroment.SymbolTable
import Node._

class CodegenVisitor(private val rootTable: SymbolTable) extends Analysis.NodeVisitor {
  private val instructions: StringBuilder = new StringBuilder
  private val currentFunctionEnv = rootTable

  override def postVisit(node: Expression.Logical): Unit = ???

  override def postVisit(node: Expression.Equality): Unit = ???

  override def postVisit(node: Expression.Comparison): Unit = ???

  override def postVisit(node: Expression.Arithmetic): Unit = ???

  override def postVisit(node: Expression.ArrayAccess): Unit = ???

  override def postVisit(node: Expression.Call): Unit = ???

  override def postVisit(node: Expression.IntLiteral): Unit = {

  }

  override def postVisit(node: Expression.CharLiteral): Unit = ???

  override def postVisit(node: Expression.BoolLiteral): Unit = ???

  override def postVisit(node: Expression.Identifier): Unit = ???

  override def postVisit(node: Expression.NewArray): Unit = ???

  override def postVisit(node: Expression.Negated): Unit = ???

  override def postVisit(node: FunctionBody): Unit = ???

  override def postVisit(node: FunctionDeclaration): Unit = ???

  override def postVisit(node: FunctionDefinition): Unit = ???

  override def postVisit(node: FunctionSignature): Unit = ???

  override def postVisit(node: MainFunction): Unit = ???

  override def postVisit(node: Parameter): Unit = ???

  override def postVisit(node: Program): Unit = ???

  override def postVisit(node: Statement.Compound): Unit = ???

  override def postVisit(node: Statement.IfThenElse): Unit = ???

  override def postVisit(node: Statement.While): Unit = ???

  override def postVisit(node: Statement.Assign): Unit = ???

  override def postVisit(node: Statement.ArrayAssign): Unit = ???

  override def postVisit(node: Statement.Free): Unit = ???

  override def postVisit(node: Statement.Print): Unit = ???

  override def postVisit(node: TypeLabel.Primitive): Unit = ???

  override def postVisit(node: TypeLabel.StackArray): Unit = ???

  override def postVisit(node: TypeLabel.HeapArray): Unit = ???

  override def postVisit(node: VariableDeclaration): Unit = ???
}
