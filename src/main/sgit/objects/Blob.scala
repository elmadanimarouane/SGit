package main.sgit.objects

import java.io.{File, FileInputStream}
import java.security.{DigestInputStream, MessageDigest}

import javax.xml.bind.DatatypeConverter

// A blob is what allows us to store the content of each file. It keeps the content of the file and generate a SHA
// of it so we can check later if the file was modified or not
case class Blob(content: String, sha: String)

object Blob{
  // This method allows us to encode a full file in a SHA string
  def encodeSha(file: File): String =
    {
      // We choose to set our digester to SHA1
      val messageDigest = MessageDigest.getInstance("SHA1");
      // We affect to our buffer a size of 8192 which allows us to not keep a lot of bytes in our memory, but still
      // be quite fast (good ratio speed/memory consumption)
      val buffer = new Array[Byte](8192)

      // We get our file
      val dis = new DigestInputStream(new FileInputStream(file), messageDigest)
      try
        {
          // While we didn't find the end of the file, we keep reading the buffer
          while (dis.read(buffer) != -1)
          {

          }
        }
        // When we are done, we close the digester
      finally
      {
        dis.close()
      }
      // We convert our result in a string and return it
      messageDigest.digest.map("%02x".format(_)).mkString
    }
}

