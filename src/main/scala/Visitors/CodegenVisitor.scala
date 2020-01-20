package Visitors

import Analysis.MyType._
import Codegen.Accumulatorx86CommandBuilder
import Enviroment.SymbolTable
import Node._
import Parser.sym
import collection.JavaConverters._

class CodegenVisitor(private val rootTable: SymbolTable) extends Analysis.NodeVisitor {
  private var declFlag = true
  private var currentFunctionName = ""
  private var currentFunctionEnv = rootTable
  private val builder = new Accumulatorx86CommandBuilder

  override def postVisit(node: Expression.Logical): Unit = {
    val tmp = node.getRhs.getAttachedAssembly ++
    builder.buildPush() ++
    node.getLhs.getAttachedAssembly ++
    (node.getOp match {
      case sym.AND => builder.buildAnd()
      case sym.OR => builder.buildOr()
    })

    node.setAttachedAssembly(tmp)
  }

  override def postVisit(node: Expression.Equality): Unit = {
    val tmp = node.getRhs.getAttachedAssembly ++
    builder.buildPush() ++
    node.getLhs.getAttachedAssembly ++
    (node.getOp match {
      case sym.EQ => builder.buildCompEQ()
      case sym.NOTEQ => builder.buildCompNEQ()
    })

    node.setAttachedAssembly(tmp)
  }

  override def postVisit(node: Expression.Comparison): Unit = {
    val tmp = node.getRhs.getAttachedAssembly ++
    builder.buildPush() ++
    node.getLhs.getAttachedAssembly ++
    (node.getOp match {
      case sym.GT => builder.buildCompGT()
      case sym.GTE => builder.buildCompGTE()
      case sym.LT => builder.buildCompLT()
      case sym.LTE => builder.buildCompLTE()
    })

    node.setAttachedAssembly(tmp)
  }

  override def postVisit(node: Expression.Arithmetic): Unit = {
    val tmp = node.getRhs.getAttachedAssembly ++
      builder.buildPush() ++
      node.getLhs.getAttachedAssembly ++
      (node.getOp match {
        case sym.PLUS => builder.buildPlus()
        case sym.MINUS => builder.buildMinus()
        case sym.MULTI => builder.buildMulti()
      })

    node.setAttachedAssembly(tmp)
  }

  override def postVisit(node: Expression.ArrayAccess): Unit = {

    if (node.getName.getAttachedType.isInstanceOf[CharStackArray_T] || node.getName.getAttachedType.isInstanceOf[CharHeapArray_T]) {
      val tmp = node.getIdx.getAttachedAssembly ++
      builder.buildPush ++
      node.getName.getAttachedAssembly ++
      builder.buildPlus ++
      builder.buildLoadByte("(%eax)") ++
      "incl %esp\n"

      node.setAttachedAssembly(tmp)
    }
    else if (node.getName.getAttachedType.isInstanceOf[IntStackArray_T] || node.getName.getAttachedType.isInstanceOf[IntHeapArray_T]) {
      val tmp = node.getIdx.getAttachedAssembly ++
      builder.buildPush ++
      builder.buildLoadImm("4")
      builder.buildMulti ++
      builder.buildPush ++
      node.getName.getAttachedAssembly ++
      builder.buildPlus ++
      builder.buildLoad("(%eax)") ++
      "incl %esp\n"

      node.setAttachedAssembly(tmp)
    }
  }

  override def postVisit(node: Expression.Call): Unit = {
    var tmp = ""
    val args = node.getArgs.asScala.toList.reverse

    for (arg <- args) {
      tmp += arg.getAttachedAssembly // Evaluate the argument
      tmp += builder.buildPush // Push it onto the stack
    }

    tmp += s"call ${node.getName}\n"
    tmp += s"addl $$${4 * args.length},%esp\n"

    node.setAttachedAssembly(tmp)
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
    val offset = currentFunctionEnv.lookupMapping(node.getName).get.frameOffset

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
    val label = currentFunctionName
    val tmp = s".type $label,@function\n" ++
    s"$label:\n" ++
    "pushl %ebp\n" ++ // Save the old base pointer
    "movl %esp,%ebp\n" ++ // Make the stack pointer the base pointer
    s"subl $$${Math.abs(currentFunctionEnv.frameSize)},%esp\n" ++ // Allocate room needed for local storage
    node.getBody.getAttachedAssembly ++
    "movl %ebp,%esp\n" ++ // Restore the stack pointer
    "popl %ebp\n" ++ // Restore the base pointer
    "ret\n"

    node.setAttachedAssembly(tmp)

    // go back up to higher scope
    currentFunctionEnv = rootTable
  }

