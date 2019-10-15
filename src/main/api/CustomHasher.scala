package main.api

import java.io.{ByteArrayOutputStream, ObjectOutputStream}
import java.security.MessageDigest

object CustomHasher {

  // This method allow us to serialize any type of object into an array of bytes. It allows us to later convert it
  // into a SHA value
  def byteConverter(objectToSerialize: Any): Array[Byte] =
    {
      val stream = new ByteArrayOutputStream()
      val oos = new ObjectOutputStream(stream)
      oos.writeObject(objectToSerialize)
      oos.close()
      stream.toByteArray
    }


  def digest(bytes: Array[Byte], md: MessageDigest): String = {
    md.update(bytes)
    hexify(md.digest)
  }

  def hexify(bytes: Array[Byte]): String = {
    val builder = new java.lang.StringBuilder(bytes.length * 2)
    val hex = "0123456789ABCDEF"
    bytes.foreach { byte => builder.append(hex.charAt((byte & 0xF0) >> 4)).append(hex.charAt(byte & 0xF)) }
    builder.toString
  }

  def hashObjectIntoSha1(objectToHash: Object): String =
    {
      // We convert our object into some bytes
      val objectInBytes = byteConverter(objectToHash)
      // We digest it into a SHA1 value
      digest(objectInBytes, MessageDigest.getInstance("SHA1"))
    }

}
