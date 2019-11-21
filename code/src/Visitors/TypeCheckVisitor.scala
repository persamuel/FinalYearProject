package Visitors

import Analysis.{MyType, NodeVisitor}
import Node._
import Analysis.MyType._
import Enviroment.SymbolCategory._
import Enviroment.{RedefinitionException, SymbolTable, TypeCheckingException}
import Parser.sym

import collection.JavaConverters._

class TypeCheckVisitor extends NodeVisitor {
  private val rootTable: SymbolTable = SymbolTable(None)

  private var declFlag: Boolean = true

  private var currentFunction: Option[MyType] = None
  private var currentTable: SymbolTable = rootTable

  override def preVisit(node: Expression.Logical): Boolean = {
    true
  }

  override def postVisit(node: Expression.Logical): Unit = {
    val lhs = node.getLhs.getAttachedType
    val rhs = node.getRhs.getAttachedType

    (lhs, rhs) match {
      case (_:Bool_T, _:Bool_T) => node.setAttachedType(new Bool_T)
      case _                    => throw TypeCheckingException("Error: Logical operator can't be applied to " + lhs + " and " + rhs + ".")
    }
  }

  override def preVisit(node: Expression.Equality): Boolean = {
    true
  }

  override def postVisit(node: Expression.Equality): Unit = {
    val lhs = node.getLhs.getAttachedType
    val rhs = node.getRhs.getAttachedType

    if (lhs.getClass == rhs.getClass) {
      node.setAttachedType(new Bool_T())
    } else {
      (lhs, rhs) match {
        case (_:Int_T, _:Char_T) | (_:Char_T, _:Int_T) => node.setAttachedType(new Bool_T)
        case _                                         => throw TypeCheckingException("Error: Equality operator can't be applied to " + lhs + " and " + rhs + ".")
      }
    }
  }

  override def preVisit(node: Expression.Comparison): Boolean = {
    true
  }

  override def postVisit(node: Expression.Comparison): Unit = {
    val lhs = node.getLhs.getAttachedType
    val rhs = node.getRhs.getAttachedType

    (lhs, rhs) match {
      case (_:Char_T, _:Char_T) | (_:Int_T, _:Int_T) => node.setAttachedType(new Bool_T)
      case (_:Int_T, _:Char_T) | (_:Char_T, _:Int_T) => node.setAttachedType(new Bool_T)
      case _                                         => throw TypeCheckingException("Error: Comparison operator can't be applied to " + lhs + " and " + rhs + ".")
    }
  }

  override def preVisit(node: Expression.Arithmetic): Boolean = {
    true
  }

  override def postVisit(node: Expression.Arithmetic): Unit = {
    val lhs = node.getLhs.getAttachedType
    val rhs = node.getRhs.getAttachedType

    (lhs, rhs) match {
      case (_:Char_T, _:Char_T) | (_:Int_T, _:Int_T) => node.setAttachedType(new Int_T)
      case (_:Int_T, _:Char_T) | (_:Char_T, _:Int_T) => node.setAttachedType(new Int_T)
      case _                                         => throw TypeCheckingException("Error: Arithmetic operator can't be applied to " + lhs + " and " + rhs + ".")
    }
  }

  override def preVisit(node: Expression.ArrayAccess): Boolean = {
    true
  }

  override def postVisit(node: Expression.ArrayAccess): Unit = {
    val index = node.getIdx.getAttachedType

    if (!index.isInstanceOf[Int_T] && !index.isInstanceOf[Char_T]) {
      throw TypeCheckingException("Error: " + index + " can't be used to index into array.")
    } else {
      node.getName.getAttachedType match {
        case _: CharStackArray_T | _: CharHeapArray_T => node.setAttachedType(new Char_T)
        case _: IntStackArray_T | _: IntHeapArray_T   => node.setAttachedType(new Int_T)
        case _: StringArray_T                         => node.setAttachedType(new CharHeapArray_T)
        case _                                        => throw TypeCheckingException("Error: ")
      }
    }
  }

