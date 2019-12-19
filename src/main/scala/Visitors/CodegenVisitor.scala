package Visitors

import Analysis.MyType.{CharHeapArray_T, CharStackArray_T, IntStackArray_T}
import Codegen.Accumulatorx86CommandBuilder
import Enviroment.SymbolTable
import Node._
import Parser.sym

class CodegenVisitor(private val rootTable: SymbolTable) extends Analysis.NodeVisitor {
  private val currentFunctionEnv = rootTable
  private val builder = new Accumulatorx86CommandBuilder

  override def postVisit(node: Expression.Logical): Unit = ???

  override def postVisit(node: Expression.Equality): Unit = ???

  override def postVisit(node: Expression.Comparison): Unit = ???

  override def postVisit(node: Expression.Arithmetic): Unit = {
    node.getOp match {
      case sym.PLUS => node.setAttachedAssembly(builder.buildPlus())
      case sym.MINUS => node.setAttachedAssembly(builder.buildMinus())
      case sym.MULTI => node.setAttachedAssembly(builder.buildMulti())
    }
  }

  override def postVisit(node: Expression.ArrayAccess): Unit = {
    val offset = currentFunctionEnv.lookupMapping(node.getName.toString).get.offset

    // if the array is on the stack we can just adjust the BP offset
    // if the array is on the heap we have to indirectly access it


    if (node.getName.getAttachedType.isInstanceOf[CharStackArray_T]) {
      // load offset + (index * charsize)(%ebp)
    }
    else if (node.getName.getAttachedType.isInstanceOf[IntStackArray_T]) {
      // load offset + (index * intsize)(%ebp)
    }
    else if (node.getName.getAttachedType.isInstanceOf[CharHeapArray_T]) {
      // load address into acc
      // add to address
      // load that value into the acc
    }
  }

  override def postVisit(node: Expression.Call): Unit = {}

  override def postVisit(node: Expression.IntLiteral): Unit = {
    node.setAttachedAssembly(builder.buildLoadImm(node.getVal.toString))
  }

  override def postVisit(node: Expression.CharLiteral): Unit = {
    node.setAttachedAssembly(builder.buildLoadImm(node.getVal.toInt.toString))
  }

  override def postVisit(node: Expression.BoolLiteral): Unit = {
    if (node.getVal)
      node.setAttachedAssembly(builder.buildLoadImm("1"))
    else
      node.setAttachedAssembly(builder.buildLoadImm("0"))
  }

  override def postVisit(node: Expression.Identifier): Unit = {
    val offset = currentFunctionEnv.lookupMapping(node.getName).get.offset

    node.setAttachedAssembly(builder.buildLoad(s"$offset(%ebp)"))
  }

  override def postVisit(node: Expression.NewArray): Unit = ???

  override def postVisit(node: Expression.Negated): Unit = ???

  override def postVisit(node: FunctionBody): Unit = ???

  override def postVisit(node: FunctionDeclaration): Unit = ???

  override def postVisit(node: FunctionDefinition): Unit = {
    // Make label
    // Save BP
    // reserve space

    // go back up to higher scope
  }

  override def postVisit(node: FunctionSignature): Unit = {
    // go into scope if it is passed the main function
  }


  override def preVisit(node: MainFunction): Boolean = {

    true
  }

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
