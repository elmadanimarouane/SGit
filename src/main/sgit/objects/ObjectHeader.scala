package main.sgit.objects

import main.sgit.objects.ObjectType.ObjectType

case class ObjectHeader(typeObject: ObjectType, sizeObject: Int = 0)

object ObjectHeader {
  // We simply make a String containing our type, a space, our size and an empty
  def encode(header: ObjectHeader): Seq[Byte] = {

    val result = Vector.newBuilder[Byte]

    result.addOne((header.typeObject + " ").toByte)
    result.addOne(header.sizeObject.toByte)
    result.addOne("\u0000".toByte)

    result.result()
  }

  def decode(headerString: String): ObjectHeader = {
    // We get the header then we convert it to an ObjectType
    val typeHeaderString = headerString.substring(0, headerString.indexOf(" "))
    val typeHeaderObject = ObjectType.withName(new String(typeHeaderString))
    // We get the size then we convert it to an Int
    val sizeHeaderString = headerString.substring(headerString.indexOf(" ") + 1, headerString.indexOf("\u0000"))
    val sizeHeaderInt = sizeHeaderString.toInt
    // We then recreate an ObjectHeader
    ObjectHeader(typeHeaderObject, sizeHeaderInt)
  }
}