  override def postVisit(node: FunctionSignature): Unit = {
    // go into scope if it is passed the main function
    if (!declFlag) {
      currentFunctionName = node.getName
      currentFunctionEnv = currentFunctionEnv.lookupMapping(node.getName).get.env.get
    }
  }


  override def preVisit(node: MainFunction): Boolean = {
    declFlag = false

    currentFunctionName = "main"
    currentFunctionEnv = currentFunctionEnv.lookupMapping("main").get.env.get

    true
  }

  override def postVisit(node: MainFunction): Unit = {
    val tmp = ".globl _start\n" ++
    "_start:\n" ++
    node.getBody.getAttachedAssembly ++
    builder.buildPush() ++ // Push our exit status on the stack
    "call exit\n" // call the function to exit

    node.setAttachedAssembly(tmp)

    currentFunctionEnv = rootTable
  }

  override def postVisit(node: Parameter): Unit = {
    // Do nothing
  }

  override def postVisit(node: Program): Unit = {
    val datasection = ".section .data\n" ++
    "print_int:\n" ++
    ".ascii \"%d\\0\"\n" ++
    "print_str:\n" ++
    ".ascii \"%s\\0\"\n" ++
    "print_arr:\n" ++
    ".ascii \"%p\\0\"\n" ++
    "print_char:\n" ++
    ".ascii \"%c\\0\"\n" ++
    "print_true:\n" ++
    ".ascii \"True\\0\"\n" ++
    "print_false:\n" ++
    ".ascii \"False\\0\"\n"

    var codesection = ".section .text\n" ++
    node.getMainFunction.getAttachedAssembly

    for (fun <- node.getDefinitions.asScala.toList)
      codesection += fun.getAttachedAssembly

    node.setAttachedAssembly(datasection + codesection)
  }

  override def postVisit(node: Statement.Compound): Unit = {
    var tmp = ""

    for (stm <- node.getStms.asScala.toList) {
      tmp += stm.getAttachedAssembly
    }

    node.setAttachedAssembly(tmp)
  }

  override def postVisit(node: Statement.IfThenElse): Unit = {
    val thenlabel = builder.newLabel
    val endlabel = builder.newLabel

    val tmp = node.getCond.getAttachedAssembly ++
    builder.buildJumpTrue(thenlabel) ++
    node.getElse.getAttachedAssembly ++
    builder.buildJump(endlabel) ++
    s"$thenlabel:\n" ++
    node.getThen.getAttachedAssembly ++
    s"$endlabel:\n"

    node.setAttachedAssembly(tmp)
  }

  override def postVisit(node: Statement.While): Unit = {
    val loopstart = builder.newLabel
    val loopbody = builder.newLabel
    val loopend = builder.newLabel

    val tmp = s"$loopstart:\n" ++
    node.getCond.getAttachedAssembly ++
    builder.buildJumpTrue(loopbody) ++
    builder.buildJump(loopend) ++
    s"$loopbody:\n" ++
    node.getBody.getAttachedAssembly ++
    builder.buildJump(loopstart) ++
    s"$loopend:\n"

    node.setAttachedAssembly(tmp)
  }

  override def postVisit(node: Statement.Assign): Unit = {
    val mapping = currentFunctionEnv.lookupMapping(node.getName).get

    val tmp = node.getVal.getAttachedAssembly ++
    builder.buildStore(s"${mapping.frameOffset}(%ebp)")

    node.setAttachedAssembly(tmp)
  }

