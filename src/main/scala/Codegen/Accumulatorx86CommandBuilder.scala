package Codegen

class Accumulatorx86CommandBuilder {
  private var lcount: Int = 0

  def newLabel(): String = {
    lcount += 1
    s"l$lcount"
  }

  def buildNop(): String = {
    "nop\n"
  }

  def buildPop(): String = {
    "popl %eax\n"
  }

  def buildPush(): String = {
    "pushl %eax\n"
  }

  def buildLoad(loc: String): String = {
    s"movl $loc,%eax\n"
  }

  def buildLoadByte(loc: String): String = {
    s"movb $loc,%eax\n"
  }

  def buildLoadEff(loc: String): String = {
    s"leal $loc,%eax\n"
  }

  def buildLoadImm(value: String): String = {
    s"movl $$$value,%eax\n"
  }

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

    "cmpl %eax,(%esp)\n" + // Compare the accumulator and the top of the stack
    s"$op $truepath\n" + // Jump based on the operator chosen
    "movl $0,%eax\n" + // Put false in the accumulator if comparison didn't succeed
    s"jmp $cleanup\n" +
    s"$truepath:\n" +
    "movl $255,%eax\n" + // Put true in the accumulator if comparison succeeded
    s"$cleanup:\n" +
    "incl %esp\n" // Cleanup the stack
  }

  def buildJump(loc: String): String = {
    s"jmp $loc\n"
  }

  def buildJumpTrue(loc: String): String = {
    "cmpl $255,%eax\n" +
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