  override def preVisit(node: Expression.Call): Boolean = {
    true
  }

  override def postVisit(node: Expression.Call): Unit = {
    // lookup the call in the parent table
    val mapping = currentTable.lookupMappingInParent(node.getName)

    if (mapping._2.isDefined) {                                      // It is a function, as it has it's own environment
      val expected = mapping._2.get.values(PARAMETER)
      val actual = node.getArgs.asScala.toList

      if (expected.length != actual.length) {
        throw TypeCheckingException("Error: Incorrect number of arguments provided.")
      }

      var i = 0
      while (expected.hasNext) {
        val p1 = expected.next()._1
        val p2 = actual(i).getAttachedType

        if (p1.getClass != p2.getClass) {
          throw TypeCheckingException("Error: Actual argument types don't match expected ones.")
        }

        i += 1
      }

      node.setAttachedType(mapping._1)
    }
  }

  override def preVisit(node: Expression.IntLiteral): Boolean = {
    node.setAttachedType(new Int_T)
    false
  }

  override def postVisit(node: Expression.IntLiteral): Unit = {}

  override def preVisit(node: Expression.CharLiteral): Boolean = {
    node.setAttachedType(new Char_T)
    false
  }

  override def postVisit(node: Expression.CharLiteral): Unit = {}

  override def preVisit(node: Expression.BoolLiteral): Boolean = {
    node.setAttachedType(new Bool_T)
    false
  }

  override def postVisit(node: Expression.BoolLiteral): Unit = {}

  override def preVisit(node: Expression.Identifier): Boolean = {
    val mapping = currentTable.lookupMapping(node.getName)
    node.setAttachedType(mapping._1)
    false
  }

  override def postVisit(node: Expression.Identifier): Unit = {}

  override def preVisit(node: Expression.NewArray): Boolean = {
    true
  }

  override def postVisit(node: Expression.NewArray): Unit = {
    val len = node.getLength.getAttachedType

    if (!len.isInstanceOf[Char_T] || !len.isInstanceOf[Int_T]) {
      throw TypeCheckingException("Error: " + len + " can't be used as size for an array array.")
    } else {
      node.getTypeConst match {
        case sym.INT  => node.setAttachedType(new IntHeapArray_T)
        case sym.CHAR => node.setAttachedType(new CharHeapArray_T)
        case _        => throw TypeCheckingException("Error: Unexpected type constant.")
      }
    }
  }

  override def preVisit(node: Expression.Negated): Boolean = {
    true
  }

  override def postVisit(node: Expression.Negated): Unit = {
    if (!node.getExp.isInstanceOf[Bool_T]) {
      throw TypeCheckingException("Error: Can't negate " + node.getExp.getAttachedType + ".")
    } else {
      node.setAttachedType(new Bool_T)
    }
  }

  override def preVisit(node: FunctionBody): Boolean = {
    true
  }

  override def postVisit(node: FunctionBody): Unit = {
    val mapping = currentFunction.get
    val ret = node.getRet.getAttachedType

    if (mapping.getClass != ret.getClass) {
      throw TypeCheckingException("Error: Return type " + ret + " doesn't match actual type " + mapping + ".")
    }

    for (stm <- node.getStms.asScala.toList) {
      if (!stm.getAttachedType.isInstanceOf[Unit_T]) {
        throw TypeCheckingException("Error: Statement not of type " + new Unit_T + ".")
      }
    }

    node.setAttachedType(new Unit_T)
  }

  override def preVisit(node: FunctionDeclaration): Boolean = {
    true
  }

  override def postVisit(node: FunctionDeclaration): Unit = {
    if (!node.getSignature.getAttachedType.isInstanceOf[Unit_T]) {
      throw TypeCheckingException("Error: Function signature not of type " + new Unit_T + ".")
    }

    node.setAttachedType(new Unit_T)
  }

  override def preVisit(node: FunctionDefinition): Boolean = {
    true
  }

