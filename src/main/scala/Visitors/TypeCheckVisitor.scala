package Visitors

import Analysis._
import Node._
import Analysis.MyType._
import Enviroment.SymbolCategory._
import Enviroment._
import Parser.sym

import scala.collection.mutable.LinkedHashSet
import collection.JavaConverters._

class TypeCheckVisitor extends NodeVisitor {
  val rootEnv: SymbolTable = SymbolTable(new Unit_T, "")
  private var currentEnv: SymbolTable = rootEnv

  private val decls = LinkedHashSet.empty[String]
  private var declFlag: Boolean = true // Marker that indicates whether a signature is part of a declaration or definition in the tree walk

  override def postVisit(node: Expression.Logical): Unit = {
    val lhsType = node.getLhs.getAttachedType
    val rhsType = node.getRhs.getAttachedType

    (lhsType, rhsType) match {
      case (_:Bool_T, _:Bool_T) => node.setAttachedType(new Bool_T)
      case _                    => throw TypeCheckingException(s"Error: Both sides in ${node.toString} must be of type Boolean.")
    }
  }

  override def postVisit(node: Expression.Equality): Unit = {
    val lhsType = node.getLhs.getAttachedType
    val rhsType = node.getRhs.getAttachedType

    if (lhsType.getClass == rhsType.getClass) {
      node.setAttachedType(new Bool_T())
    }
    else {
      (lhsType, rhsType) match {
        case (_:Int_T, _:Char_T) | (_:Char_T, _:Int_T) => node.setAttachedType(new Bool_T)
        case _                                         => throw TypeCheckingException(s"Error: Non comparable types in ${node.toString}.")
      }
    }
  }

  override def postVisit(node: Expression.Comparison): Unit = {
    val lhsType = node.getLhs.getAttachedType
    val rhsType = node.getRhs.getAttachedType

    (lhsType, rhsType) match {
      case (_:Char_T, _:Char_T) | (_:Int_T, _:Int_T) => node.setAttachedType(new Bool_T)
      case (_:Int_T, _:Char_T) | (_:Char_T, _:Int_T) => node.setAttachedType(new Bool_T)
      case _                                         => throw TypeCheckingException(s"Error: Non comparable types in ${node.toString}.")
    }
  }

  override def postVisit(node: Expression.Arithmetic): Unit = {
    val lhsType = node.getLhs.getAttachedType
    val rhsType = node.getRhs.getAttachedType

    (lhsType, rhsType) match {
      case (_:Char_T, _:Char_T) | (_:Int_T, _:Int_T) => node.setAttachedType(new Int_T)
      case (_:Int_T, _:Char_T) | (_:Char_T, _:Int_T) => node.setAttachedType(new Int_T)
      case _                                         => throw TypeCheckingException(s"Error: Both sides in ${node.toString} must be of either type Int or Char.")
    }
  }

  override def postVisit(node: Expression.ArrayAccess): Unit = {
    val indexType = node.getIdx.getAttachedType

    if (!indexType.isInstanceOf[Int_T] && !indexType.isInstanceOf[Char_T])
      throw TypeCheckingException(s"Error: ${indexType.toString} can't be used as an index in ${node.toString}.")

    node.getName.getAttachedType match {
      case _: CharStackArray_T | _: CharHeapArray_T => node.setAttachedType(new Char_T)
      case _: IntStackArray_T | _: IntHeapArray_T   => node.setAttachedType(new Int_T)
      case _: StringArray_T                         => node.setAttachedType(new CharHeapArray_T) // todo: Check usage of StringArray_T
      case _                                        => throw TypeCheckingException(s"Error: Identifier ${node.getName.toString} in ${node.toString} is not an array type.")
    }
  }

  override def postVisit(node: Expression.Call): Unit = {
    val mapping = rootEnv.lookupMapping(node.getName)

    if (node.getName == "main") {
      throw TypeCheckingException("Error: Can not call the main function directly in code.")
    }
    else if (mapping.isEmpty) {
      throw TypeCheckingException(s"Error: Undeclared identifier ${node.getName} in ${node.toString}.")
    }
    else if (rootEnv.lookupCategory(node.getName) == FUNCTION) {
      val env = mapping.get.env.get
      val expected = env.values(PARAMETER)
      val actual = node.getArgs.asScala.toList

      checkArgumentsMatch(expected, actual, node.toString);

      node.setAttachedType(mapping.get.theType);
    }
    else {
      throw TypeCheckingException(s"Error: Identifier ${node.getName} in ${node.toString} doesn't map to a function.")
    }
  }

