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
    buildComp("jlt")
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
    // todo: Check the ordering of this instruction
    "cmpl (%esp),%eax\n" + // Compare the accumulator and the top of the stack
    s"$op $truepath\n" + // Jump based on the operator chosen
    "movb $0,%eax\n" + // Put false in the accumulator if comparison didn't succeed
    s"jmp $cleanup\n" +
    s"$truepath:\n" +
    "movb $255,%eax\n" + // Put true in the accumulator if comparison succeeded
    s"$cleanup:\n" +
    "incl %esp\n" // Cleanup the stack
  }

  def buildJump(loc: String): String = {
    s"jmp $loc\n"
  }

  def buildJumpTrue(loc: String): String = {
    "cmpb $255,%eax\n" +
    s"je $loc\n"
  }

  def buildPlus(): String = {
    "addl (%esp),%eax\n" +
    "incl %esp\n"
  }

  def buildMulti(): String = {
    "imull (%esp),%eax\n" +
    "incl %esp\n"
  }

  def buildMinus(): String = {
    "subl (%esp),%eax\n" +
    "incl %esp\n"
  }

  /*def buildDIVIDE(): String = {
    ""
  }*/

  def buildAnd(): String = {
    "andl (%esp),%eax\n" +
    "incl %esp\n"
  }

  def buildOr(): String = {
    "orl (%esp),%eax\n" +
    "incl %esp\n"
  }

  def buildNegate(): String = {
    "negl %eax"
  }
}
