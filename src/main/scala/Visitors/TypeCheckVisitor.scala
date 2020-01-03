package Visitors

import Analysis.{MyType, NodeVisitor}
import Node._
import Analysis.MyType._
import Enviroment.SymbolCategory._
import Enviroment.{Mapping, SymbolTable, TypeCheckingException}
import Parser.sym

import scala.collection.mutable.LinkedHashSet
import collection.JavaConverters._

class TypeCheckVisitor extends NodeVisitor {
  val rootTable: SymbolTable = SymbolTable(None)

  private val decls = LinkedHashSet.empty[String]
  private var declFlag: Boolean = true

  private var currentFunctionType: Option[MyType] = None
  private var currentFunctionEnv: SymbolTable = rootTable

  override def postVisit(node: Expression.Logical): Unit = {
    val lhsType = node.getLhs.getAttachedType
    val rhsType = node.getRhs.getAttachedType

    (lhsType, rhsType) match {
      case (_:Bool_T, _:Bool_T) => node.setAttachedType(new Bool_T)
      case _                    => throw TypeCheckingException(s"Error: Both sides in ${node.toString} must be of type Boolean.")
    }
  }

  override def postVisit(node: Expression.Equality): Unit = {
    val lhs = node.getLhs.getAttachedType
    val rhs = node.getRhs.getAttachedType

    if (lhs.getClass == rhs.getClass) {
      node.setAttachedType(new Bool_T())
    }
    else {
      (lhs, rhs) match {
        case (_:Int_T, _:Char_T) | (_:Char_T, _:Int_T) => node.setAttachedType(new Bool_T)
        case _                                         => throw TypeCheckingException(s"Error: Non comparable types in ${node.toString}.")
      }
    }
  }

  override def postVisit(node: Expression.Comparison): Unit = {
    val lhs = node.getLhs.getAttachedType
    val rhs = node.getRhs.getAttachedType

    (lhs, rhs) match {
      case (_:Char_T, _:Char_T) | (_:Int_T, _:Int_T) => node.setAttachedType(new Bool_T)
      case (_:Int_T, _:Char_T) | (_:Char_T, _:Int_T) => node.setAttachedType(new Bool_T)
      case _                                         => throw TypeCheckingException(s"Error: Non comparable types in ${node.toString}.")
    }
  }

  override def postVisit(node: Expression.Arithmetic): Unit = {
    val lhs = node.getLhs.getAttachedType
    val rhs = node.getRhs.getAttachedType

    (lhs, rhs) match {
      case (_:Char_T, _:Char_T) | (_:Int_T, _:Int_T) => node.setAttachedType(new Int_T)
      case (_:Int_T, _:Char_T) | (_:Char_T, _:Int_T) => node.setAttachedType(new Int_T)
      case _                                         => throw TypeCheckingException(s"Error: Both sides in ${node.toString} must be of either type Int or Char.")
    }
  }

  override def postVisit(node: Expression.ArrayAccess): Unit = {
    val index = node.getIdx.getAttachedType

    if (!index.isInstanceOf[Int_T] && !index.isInstanceOf[Char_T]) {
      throw TypeCheckingException(s"Error: ${index.toString} can't be used to index in ${node.toString}.")
    }
    else {
      node.getName.getAttachedType match {
        case _: CharStackArray_T | _: CharHeapArray_T => node.setAttachedType(new Char_T)
        case _: IntStackArray_T | _: IntHeapArray_T   => node.setAttachedType(new Int_T)
        case _: StringArray_T                         => node.setAttachedType(new CharHeapArray_T)
        case _                                        => throw TypeCheckingException(s"Error: Identifier ${node.getName.toString} in ${node.toString} is not an array type.")
      }
    }
  }

  override def postVisit(node: Expression.Call): Unit = {
    val mapping = currentFunctionEnv.lookupMappingInParent(node.getName)

    if (node.getName == "main") {
      throw TypeCheckingException("Error: Can not call the main function directly in code.")
    }

    if (!mapping.isDefined) {
      throw TypeCheckingException(s"Error: Undeclared identifier ${node.getName} in ${node.toString}.")
    }
    else if (currentFunctionEnv.lookupCategoryInParent(node.getName) == FUNCTION) {
      val expected = mapping.get.enviroment.get.values(PARAMETER)
      val actual = node.getArgs.asScala.toList

      checkArgumentsMatch(expected, actual, node.toString);

      node.setAttachedType(mapping.get.typeof);
    }
    else {
      throw TypeCheckingException(s"Error: Identifier ${node.getName} in ${node.toString} doesn't map to a function.")
    }
  }

