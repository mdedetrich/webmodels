package org.mdedetrich.webmodels

private[webmodels] object Platform {

  /** Logic taken for figuring out fast way to calculate first digit
    * of Int taken using OldCurmudgeon's method is
    * taken from https://stackoverflow.com/a/18054242. This uses
    */

  private val limits: Array[Int] = Array[Int](
    2000000000,
    Integer.MAX_VALUE,
    200000000,
    300000000 - 1,
    20000000,
    30000000 - 1,
    2000000,
    3000000 - 1,
    200000,
    300000 - 1,
    20000,
    30000 - 1,
    2000,
    3000 - 1,
    200,
    300 - 1,
    20,
    30 - 1,
    2,
    3 - 1
  )

  def checkFirstDigitOfInt(digit: Int, value: Int): Boolean = {
    var i = 0
    while (i < limits.length) {
      if (value > limits(i + 1)) return false
      if (value >= limits(i)) return true

      i += digit
    }
    false
  }

}
