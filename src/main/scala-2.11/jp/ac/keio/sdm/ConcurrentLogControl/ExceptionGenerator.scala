/* Copyright (c) 2017 Ryuichi Saito, Keio University. All right reserved. */
package jp.ac.keio.sdm.ConcurrentLogControl

import scala.math._

class ExceptionGenerator {

  def callException() {
    def randomInt(n: Double): Int = floor(random * n).toInt
    randomInt(11) % 2 match {
      case 1 => throw new IllegalArgumentException
      case 0 => throw new IndexOutOfBoundsException
      case 0.5 => throw new ClassCastException
    }
  }

}