  override def postVisit(node: Expression.IntLiteral): Unit = {
    node.setAttachedType(new Int_T)
  }

  override def postVisit(node: Expression.CharLiteral): Unit = {
    node.setAttachedType(new Char_T)
  }

  override def postVisit(node: Expression.BoolLiteral): Unit = {
    node.setAttachedType(new Bool_T)
  }

  override def postVisit(node: Expression.Identifier): Unit = {
    val mapping = currentFunctionEnv.lookupMapping(node.getName)

    if (!mapping.isDefined) {
      throw TypeCheckingException(s"Error: Undeclared identifier ${node.getName}.")
    }

    node.setAttachedType(mapping.get.typeof)
  }

  override def postVisit(node: Expression.NewArray): Unit = {
    val len = node.getLength.getAttachedType

    if (!len.isInstanceOf[Char_T] && !len.isInstanceOf[Int_T]) {
      throw TypeCheckingException(s"Error: Unable to use ${node.getLength.toString} as array size in ${node.toString}.")
    }

    node.getTypeConst match {
      case sym.INT  => node.setAttachedType(new IntHeapArray_T)
      case sym.CHAR => node.setAttachedType(new CharHeapArray_T)
      case _        => throw TypeCheckingException("Error: Unexpected type constant.")
    }
  }

  override def postVisit(node: Expression.Negated): Unit = {
    if (!node.getExp.getAttachedType.isInstanceOf[Bool_T]) {
      throw TypeCheckingException(s"Error: Can't negate non-boolean expression ${node.toString}.")
    }

    node.setAttachedType(new Bool_T)
  }

  override def postVisit(node: FunctionBody): Unit = {
    val mapping = currentFunctionType.get
    val ret = node.getRet.getAttachedType

    if (mapping.getClass != ret.getClass) {
      throw TypeCheckingException(s"Error: Return expression ${node.getRet.toString} doesn't match the expected return type.")
    }

    for (stm <- node.getStms.asScala.toList) {
      if (!stm.getAttachedType.isInstanceOf[Unit_T]) {
        throw TypeCheckingException(s"Error: Statement ${stm.toString} is not of type Unit.")
      }
    }

    node.setAttachedType(new Unit_T)
  }

  override def postVisit(node: FunctionDeclaration): Unit = {
    if (!node.getSignature.getAttachedType.isInstanceOf[Unit_T]) {
      throw TypeCheckingException(s"Error: Function body for ${node.getSignature.toString} is not of type Unit.")
    }

    node.setAttachedType(new Unit_T)
  }

  override def postVisit(node: FunctionDefinition): Unit = {
    if (!node.getSignature.getAttachedType.isInstanceOf[Unit_T]) {
      throw TypeCheckingException(s"Error: Function signature ${node.getSignature.toString} is not of type Unit.")
    }

    if (!node.getBody.getAttachedType.isInstanceOf[Unit_T]) {
      throw TypeCheckingException(s"Error: Function body for ${node.getSignature.toString} is not of type Unit.")
    }

    currentFunctionType = None
    currentFunctionEnv = currentFunctionEnv.parent.get
    node.setAttachedType(new Unit_T)
  }

  override def postVisit(node: FunctionSignature): Unit = {
    if (declFlag) {
      handleDeclarationSignature(node)
    }
    else {
      handleDefinitionSignature(node)
    }

    node.setAttachedType(new Unit_T)
  }

  def handleDeclarationSignature(node: FunctionSignature) = {
    currentFunctionEnv.add(node.getName, node.getTypeLabel.getAttachedType, FUNCTION)
    currentFunctionEnv = rootTable.lookupMapping(node.getName).get.enviroment.get

    val args = node.getArgs.asScala.toList
    for (arg <- args) {
      currentFunctionEnv.add(arg.getName, arg.getAttachedType, PARAMETER)
    }

    currentFunctionEnv = currentFunctionEnv.parent.get
    decls.add(node.getName)
  }

  def handleDefinitionSignature(node: FunctionSignature) = {
    val mapping = currentFunctionEnv.lookupMapping(node.getName)

    if (mapping.isDefined) {
      val expected = mapping.get.enviroment.get.values(PARAMETER)
      val actual = node.getArgs.asScala.toList

      checkArgumentsMatch(expected, actual, node.toString)

      currentFunctionType = Some(mapping.get.typeof)
      currentFunctionEnv = mapping.get.enviroment.get
      decls.remove(node.getName)
    }
    else {
      throw TypeCheckingException(s"Error: ${node.toString} doesn't have a corresponding function declaration.")
    }
  }

