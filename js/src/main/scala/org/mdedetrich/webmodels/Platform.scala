package org.mdedetrich.webmodels

private[webmodels] object Platform {

  /** Note that this only works for non negative int's but since we are using it for HTTP codes it should
    * be fine
    * @param digit
    * @param value
    * @return
    */
  def checkFirstDigitOfInt(digit: Int, value: Int): Boolean = {
    var x = value

    while (x > 9) x /= 10
    x == digit
  }

}
