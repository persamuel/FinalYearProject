package Visitors

import Analysis.MyType._
import Codegen.Accumulatorx86CommandBuilder
import Enviroment.SymbolTable
import Node._
import Parser.sym
import collection.JavaConverters._

class CodegenVisitor(private val rootTable: SymbolTable) extends Analysis.NodeVisitor {
  private var declFlag = true
  private var currentFunctionEnv = rootTable
  private val builder = new Accumulatorx86CommandBuilder

  override def postVisit(node: Expression.Logical): Unit = {
    val tmp = node.getLhs.getAttachedAssembly +
    builder.buildPush() +
    node.getRhs.getAttachedAssembly +
    (node.getOp match {
      case sym.AND => builder.buildAnd()
      case sym.OR => builder.buildOr()
    })

    node.setAttachedAssembly(tmp)
  }

  override def postVisit(node: Expression.Equality): Unit = {
    val tmp = node.getLhs.getAttachedAssembly +
    builder.buildPush() +
    node.getRhs.getAttachedAssembly +
    (node.getOp match {
      case sym.EQ => builder.buildCompEQ()
      case sym.NOTEQ => builder.buildCompNEQ()
    })

    node.setAttachedAssembly(tmp)
  }

  override def postVisit(node: Expression.Comparison): Unit = {
    val tmp = node.getLhs.getAttachedAssembly +
    builder.buildPush() +
    node.getRhs.getAttachedAssembly +
    (node.getOp match {
      case sym.GT => builder.buildCompGT()
      case sym.GTE => builder.buildCompGTE()
      case sym.LT => builder.buildCompLT()
      case sym.LTE => builder.buildCompLTE()
    })

    node.setAttachedAssembly(tmp)
  }

  override def postVisit(node: Expression.Arithmetic): Unit = {
    val tmp = node.getLhs.getAttachedAssembly +
      builder.buildPush() +
      node.getRhs.getAttachedAssembly +
      (node.getOp match {
        case sym.PLUS => builder.buildPlus()
        case sym.MINUS => builder.buildMinus()
        case sym.MULTI => builder.buildMulti()
      })

    node.setAttachedAssembly(tmp)
  }

  override def postVisit(node: Expression.ArrayAccess): Unit = {
    val offset = currentFunctionEnv.lookupMapping(node.getName.toString).get.offset

    if (node.getName.getAttachedType.isInstanceOf[CharStackArray_T] || node.getName.getAttachedType.isInstanceOf[CharHeapArray_T]) {
      val tmp = node.getName.getAttachedAssembly +
      builder.buildPush +
      node.getIdx.getAttachedAssembly +
      builder.buildLoad("(%esp,%eax,1)") +
      "incl %esp\n"

      node.setAttachedAssembly(tmp)
    }
    else if (node.getName.getAttachedType.isInstanceOf[IntStackArray_T] || node.getName.getAttachedType.isInstanceOf[IntStackArray_T]) {
      val tmp = node.getName.getAttachedAssembly +
      builder.buildPush +
      node.getIdx.getAttachedAssembly +
      builder.buildLoad("(%esp,%eax,4)") +
      "incl %esp\n"

      node.setAttachedAssembly(tmp)
    }
  }

  override def postVisit(node: Expression.Call): Unit = {

  }

  override def postVisit(node: Expression.IntLiteral): Unit = {
    node.setAttachedAssembly(builder.buildLoadImm(node.getVal.toString))
  }

  override def postVisit(node: Expression.CharLiteral): Unit = {
    node.setAttachedAssembly(builder.buildLoadImm(node.getVal.toInt.toString))
  }

  override def postVisit(node: Expression.BoolLiteral): Unit = {
    if (node.getVal)
      node.setAttachedAssembly(builder.buildLoadImm("255"))
    else
      node.setAttachedAssembly(builder.buildLoadImm("0"))
  }

  override def postVisit(node: Expression.Identifier): Unit = {
    val offset = currentFunctionEnv.lookupMapping(node.getName).get.offset

    if (node.getAttachedType.isInstanceOf[CharStackArray_T] || node.getAttachedType.isInstanceOf[IntStackArray_T])
      node.setAttachedAssembly(builder.buildLoadEff(s"$offset(%ebp)"))
    else
      node.setAttachedAssembly(builder.buildLoad(s"$offset(%ebp)"))
  }