  override def preVisit(node: MainFunction): Boolean = {
    declFlag = false

    currentFunctionEnv.add("main", new Int_T, FUNCTION)

    currentFunctionEnv = rootTable.lookupMapping("main").get.enviroment.get
    currentFunctionType = Some(new Int_T)

    currentFunctionEnv.add(node.getArgc, new Int_T, PARAMETER)
    currentFunctionEnv.add(node.getArgv, new StringArray_T, PARAMETER)

    true
  }

  override def postVisit(node: MainFunction): Unit = {
    if (!node.getBody.getAttachedType.isInstanceOf[Unit_T]) {
      throw TypeCheckingException(s"Error: Function body for ${node.toString} is not of type Unit.")
    }

    currentFunctionType = None
    currentFunctionEnv = currentFunctionEnv.parent.get

    node.setAttachedType(new Unit_T)
  }

  override def postVisit(node: Parameter): Unit = {
    node.setAttachedType(node.getTypeLabel.getAttachedType)
  }

  override def postVisit(node: Program): Unit = {
    if (!decls.isEmpty) {
      throw TypeCheckingException(s"Error: No function definition provided for ${decls.iterator.next().toString}")
    }

    for (decl <- node.getDeclarations.asScala.toList) {
      if (!decl.getAttachedType.isInstanceOf[Unit_T]) {
        throw TypeCheckingException(s"Error: Declaration ${decl.toString} is not of type Unit.")
      }
    }

    if (!node.getMainFunction.getAttachedType.isInstanceOf[Unit_T]) {
      throw TypeCheckingException(s"Error: Main function is not of type Unit")
    }

    for (definition <- node.getDeclarations.asScala.toList) {
      if (!definition.getAttachedType.isInstanceOf[Unit_T]) {
        throw TypeCheckingException(s"Error: Definition for ${definition.getSignature.toString} is not of type Unit.")
      }
    }

  }

  override def postVisit(node: Statement.Compound): Unit = {
    val stms = node.getStms.asScala.toList

    for (stm <- stms) {
      if (!stm.getAttachedType.isInstanceOf[Unit_T]) {
        throw TypeCheckingException(s"Error: Statement ${stm.toString} is not of type Unit.")
      }
    }

    node.setAttachedType(new Unit_T)
  }

  override def postVisit(node: Statement.IfThenElse): Unit = {
    if (!node.getCond.getAttachedType.isInstanceOf[Bool_T]) {
      throw TypeCheckingException(s"Error: If condition ${node.getCond.toString} is not of type Bool.")
    }
    else if (!node.getThen.getAttachedType.isInstanceOf[Unit_T]) {
      throw TypeCheckingException(s"Error: Then statement ${node.getThen.toString} is not of type Unit.")
    }
    else if (!node.getElse.getAttachedType.isInstanceOf[Unit_T]) {
      throw TypeCheckingException(s"Error: Else statement ${node.getElse.toString} is not of type Unit.")
    }

    node.setAttachedType(new Unit_T)
  }

  override def postVisit(node: Statement.While): Unit = {
    if (!node.getCond.getAttachedType.isInstanceOf[Bool_T]) {
      throw TypeCheckingException(s"Error: While condition ${node.getCond.toString} is not of type Bool.")
    }
    else if (!node.getBody.getAttachedType.isInstanceOf[Unit_T]) {
      throw TypeCheckingException(s"Error: While body ${node.getBody.toString} is not of type Unit.")
    }

    node.setAttachedType(new Unit_T)
  }

  override def postVisit(node: Statement.Assign): Unit = {
    val mapping = currentFunctionEnv.lookupMapping(node.getName)
    val assignment = node.getVal.getAttachedType

    if (!mapping.isDefined) {
      throw TypeCheckingException(s"Error: Undeclared identifier ${node.getName}.")
    }

    if (mapping.get.typeof.isInstanceOf[CharStackArray_T] || mapping.get.typeof.isInstanceOf[IntStackArray_T]) {
      throw TypeCheckingException(s"Error: Can't reassign stack array ${node.getName}.")
    }

    if (mapping.get.typeof.getClass != node.getVal.getAttachedType.getClass) {
      (mapping.get.typeof, assignment) match {
        case (_: Int_T, _: Char_T) | (_: Char_T, _: Int_T) => node.setAttachedType(new Unit_T)
        case _                                             => throw TypeCheckingException(s"Error: Unable to assign ${node.getVal.toString} to ${node.getName} in ${node.toString}.")
      }
    }

    node.setAttachedType(new Unit_T)
  }