  override def postVisit(node: Statement.ArrayAssign): Unit = {
    val mapping = currentFunctionEnv.lookupMapping(node.getName).get

    if (mapping.theType.isInstanceOf[CharStackArray_T]) { // todo: Change movl to movb when working with char arrays
      val tmp = node.getIdx.getAttachedAssembly ++ // Compute the index
      builder.buildPush ++ // Push the index to the stack
      builder.buildLoadEff(s"${mapping.frameOffset}(%ebp)") // Load the address of the start of the array
      builder.buildPlus ++ // Add the index on the ToS to the array start
      builder.buildPush ++ // Push the address computed onto the stack
      node.getVal.getAttachedAssembly ++ // Compute the value
      builder.buildStore("(%esp)") ++ // Store the value at the address computed
      "incl %esp\n"

      node.setAttachedAssembly(tmp)
    }
    else if (mapping.theType.isInstanceOf[CharHeapArray_T]) {
      val tmp = node.getIdx.getAttachedAssembly ++ // Compute the index
      builder.buildPush ++ // Push the index to the stack
      builder.buildLoad(s"${mapping.frameOffset}(%ebp)") // Load the address of the start of the array
      builder.buildPlus ++ // Add the index on the ToS to the array start
      builder.buildPush ++ // Push the address computed onto the stack
      node.getVal.getAttachedAssembly ++ // Compute the value
      builder.buildStore("(%esp)") ++ // Store the value at the address computed
      "incl %esp\n"

      node.setAttachedAssembly(tmp)
    }
    else if (mapping.theType.isInstanceOf[IntStackArray_T]) {
      val tmp = node.getIdx.getAttachedAssembly ++ // Compute the index
      builder.buildPush ++ // Push the index to the stack
      builder.buildLoadImm("4") ++
      builder.buildMulti ++
      builder.buildPush ++
      builder.buildLoadEff(s"${mapping.frameOffset}(%ebp)") // Load the address of the start of the array
      builder.buildPlus ++ // Add the index on the ToS to the array start
      builder.buildPush ++ // Push the address computed onto the stack
      node.getVal.getAttachedAssembly ++ // Compute the value
      builder.buildStore("(%esp)") ++ // Store the value at the address computed
      "incl %esp\n"

      node.setAttachedAssembly(tmp)
    }
    else if (mapping.theType.isInstanceOf[IntHeapArray_T]) {
      val tmp = node.getIdx.getAttachedAssembly ++ // Compute the index
      builder.buildPush ++ // Push the index to the stack
      builder.buildLoadImm("4") ++
      builder.buildMulti ++
      builder.buildPush ++
      builder.buildLoad(s"${mapping.frameOffset}(%ebp)") // Load the address of the start of the array
      builder.buildPlus ++ // Add the index on the ToS to the array start
      builder.buildPush ++ // Push the address computed onto the stack
      node.getVal.getAttachedAssembly ++ // Compute the value
      builder.buildStore("(%esp)") ++ // Store the value at the address computed
      "incl %esp\n"

      node.setAttachedAssembly(tmp)
    }
  }

  override def postVisit(node: Statement.Free): Unit = {
    val mapping = currentFunctionEnv.lookupMapping(node.getName).get

    val tmp =
    (if (mapping.theType.isInstanceOf[CharStackArray_T] || mapping.theType.isInstanceOf[IntStackArray_T])
      builder.buildLoadEff(s"${mapping.frameOffset}(%ebp)")
    else
      builder.buildLoad(s"${mapping.frameOffset}(%ebp)")) ++
    "call _free\n" ++ // call the free function
    "incl %esp\n" // cleanup the parameter on the stack

    node.setAttachedAssembly(tmp)
  }

  override def postVisit(node: Statement.Print): Unit = {
    val typeof = node.getVal.getAttachedType

    val tmp = node.getVal.getAttachedAssembly ++
    (if (typeof.isInstanceOf[Bool_T]) {
      val tlabel = builder.newLabel
      val endlabel = builder.newLabel
      builder.buildJumpTrue(tlabel) ++
      "pushl $print_false\n" ++
      builder.buildJump(endlabel) ++
      s"$tlabel:" ++
      "pushl $print_true\n" ++
      s"$endlabel:" ++
      "call printf\n" ++
      "incl %esp\n"
    }
    else {
      builder.buildPush() ++
      (typeof match {
        case _:Int_T => "pushl $print_int\n"
        case _:Char_T => "pushl $print_char\n"
        case _:IntStackArray_T => "pushl $print_arr\n"
        case _:IntHeapArray_T => "pushl $print_arr\n"
        case _:CharStackArray_T => "pushl $print_str\n"
        case _:CharHeapArray_T => "pushl $print_str\n"
      }) +
      "call printf\n" ++
      "addl $8,%esp\n"
    })

    node.setAttachedAssembly(tmp)
  }

  override def postVisit(node: TypeLabel.Primitive): Unit = {
    // Do Nothing
  }

  override def postVisit(node: TypeLabel.StackArray): Unit = {
    // Do Nothing
  }

  override def postVisit(node: TypeLabel.HeapArray): Unit =  {
    // Do Nothing
  }

  override def postVisit(node: VariableDeclaration): Unit = {
    // Do Nothing
  }
}