  override def postVisit(node: FunctionDefinition): Unit = {
    if (!node.getSignature.getAttachedType.isInstanceOf[Unit_T]) {
      throw TypeCheckingException("Error: Function signature not of type " + new Unit_T + ".")
    }

    if (!node.getBody.getAttachedType.isInstanceOf[Unit_T]) {
      throw TypeCheckingException("Error: Function body not of type " + new Unit_T + ".")
    }

    currentFunction = None
    currentTable = currentTable.parent.get
    node.setAttachedType(new Unit_T)
  }

  override def preVisit(node: FunctionSignature): Boolean = {
    true
  }

  override def postVisit(node: FunctionSignature): Unit = {
    if (declFlag) { // You must be visiting a function declaration
      currentTable.add(node.getName, node.getTypeLabel.getAttachedType, FUNCTION)
      currentTable = currentTable.lookupMapping(node.getName)._2.get

      for (arg <- node.getArgs.asScala.toList) {
        currentTable.add(arg.getName, arg.getAttachedType, PARAMETER)
      }

      currentTable = currentTable.parent.get
    } else {
      val mapping = currentTable.lookupMapping(node.getName)
      currentFunction = Some(mapping._1)
      currentTable = mapping._2.get
    }

    node.setAttachedType(new Unit_T)
  }

  override def preVisit(node: MainFunction): Boolean = {
    declFlag = false
    currentTable.add("main", new Int_T, FUNCTION)
    currentTable = currentTable.lookupMapping("main")._2.get
    currentFunction = Some(new Int_T)

    currentTable.add(node.getArgc, new Int_T, PARAMETER)
    currentTable.add(node.getArgv, new StringArray_T, PARAMETER)

    true
  }

  override def postVisit(node: MainFunction): Unit = {
    if (!node.getBody.getAttachedType.isInstanceOf[Unit_T]) {
      throw TypeCheckingException("Error: Function body not of type " + new Unit_T + ".")
    }

    currentFunction = None
    currentTable = currentTable.parent.get
    node.setAttachedType(new Unit_T)
  }

  override def preVisit(node: Parameter): Boolean = {
    true
  }

  override def postVisit(node: Parameter): Unit = {
    node.setAttachedType(node.getTypeLabel.getAttachedType)
  }

  override def preVisit(node: Program): Boolean = {
    true
  }

  override def postVisit(node: Program): Unit = {
    // check that everything is of type unit.
  }

  override def preVisit(node: Statement.Compound): Boolean = {
    true
  }

  override def postVisit(node: Statement.Compound): Unit = {
    val stms = node.getStms.asScala.toList

    for (stm <- stms) {
      if (!stm.getAttachedType.isInstanceOf[Unit_T]) {
        throw TypeCheckingException("Error: Statement not of type " + new Unit_T + ".")
      }
    }

    node.setAttachedType(new Unit_T)
  }

  override def preVisit(node: Statement.IfThenElse): Boolean = {
    true
  }

  override def postVisit(node: Statement.IfThenElse): Unit = {
    if (!node.getCond.getAttachedType.isInstanceOf[Bool_T]) {
      throw TypeCheckingException("Error: If condition must be of type + " + new Bool_T + ".")
    } else if (!node.getThen.getAttachedType.isInstanceOf[Unit_T]) {
      throw TypeCheckingException("Error: Then statement must be of type + " + new Unit_T + ".")
    } else if (!node.getElse.getAttachedType.isInstanceOf[Unit_T]) {
      throw TypeCheckingException("Error: Else statement must be of type + " + new Unit_T + ".")
    }

    node.setAttachedType(new Unit_T)
  }

  override def preVisit(node: Statement.While): Boolean = {
    true
  }

  override def postVisit(node: Statement.While): Unit = {
    if (!node.getCond.getAttachedType.isInstanceOf[Bool_T]) {
      throw TypeCheckingException("Error: While condition must be of type " + new Bool_T + ".")
    } else if (!node.getBody.getAttachedType.isInstanceOf[Unit_T]) {
      throw TypeCheckingException("Error: While body must be of type " + new Unit_T + ".")
    }

    node.setAttachedType(new Unit_T)
  }

