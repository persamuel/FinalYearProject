package Visitors

import Analysis.NodeVisitor
import Node._

class TypeCheckVisitor extends NodeVisitor {
  override def preVisit(node: Expression.Logical): Boolean = ???

  override def postVisit(node: Expression.Logical): Unit = ???

  override def preVisit(node: Expression.Equality): Boolean = ???

  override def postVisit(node: Expression.Equality): Unit = ???

  override def preVisit(node: Expression.Comparison): Boolean = ???

  override def postVisit(node: Expression.Comparison): Unit = ???

  override def preVisit(node: Expression.Arithmetic): Boolean = ???

  override def postVisit(node: Expression.Arithmetic): Unit = ???

  override def preVisit(node: Expression.ArrayAccess): Boolean = ???

  override def postVisit(node: Expression.ArrayAccess): Unit = ???

  override def preVisit(node: Expression.Call): Boolean = ???

  override def postVisit(node: Expression.Call): Unit = ???

  override def preVisit(node: Expression.IntLiteral): Boolean = ???

  override def postVisit(node: Expression.IntLiteral): Unit = ???

  override def preVisit(node: Expression.CharLiteral): Boolean = ???

  override def postVisit(node: Expression.CharLiteral): Unit = ???

  override def preVisit(node: Expression.BoolLiteral): Boolean = ???

  override def postVisit(node: Expression.BoolLiteral): Unit = ???

  override def preVisit(node: Expression.Identifier): Boolean = ???

  override def postVisit(node: Expression.Identifier): Unit = ???

  override def preVisit(node: Expression.NewArray): Boolean = ???

  override def postVisit(node: Expression.NewArray): Unit = ???

  override def preVisit(node: Expression.Negated): Boolean = ???

  override def postVisit(node: Expression.Negated): Unit = ???

  override def preVisit(node: FunctionBody): Boolean = ???

  override def postVisit(node: FunctionBody): Unit = ???

  override def preVisit(node: FunctionDeclaration): Boolean = ???

  override def postVisit(node: FunctionDeclaration): Unit = ???

  override def preVisit(node: FunctionDefinition): Boolean = ???

  override def postVisit(node: FunctionDefinition): Unit = ???

  override def preVisit(node: FunctionSignature): Boolean = ???

  override def postVisit(node: FunctionSignature): Unit = ???

  override def preVisit(node: MainFunction): Boolean = ???

  override def postVisit(node: MainFunction): Unit = ???

  override def preVisit(node: Parameter): Boolean = ???

  override def postVisit(node: Parameter): Unit = ???

  override def preVisit(node: Program): Boolean = ???

  override def postVisit(node: Program): Unit = ???

  override def preVisit(node: Statement.Compound): Boolean = ???

  override def postVisit(node: Statement.Compound): Unit = ???

  override def preVisit(node: Statement.IfThenElse): Boolean = ???

  override def postVisit(node: Statement.IfThenElse): Unit = ???

  override def preVisit(node: Statement.While): Boolean = ???

  override def postVisit(node: Statement.While): Unit = ???

  override def preVisit(node: Statement.Assign): Boolean = ???

  override def postVisit(node: Statement.Assign): Unit = ???

  override def preVisit(node: Statement.ArrayAssign): Boolean = ???

  override def postVisit(node: Statement.ArrayAssign): Unit = ???

  override def preVisit(node: Statement.Free): Boolean = ???

  override def postVisit(node: Statement.Free): Unit = ???

  override def preVisit(node: TypeLabel.Primitive): Boolean = ???

  override def postVisit(node: TypeLabel.Primitive): Unit = ???

  override def preVisit(node: TypeLabel.StackArray): Boolean = ???

  override def postVisit(node: TypeLabel.StackArray): Unit = ???

  override def preVisit(node: TypeLabel.HeapArray): Boolean = ???

  override def postVisit(node: TypeLabel.HeapArray): Unit = ???

  override def preVisit(node: VariableDeclaration): Boolean = ???

  override def postVisit(node: VariableDeclaration): Unit = ???
}