  private def checkArgumentsMatch(expected: List[Mapping], actual: List[Node], errorLine: String): Unit = {
    if (actual.length != expected.length) {
      throw TypeCheckingException(s"Error: Incorrect number of arguments provided in $errorLine.")
    }

    for (i <- 0 to actual.length - 1) {
      // Stack arrays can be passed to other functions as the pointer will just be copied, will lose size info though
      val arg1 = expected(i).theType
      val arg2 = actual(i).getAttachedType

      if (arg1.getClass != arg2.getClass) {
        (arg1, arg2) match {
          case (_: IntHeapArray_T, _:IntStackArray_T) | (_: CharHeapArray_T, _: CharStackArray_T) => ; // Do nothing, as you should be able to pass a stack array as an argument.
          case _ => throw TypeCheckingException(s"Error: Actual argument types don't match expected ones in $errorLine.")
        }
      }
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
    val mapping = currentEnv.lookupMapping(node.getName)

    if (mapping.isEmpty)
      throw TypeCheckingException(s"Error: Undeclared identifier ${node.getName}.")

    node.setAttachedType(mapping.get.theType)
  }

  override def postVisit(node: Expression.NewArray): Unit = {
    val lenType = node.getLength.getAttachedType

    if (!lenType.isInstanceOf[Char_T] && !lenType.isInstanceOf[Int_T])
      throw TypeCheckingException(s"Error: Unable to use ${node.getLength.toString} as array size in ${node.toString}.")

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
    val funcType = currentEnv.funcType
    val retType = node.getRet.getAttachedType

    if (funcType.getClass != retType.getClass)
      throw TypeCheckingException(s"Error: Return expression ${node.getRet.toString} doesn't match the expected return type.")

    for (stm <- node.getStms.asScala.toList) {
      if (!stm.getAttachedType.isInstanceOf[Unit_T])
        throw TypeCheckingException(s"Error: Statement ${stm.toString} is not of type Unit.")
    }

    node.setAttachedType(new Unit_T)
  }

  override def postVisit(node: FunctionDeclaration): Unit = {
    if (!node.getSignature.getAttachedType.isInstanceOf[Unit_T])
      throw TypeCheckingException(s"Error: Function body for ${node.getSignature.toString} is not of type Unit.")

    node.setAttachedType(new Unit_T)
  }

  override def postVisit(node: FunctionDefinition): Unit = {
    if (!node.getSignature.getAttachedType.isInstanceOf[Unit_T])
      throw TypeCheckingException(s"Error: Function signature ${node.getSignature.toString} is not of type Unit.")

    if (!node.getBody.getAttachedType.isInstanceOf[Unit_T])
      throw TypeCheckingException(s"Error: Function body for ${node.getSignature.toString} is not of type Unit.")

    currentEnv = rootEnv // Go back to the root table which maps all functions
    node.setAttachedType(new Unit_T)
  }

  override def postVisit(node: FunctionSignature): Unit = {
    if (declFlag)
      handleDeclarationSignature(node)
    else
      handleDefinitionSignature(node)

    node.setAttachedType(new Unit_T)
  }

  def handleDeclarationSignature(node: FunctionSignature) = {
    rootEnv.add(node.getName, node.getTypeLabel.getAttachedType, FUNCTION) // Put the function signature in the root env
    currentEnv = rootEnv.lookupMapping(node.getName).get.env.get

    // Add all arguments from the signature to the newly created env for that function
    val args = node.getArgs.asScala.toList
    for (arg <- args)
      currentEnv.add(arg.getName, arg.getAttachedType, PARAMETER)

    currentEnv = rootEnv
    decls.add(node.getName) // Add that function to the list of declared ones
  }

  def handleDefinitionSignature(node: FunctionSignature) = {
    val mapping = rootEnv.lookupMapping(node.getName)

    if (mapping.isEmpty)
      throw TypeCheckingException(s"Error: ${node.toString} doesn't have a corresponding function declaration.")

    val env = mapping.get.env.get
    val expected = env.values(PARAMETER)
    val actual = node.getArgs.asScala.toList

    checkArgumentsMatch(expected, actual, node.toString)

    currentEnv = env
    decls.remove(node.getName) // Mark this function as defined
  }

  override def preVisit(node: MainFunction): Boolean = {
    declFlag = false

    rootEnv.add("main", new Int_T, FUNCTION)

    currentEnv = rootEnv.lookupMapping("main").get.env.get

    // Adjust offsets for main function
    // First local is -4 from %ebp
    // Argc is 0 from %ebp
    // Argv[0] is +4 from %ebp
    // ...
    currentEnv.paramOffset = -4
    currentEnv.localOffset = 0

    currentEnv.add(node.getArgc, new Int_T, PARAMETER)
    currentEnv.add(node.getArgv, new StringArray_T, PARAMETER)

    true // Continue the walk
  }

  override def postVisit(node: MainFunction): Unit = {
    if (!node.getBody.getAttachedType.isInstanceOf[Unit_T])
      throw TypeCheckingException(s"Error: Function body for ${node.toString} is not of type Unit.")

    currentEnv = rootEnv

    node.setAttachedType(new Unit_T)
  }

  override def postVisit(node: Parameter): Unit = {
    node.setAttachedType(node.getTypeLabel.getAttachedType)
  }

  override def postVisit(node: Program): Unit = {
    if (!decls.isEmpty)
      throw TypeCheckingException(s"Error: No function definition provided for ${decls.iterator.next().toString}")

    for (declaration <- node.getDeclarations.asScala.toList) {
      if (!declaration.getAttachedType.isInstanceOf[Unit_T])
        throw TypeCheckingException(s"Error: Declaration ${declaration.toString} is not of type Unit.")
    }

    if (!node.getMainFunction.getAttachedType.isInstanceOf[Unit_T])
      throw TypeCheckingException(s"Error: Main function is not of type Unit")

    for (definition <- node.getDeclarations.asScala.toList) {
      if (!definition.getAttachedType.isInstanceOf[Unit_T])
        throw TypeCheckingException(s"Error: Definition for ${definition.getSignature.toString} is not of type Unit.")
    }
  }

  override def postVisit(node: Statement.Compound): Unit = {
    val stms = node.getStms.asScala.toList

    for (stm <- stms) {
      if (!stm.getAttachedType.isInstanceOf[Unit_T])
        throw TypeCheckingException(s"Error: Statement ${stm.toString} is not of type Unit.")
    }

    node.setAttachedType(new Unit_T)
  }

  override def postVisit(node: Statement.IfThenElse): Unit = {
    if (!node.getCond.getAttachedType.isInstanceOf[Bool_T])
      throw TypeCheckingException(s"Error: If condition ${node.getCond.toString} is not of type Bool.")

    if (!node.getThen.getAttachedType.isInstanceOf[Unit_T])
      throw TypeCheckingException(s"Error: Then statement ${node.getThen.toString} is not of type Unit.")

    if (!node.getElse.getAttachedType.isInstanceOf[Unit_T])
      throw TypeCheckingException(s"Error: Else statement ${node.getElse.toString} is not of type Unit.")

    node.setAttachedType(new Unit_T)
  }

  override def postVisit(node: Statement.While): Unit = {
    if (!node.getCond.getAttachedType.isInstanceOf[Bool_T])
      throw TypeCheckingException(s"Error: While condition ${node.getCond.toString} is not of type Bool.")

    if (!node.getBody.getAttachedType.isInstanceOf[Unit_T])
      throw TypeCheckingException(s"Error: While body ${node.getBody.toString} is not of type Unit.")

    node.setAttachedType(new Unit_T)
  }

  override def postVisit(node: Statement.Assign): Unit = {
    val mapping = currentEnv.lookupMapping(node.getName)
    val assignmentType = node.getVal.getAttachedType

    if (!mapping.isDefined)
      throw TypeCheckingException(s"Error: Undeclared identifier ${node.getName}.")

    if (mapping.get.theType.isInstanceOf[CharStackArray_T] || mapping.get.theType.isInstanceOf[IntStackArray_T])
      throw TypeCheckingException(s"Error: Can't reassign stack array ${node.getName}.")

    if (mapping.get.theType.getClass != node.getVal.getAttachedType.getClass) {
      (mapping.get.theType, assignmentType) match { // todo: Allow assignment of stack arrays to heap arrays
        case (_: Int_T, _: Char_T) | (_: Char_T, _: Int_T) => node.setAttachedType(new Unit_T) // Can cast ints to chars
        case _                                             => throw TypeCheckingException(s"Error: Unable to assign ${node.getVal.toString} to ${node.getName} in ${node.toString}.")
      }
    }

    node.setAttachedType(new Unit_T)
  }

  override def postVisit(node: Statement.ArrayAssign): Unit = {
    val index = node.getIdx.getAttachedType

    if (!index.isInstanceOf[Int_T] && !index.isInstanceOf[Char_T])
      throw TypeCheckingException(s"Error: ${index.toString} can't be used to index in ${node.toString}.")

    val mapping = currentEnv.lookupMapping(node.getName)
    val assignmentType = node.getVal.getAttachedType

    if (mapping.isEmpty)
      throw TypeCheckingException(s"Error: Undeclared identifier ${node.getName}.")

    (mapping.get.theType, assignmentType) match {
      case (_: CharStackArray_T, _: Char_T) | (_: CharHeapArray_T, _: Char_T) => node.setAttachedType(new Unit_T)
      case (_: CharStackArray_T, _: Int_T) | (_: CharHeapArray_T, _: Int_T)   => node.setAttachedType(new Unit_T)
      case (_: IntStackArray_T, _: Int_T) | (_: IntHeapArray_T, _: Int_T)     => node.setAttachedType(new Unit_T)
      case (_: IntStackArray_T, _: Char_T) | (_: IntHeapArray_T, _: Char_T)   => node.setAttachedType(new Unit_T)
      case (_: StringArray_T, _: CharHeapArray_T)                             => node.setAttachedType(new Unit_T) // todo: Check usage of StringArray_T
      case _                                                                  => throw TypeCheckingException(s"Error: Types do not match in assignment ${node.toString}.")
    }
  }

  override def postVisit(node: Statement.Free): Unit = {
    val mapping = currentEnv.lookupMapping(node.getName)

    if (mapping.isEmpty)
      throw TypeCheckingException(s"Error: Undeclared identifier ${node.getName}.")

    mapping.get.theType match {
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
    node.getTypeConst match {
      case sym.INT  => node.setAttachedType(new IntStackArray_T(node.getSize))
      case sym.CHAR => node.setAttachedType(new CharStackArray_T(node.getSize))
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
    currentEnv.add(node.getName, node.getTypeLabel.getAttachedType, LOCAL)
  }
}
