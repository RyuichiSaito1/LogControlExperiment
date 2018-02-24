package jp.ac.keio.sdm.ConcurrentLogControl

import java.lang.Character.UnicodeBlock

import scala.collection.mutable

class Validator {

  def isChackWordLengh(word: String): Boolean = {
    if (word.length < 5) {
      true
    } else {
      false
    }
  }

  def isJpananesUnicodeBlock(word: String): Boolean = {
    val japaneseUnicodeBlock = new mutable.HashSet[UnicodeBlock]()
      .+=(UnicodeBlock.HIRAGANA, UnicodeBlock.HIRAGANA, UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS)
    val charArray = word.toCharArray
    charArray.foreach(s =>
      if (japaneseUnicodeBlock.contains(UnicodeBlock.of(s))) {
        true
      }
    )
    false
  }

  def isExistsHashTag(word: String): Boolean = {
    if (!word.startsWith("#")) {
      true
    }
    false
  }
}
final case class UnicodeBlockException(private val message: String = "",
                                       private val cause: Throwable = None.orNull)extends Exception(message, cause)
final case class HashTagException(private val message: String = "",
                                  private val cause: Throwable = None.orNull)extends Exception(message, cause)
final case class WordLengthException(private val message: String = "",
                                      private val cause: Throwable = None.orNull)extends Exception(message, cause)