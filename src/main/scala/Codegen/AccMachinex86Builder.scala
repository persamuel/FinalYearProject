package Codegen

object AccMachinex86Builder {
  def buildNOP(): String = {
    "nop\n"
  }

  def buildPOP(): String = {
    "popl %eax\n"
  }

  def buildPUSH(): String = {
    "pushl %eax\n"
  }

  def buildLOAD(loc: String): String = {
    s"movl $loc,%eax\n"
  }

  def buildLOADIMM(value: String): String = {
    s"movl $$($value),%eax\n"
  }

  def buildSTORE(loc: String): String = {
    s"movl %eax,$loc\n"
  }

  def buildCOMPGT(): String = {
    ""
  }

  def buildCOMPGTE(): String = {
    ""
  }

  def buildCOMPLT(): String = {
    ""
  }

  def buildCOMPLTE(): String = {
    ""
  }

  def buildCOMPEQ(): String = {
    ""
  }

  def buildCOMPNEQ(): String = {
    ""
  }

  def buildJUMP(loc: String): String = {
    ""
  }

  def buildJUMPTRUE(loc: String): String = {
    ""
  }

  def buildPLUS(): String = {
    ""
  }

  def buildTIMES(): String = {
    ""
  }

  def buildMINUS(): String = {
    ""
  }

  /*def buildDIVIDE(): String = {
    ""
  }*/

  def buildNEGATE(): String = {
    ""
  }
}