  override def preVisit(node: Statement.Assign): Boolean = {
    true
  }

  override def postVisit(node: Statement.Assign): Unit = {
    val mapping = currentTable.lookupMapping(node.getName)._1

    if (mapping.getClass != node.getVal.getAttachedType.getClass) {
      throw TypeCheckingException("Error: Types must match in assignment.")
    } else {
      node.setAttachedType(new Unit_T)
    }
  }

  override def preVisit(node: Statement.ArrayAssign): Boolean = {
    true
  }

  override def postVisit(node: Statement.ArrayAssign): Unit = {
    val index = node.getIdx.getAttachedType

    if (!index.isInstanceOf[Int_T] && !index.isInstanceOf[Char_T]) {
      throw TypeCheckingException("Error: " + index + " can't be used to index into array.")
    } else {
      val mapping = currentTable.lookupMapping(node.getName)._1
      val assignment = node.getVal.getAttachedType

      (mapping, assignment) match {
        case (_: CharStackArray_T, _: Char_T) | (_: CharHeapArray_T, _: Char_T) => node.setAttachedType(new Unit_T)
        case (_: IntStackArray_T, _: Int_T) | (_: IntHeapArray_T, _: Int_T)     => node.setAttachedType(new Unit_T)
        case (_: StringArray_T, _: CharHeapArray_T)                             => node.setAttachedType(new Unit_T)
        case _                                                                  => throw TypeCheckingException("Error: Can't assign a " + assignment + " to array " + node.getName + ".")
      }
    }
  }

  override def preVisit(node: Statement.Free): Boolean = {
    val mapping = currentTable.lookupMapping(node.getName)

    mapping._1 match {
      case _: IntHeapArray_T | _: CharHeapArray_T => node.setAttachedType(new Unit_T)
      case _                                      => throw TypeCheckingException("Error: Can only free heap arrays.")
    }

    false
  }

  override def postVisit(node: Statement.Free): Unit = {}

  override def preVisit(node: Statement.Print): Boolean = {
    true
  }

  override def postVisit(node: Statement.Print): Unit = {
    node.setAttachedType(new Unit_T)
  }

  override def preVisit(node: TypeLabel.Primitive): Boolean = {
    node.getTypeConst match {
      case sym.INT  => node.setAttachedType(new Int_T)
      case sym.CHAR => node.setAttachedType(new Char_T)
      case sym.BOOL => node.setAttachedType(new Bool_T)
      case _        => throw TypeCheckingException("Error: Unexpected type constant.")
    }

    false
  }

  override def postVisit(node: TypeLabel.Primitive): Unit = {}

  override def preVisit(node: TypeLabel.StackArray): Boolean = {
    val size = node.getSize

    node.getTypeConst match {
      case sym.INT  => node.setAttachedType(new IntStackArray_T(size))
      case sym.CHAR => node.setAttachedType(new CharStackArray_T(size))
      case _        => throw TypeCheckingException("Error: Unexpected type constant.")
    }

    false
  }

  override def postVisit(node: TypeLabel.StackArray): Unit = {}

  override def preVisit(node: TypeLabel.HeapArray): Boolean = {
    node.getTypeConst match {
      case sym.INT  => node.setAttachedType(new IntHeapArray_T)
      case sym.CHAR => node.setAttachedType(new CharHeapArray_T)
      case _        => throw TypeCheckingException("Error: Unexpected type constant.")
    }

    false
  }

  override def postVisit(node: TypeLabel.HeapArray): Unit = {}

  override def preVisit(node: VariableDeclaration): Boolean = {
    true
  }

  override def postVisit(node: VariableDeclaration): Unit = {
    currentTable.add(node.getName, node.getTypeLabel.getAttachedType, LOCAL)
  }
}
