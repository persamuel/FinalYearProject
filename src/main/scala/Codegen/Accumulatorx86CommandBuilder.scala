package Codegen

class Accumulatorx86CommandBuilder {
  private var lcount: Int = 0

  def newLabel(): String = {
    lcount += 1
    s"l$lcount"
  }

  /**
   * Does nothing
   */
  def buildNop(): String = {
    "nop\n"
  }

  /**
   * Pops the top of the stack into the accumulator
   */
  def buildPop(): String = {
    "popl %eax\n"
  }

  /**
   * Pushes the value in the accumulator onto the stack
   */
  def buildPush(): String = {
    "pushl %eax\n"
  }

  /**
   * Loads a word from the provided location into the accumulator
   */
  def buildLoad(loc: String): String = {
    s"movl $loc,%eax\n"
  }

  /**
   * Loads a byte from the provided location into the accumulator
   */
  def buildLoadByte(loc: String): String = {
    s"movb $loc,%eax\n"
  }

  /**
   * Loads the memory address of the provided location into the accumulator
   */
  def buildLoadEff(loc: String): String = {
    s"leal $loc,%eax\n"
  }

  /**
   * Loads an immediate value (constant) into the accumulator
   */
  def buildLoadImm(value: String): String = {
    s"movl $$$value,%eax\n"
  }

  /**
   * Stores what's in the accumulator at the location provided
   */
  def buildStore(loc: String): String = {
    s"movl %eax,$loc\n"
  }

  /**
   * Compares the accumulator with the top of the stack, stores true in the accumulator if it's bigger than the top of
   * the stack otherwise stores false there. Also cleans up the stack.
   *
   * LHS of the expression should be in the accumulator.
   * RHS of the expression should be on the top of the stack.
   */
  def buildCompGT(): String = {
    buildComp("jg")
  }

  def buildCompGTE(): String = {
    buildComp("jge")
  }

  def buildCompLT(): String = {
    buildComp("jl")
  }

  def buildCompLTE(): String = {
    buildComp("jle")
  }

  def buildCompEQ(): String = {
    buildComp("je")
  }

  def buildCompNEQ(): String = {
    buildComp("jne")
  }

  private def buildComp(op: String): String = {
    val truepath = newLabel()
    val cleanup = newLabel()

    "cmp (%esp),%eax\n" ++  // Compare the accumulator and the top of the stack
    s"$op $truepath\n" ++   // Jump based on the operator chosen
    "movb $0,%eax\n" ++     // Put false in the accumulator if comparison didn't succeed
    s"jmp $cleanup\n" ++
    s"$truepath:\n" ++
    "movb $255,%eax\n" ++   // Put true in the accumulator if comparison succeeded
    s"$cleanup:\n" ++
    "addl $4,%esp\n"        // Cleanup the stack
  }

  /**
   * Jumps to location.
   */
  def buildJump(loc: String): String = {
    s"jmp $loc\n"
  }

  /**
   * Jumps to location if accumulator holds true.
   */
  def buildJumpTrue(loc: String): String = {
    "cmpb $255,%eax\n" ++     // Compares true with the content of the accumulator
    s"je $loc\n"              // Jumps to location
  }

  /**
   * Pops the value from the top of the stack and adds it to the accumulator, storing the result in the accumulator.
   */
  def buildPlus(): String = {
    "addl (%esp),%eax\n" ++
    "addl $4,%esp\n"
  }

  /**
   * Pops the value from the top of the stack and multiplies the accumulator by it, storing the result in the accumulator.
   */
  def buildMulti(): String = {
    "imull (%esp),%eax\n" ++
    "addl $4,%esp\n"
  }

  /**
   * Pops the value from the top of the stack and subtracts it from the accumulator, storing the result in the accumulator.
   */
  def buildMinus(): String = {
    "subl (%esp),%eax\n" ++ // Subtracts the first operand from the second
    "addl $4,%esp\n"
  }

  /*def buildDIVIDE(): String = {
    ""
  }*/

  /**
   * Performs logical AND between the top of the stack and the accumulator, storing the result in the accumulator.
   */
  def buildAnd(): String = {
    "andl (%esp),%eax\n" ++
    "addl $4,%esp\n"
  }

  /**
   * Performs logical OR between the top of the stack and the accumulator, storing the result in the accumulator.
   */
  def buildOr(): String = {
    "orl (%esp),%eax\n" ++
    "addl $4,%esp\n"
  }

  /**
   * Negates what's in the accumulator.
   */
  def buildNegate(): String = {
    "negl %eax"
  }
}