  override def postVisit(node: Statement.ArrayAssign): Unit = {
    val index = node.getIdx.getAttachedType

    if (!index.isInstanceOf[Int_T] && !index.isInstanceOf[Char_T]) {
      throw TypeCheckingException(s"Error: ${index.toString} can't be used to index in ${node.toString}.")
    }

    val mapping = currentFunctionEnv.lookupMapping(node.getName)
    val assignment = node.getVal.getAttachedType

    if (!mapping.isDefined) {
      throw TypeCheckingException(s"Error: Undeclared identifier ${node.getName}.")
    }

    (mapping.get.typeof, assignment) match {
      case (_: CharStackArray_T, _: Char_T) | (_: CharHeapArray_T, _: Char_T) => node.setAttachedType(new Unit_T)
      case (_: CharStackArray_T, _: Int_T) | (_: CharHeapArray_T, _: Int_T)   => node.setAttachedType(new Unit_T)
      case (_: IntStackArray_T, _: Int_T) | (_: IntHeapArray_T, _: Int_T)     => node.setAttachedType(new Unit_T)
      case (_: IntStackArray_T, _: Char_T) | (_: IntHeapArray_T, _: Char_T)   => node.setAttachedType(new Unit_T)
      case (_: StringArray_T, _: CharHeapArray_T)                             => node.setAttachedType(new Unit_T)
      case _                                                                  => throw TypeCheckingException(s"Error: Types do not match in assignment ${node.toString}.")
    }
  }

  override def postVisit(node: Statement.Free): Unit = {
    val mapping = currentFunctionEnv.lookupMapping(node.getName)

    if (!mapping.isDefined) {
      throw TypeCheckingException(s"Error: Undeclared identifier ${node.getName}.")
    }

    mapping.get.typeof match {
      case _: IntHeapArray_T | _: CharHeapArray_T => node.setAttachedType(new Unit_T)
      case _                                      => throw TypeCheckingException(s"Error: Can't free non-heap array variable ${node.getName}.")
    }
  }

  override def postVisit(node: Statement.Print): Unit = {
    node.setAttachedType(new Unit_T)
  }

  override def postVisit(node: TypeLabel.Primitive): Unit = {
    node.getTypeConst match {
      case sym.INT  => node.setAttachedType(new Int_T)
      case sym.CHAR => node.setAttachedType(new Char_T)
      case sym.BOOL => node.setAttachedType(new Bool_T)
      case _        => throw TypeCheckingException("Error: Unexpected type constant.")
    }
  }

  override def postVisit(node: TypeLabel.StackArray): Unit = {
    val size = node.getSize

    node.getTypeConst match {
      case sym.INT  => node.setAttachedType(new IntStackArray_T(size))
      case sym.CHAR => node.setAttachedType(new CharStackArray_T(size))
      case _        => throw TypeCheckingException("Error: Unexpected type constant.")
    }
  }

  override def postVisit(node: TypeLabel.HeapArray): Unit = {
    node.getTypeConst match {
      case sym.INT  => node.setAttachedType(new IntHeapArray_T)
      case sym.CHAR => node.setAttachedType(new CharHeapArray_T)
      case _        => throw TypeCheckingException("Error: Unexpected type constant.")
    }
  }

  override def postVisit(node: VariableDeclaration): Unit = {
    currentFunctionEnv.add(node.getName, node.getTypeLabel.getAttachedType, LOCAL)
  }

  private def checkArgumentsMatch(expected: List[Mapping], actual: List[Node], errorLine: String): Unit = {
    if (actual.length != expected.length) {
      throw TypeCheckingException(s"Error: Incorrect number of arguments provided in $errorLine.")
    }

    for (i <- 0 to actual.length - 1) {
      // Stack arrays can be passed to other functions as the pointer will just be copied, will lose size info though
      if ((expected(i).typeof.getClass != actual(i).getAttachedType.getClass) &&
          !(expected(i).typeof.isInstanceOf[IntHeapArray_T] && actual(i).getAttachedType.isInstanceOf[IntStackArray_T]) &&
          !(expected(i).typeof.isInstanceOf[CharHeapArray_T] && actual(i).getAttachedType.isInstanceOf[CharStackArray_T])) {
        throw TypeCheckingException(s"Error: Actual argument types don't match expected ones in $errorLine.")
      }
    }
  }
}
