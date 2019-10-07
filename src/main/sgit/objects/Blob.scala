package main.sgit.objects

import java.io.{File, FileInputStream}
import java.security.{DigestInputStream, MessageDigest}

import javax.xml.bind.DatatypeConverter

import scala.io.Source

// A blob is what allows us to store the content of each file. It keeps the content of the file and generate a SHA
// of it so we can check later if the file was modified or not
case class Blob(content: String, sha: String)

object Blob{

  def readContentBlob(file: File): Blob =
  {
    val source = Source.fromFile(file)
    try
      {
        Blob(source.mkString, "test")
      }
    finally
    {
      source.close()
    }
  }
}

