package Visitors

import Analysis.MyType._
import Codegen.Accumulatorx86CommandBuilder
import Enviroment.SymbolTable
import Node._
import Parser.sym
import collection.JavaConverters._

class CodegenVisitor(private val rootEnv: SymbolTable) extends Analysis.NodeVisitor {
  private var declFlag = true

  private var currentEnv = rootEnv

  private val builder = new Accumulatorx86CommandBuilder

  override def postVisit(node: Expression.Logical): Unit = {
    val tmp = node.getRhs.getAttachedAssembly ++
    builder.buildPush() ++                        // RHS of expression goes on the TOS
    node.getLhs.getAttachedAssembly ++            // LHS of expression goes in the accumulator
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
      case sym.EQEQ => builder.buildCompEQ()
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
    val arrayType = node.getName.getAttachedType

    val tmp = node.getIdx.getAttachedAssembly ++
      (arrayType match {
        case _: CharHeapArray_T | _: CharStackArray_T => {
          builder.buildPush ++                              // Push the index onto the top of the stack
          node.getName.getAttachedAssembly ++               // Load the address of the start of the array in the acc
          builder.buildPlus ++                              // Add the index to the array address
          builder.buildLoadByte("(%eax)")             // Load a byte from the address in the accumulator
        }
        case _: IntHeapArray_T | _: IntStackArray_T | _: StringArray_T => { // todo: Check usage of StringArray_T
          "imull $4,%eax\n" ++                              // Multiply the index by 4 to adjust for integer size
          builder.buildPush ++
          node.getName.getAttachedAssembly ++
          builder.buildPlus ++
          builder.buildLoad("(%eax)")                 // Load a word (int) from the address in the accumulator
        }
      })

    node.setAttachedAssembly(tmp)
  }

  override def postVisit(node: Expression.Call): Unit = {
    var tmp = ""
    val args = node.getArgs.asScala.toList.reverse  // Reverse the list to respect calling convention

    for (arg <- args) {
      tmp += arg.getAttachedAssembly                // Evaluate the argument
      tmp += builder.buildPush                      // Push it onto the stack
    }

    tmp += s"call ${node.getName}\n"                // Call the function
    tmp += s"addl $$${4 * args.length},%esp\n"      // Cleanup the arguments on the stack

    node.setAttachedAssembly(tmp)
  }

  override def postVisit(node: Expression.IntLiteral): Unit = {
    node.setAttachedAssembly(builder.buildLoadImm(Integer.toString(node.getVal)))
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
    val offset = currentEnv.lookupMapping(node.getName).get.frameOffset

    node.getAttachedType match {
      case _: CharStackArray_T | _: IntStackArray_T | _: StringArray_T =>  // Have to load the address, not the first value
        node.setAttachedAssembly(builder.buildLoadEff(s"$offset(%ebp)"))
      case _ =>
        node.setAttachedAssembly(builder.buildLoad(s"$offset(%ebp)"))
    }
  }

  override def postVisit(node: Expression.NewArray): Unit = {
    val tmp = node.getLength.getAttachedAssembly ++
    (if (node.getTypeConst == sym.INT) "imull $4,%eax\n" else "") ++  // Multiply byte size by 4 to adjust for integers
    builder.buildPush() ++
    "call malloc\n" ++
    "addl $4,%esp\n"

    node.setAttachedAssembly(tmp)
  }

  override def postVisit(node: Expression.Negated): Unit = {
    node.setAttachedAssembly(node.getExp.getAttachedAssembly ++ builder.buildNegate())
  }

  override def postVisit(node: FunctionBody): Unit = {
    var tmp = ""

    for (stm <- node.getStms.asScala.toList)  // Simply concatenate all the statements of the body together
      tmp += stm.getAttachedAssembly

    node.setAttachedAssembly(tmp + node.getRet.getAttachedAssembly)
  }

  override def postVisit(node: FunctionDeclaration): Unit = {
    // Do Nothing
  }

  override def postVisit(node: FunctionDefinition): Unit = {
    val label = currentEnv.funcName
    val tmp = s".type $label,@function\n" ++
    s"$label:\n" ++
    "pushl %ebp\n" ++                                       // Save the old base pointer
    "movl %esp,%ebp\n" ++                                   // Make the stack pointer the base pointer
    s"subl $$${Math.abs(currentEnv.frameSize)},%esp\n" ++   // Allocate room needed for local storage
    node.getBody.getAttachedAssembly ++
    "movl %ebp,%esp\n" ++                                   // Restore the stack pointer
    "popl %ebp\n" ++                                        // Restore the base pointer
    "ret\n"

    node.setAttachedAssembly(tmp)
    currentEnv = rootEnv
  }