  override def postVisit(node: Expression.NewArray): Unit = {
    val tmp = node.getLength.getAttachedAssembly +
    (if (node.getTypeConst == sym.INT) "imull $4,%eax\n") +
    builder.buildPush() +
    "call _malloc\n" +
    "incl %esp\n"

    node.setAttachedAssembly(tmp)
  }

  override def postVisit(node: Expression.Negated): Unit = {
    node.setAttachedAssembly(node.getExp.getAttachedAssembly + builder.buildNegate())
  }

  override def postVisit(node: FunctionBody): Unit = {
    var tmp: String = ""

    for (stm <- node.getStms.asScala.toList)
      tmp += stm.getAttachedAssembly

    node.setAttachedAssembly(tmp + node.getRet.getAttachedAssembly)
  }

  override def postVisit(node: FunctionDeclaration): Unit = {
    // Do Nothing
  }

  override def postVisit(node: FunctionDefinition): Unit = {
    val label = builder.newLabel()
    val tmp = s".type $label,@function\n" +
    s"$label:\n" +
    "pushl %ebp\n" + // Save the old base pointer
    "movl %esp,%ebp\n" + // Make the stack pointer the base pointer
    s"subl ${currentFunctionEnv.roomNeeded},%esp\n" + // Allocate room needed for local storage
    node.getBody.getAttachedAssembly +
    "movl %ebp,%esp\n" + // Restore the stack pointer
    "popl %ebp\n" + // Restore the base pointer
    "ret\n"

    node.setAttachedAssembly(tmp)

    // go back up to higher scope
    currentFunctionEnv = currentFunctionEnv.parent.get
  }

  override def postVisit(node: FunctionSignature): Unit = {
    // go into scope if it is passed the main function
    if (!declFlag) {
      currentFunctionEnv = currentFunctionEnv.lookupMapping(node.getName).get.enviroment.get
    }
  }


  override def preVisit(node: MainFunction): Boolean = {
    declFlag = false

    currentFunctionEnv = currentFunctionEnv.lookupMapping("main").get.enviroment.get

    true
  }

  override def postVisit(node: MainFunction): Unit = {
    val tmp = ".globl _start\n" +
    "_start:\n" +
    node.getBody.getAttachedAssembly +
    builder.buildPush() + // Push our exit status on the stack
    "call exit\n" // call the function to exit

    node.setAttachedAssembly(tmp)

    currentFunctionEnv = currentFunctionEnv.parent.get
  }

  override def postVisit(node: Parameter): Unit = ???

  override def postVisit(node: Program): Unit = {
    val datasection = ".section .data\n" +
    "print_int:" +
    ".ascii \"%d\n\"" +
    "print_str:" +
    ".ascii \"%s\n\"" +
    "print_arr:" +
    ".ascii \"%p\"" +
    "print_char:" +
    ".ascii \"%c\n\"" +
    "print_true:" +
    ".ascii \"True\n\"" +
    "print_false:" +
    ".ascii \"False\n\""
  }

  override def postVisit(node: Statement.Compound): Unit = ???

  override def postVisit(node: Statement.IfThenElse): Unit = ???

  override def postVisit(node: Statement.While): Unit = ???

  override def postVisit(node: Statement.Assign): Unit = ???

  override def postVisit(node: Statement.ArrayAssign): Unit = ???

  override def postVisit(node: Statement.Free): Unit = {
    val mapping = currentFunctionEnv.lookupMapping(node.getName).get

    val tmp =
    (if (mapping.typeof.isInstanceOf[CharStackArray_T] || mapping.typeof.isInstanceOf[IntStackArray_T])
      builder.buildLoadEff(s"${mapping.offset}(%ebp)")
    else
      builder.buildLoad(s"${mapping.offset}(%ebp)")) +
    "call _free\n" + // call the free function
    "incl %esp\n" // cleanup the parameter on the stack

    node.setAttachedAssembly(tmp)
  }

  override def postVisit(node: Statement.Print): Unit = ???

  override def postVisit(node: TypeLabel.Primitive): Unit = ???

  override def postVisit(node: TypeLabel.StackArray): Unit = ???

  override def postVisit(node: TypeLabel.HeapArray): Unit = ???

  override def postVisit(node: VariableDeclaration): Unit = ???
}