  override def postVisit(node: FunctionSignature): Unit = {
    if (!declFlag)  // Go into scope if it has passed the main function
      currentEnv = rootEnv.lookupMapping(node.getName).get.env.get
  }


  override def preVisit(node: MainFunction): Boolean = {
    declFlag = false
    currentEnv = currentEnv.lookupMapping("main").get.env.get
    true
  }

  override def postVisit(node: MainFunction): Unit = {
    val tmp = ".globl _start\n" ++
    "_start:\n" ++
    "movl %esp, %ebp\n" ++                  // bp will point to argc since there's no old bp or ra to save
    s"subl $$${Math.abs(currentEnv.frameSize)},%esp\n" ++   // Allocate room needed for local storage
    node.getBody.getAttachedAssembly ++
    builder.buildPush() ++                // Push our exit status on the stack
    "call exit\n"                         // Call the function to exit

    node.setAttachedAssembly(tmp)

    currentEnv = rootEnv
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

    for (func <- node.getDefinitions.asScala.toList)
      codesection += func.getAttachedAssembly

    node.setAttachedAssembly(datasection ++ codesection)
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
    val mapping = currentEnv.lookupMapping(node.getName).get

    val tmp = node.getVal.getAttachedAssembly ++
    builder.buildStore(s"${mapping.frameOffset}(%ebp)")

    node.setAttachedAssembly(tmp)
  }

  override def postVisit(node: Statement.ArrayAssign): Unit = {
    val mapping = currentEnv.lookupMapping(node.getName).get
    val offset = mapping.frameOffset
    val arrayType = mapping.theType

    val tmp = node.getIdx.getAttachedAssembly ++
    (arrayType match {
      case _: IntHeapArray_T | _: IntStackArray_T | _: StringArray_T => "imull $4,%eax\n"
      case _ => ""
    }) ++
    builder.buildPush ++ // Push the index onto the top of the stack
    (arrayType match { // todo: Check usage of StringArray_T
      case _: CharStackArray_T | _: IntStackArray_T | _: StringArray_T => builder.buildLoadEff(s"$offset(%ebp)") // Load the address of the start of the array in the acc
      case _ =>                                                           builder.buildLoad(s"$offset(%ebp)")
    }) ++
    builder.buildPlus ++                              // Add the index to the array address
    "pushl %ebx\n" ++
    "movl %eax,%ebx\n" ++
    node.getVal.getAttachedAssembly ++
      (arrayType match {
        case _: CharHeapArray_T | _: CharStackArray_T =>  "movb %eax,(%ebx)\n"
        case _ =>                                         "movl %eax,(%ebx)\n"
      }) ++
    "popl %ebx\n"

    node.setAttachedAssembly(tmp)
  }

  override def postVisit(node: Statement.Free): Unit = {
    val mapping = currentEnv.lookupMapping(node.getName).get

    val tmp =
      (mapping.theType match {
        case _: CharStackArray_T | _: IntStackArray_T =>  builder.buildLoadEff(s"${mapping.frameOffset}(%ebp)")
        case _ =>                                         builder.buildLoad(s"${mapping.frameOffset}(%ebp)")
      }) ++
    builder.buildPush() ++
    "call free\n" ++         // Call the free function
    "addl $4,%esp\n"         // Cleanup the parameter on the stack

    node.setAttachedAssembly(tmp)
  }

  override def postVisit(node: Statement.Print): Unit = {
    val tmp =
    (if (node.getVal.getAttachedType.isInstanceOf[Bool_T])
      printBool(node)
    else
      printOther(node))

    node.setAttachedAssembly(tmp)
  }

  private def printBool(node: Statement.Print): String = {
    val tlabel = builder.newLabel
    val endlabel = builder.newLabel

    node.getVal.getAttachedAssembly ++
    builder.buildJumpTrue(tlabel) ++
    "pushl $print_false\n" ++
    builder.buildJump(endlabel) ++
    s"$tlabel:\n" ++
    "pushl $print_true\n" ++
    s"$endlabel:\n" ++
    "call printf\n" ++
    "addl $4,%esp\n"
  }

  private def printOther(node: Statement.Print): String = {
    node.getVal.getAttachedAssembly ++
    builder.buildPush() ++
    (node.getVal.getAttachedType match {
      case _:Int_T =>             "pushl $print_int\n"
      case _:Char_T =>            "pushl $print_char\n"
      case _:IntStackArray_T =>   "pushl $print_arr\n"
      case _:IntHeapArray_T =>    "pushl $print_arr\n"
      case _:CharStackArray_T =>  "pushl $print_str\n"
      case _:CharHeapArray_T =>   "pushl $print_str\n"
    }) ++
    "call printf\n" ++
    "addl $8,%esp\n"
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
