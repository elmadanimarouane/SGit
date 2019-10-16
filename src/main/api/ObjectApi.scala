package main.api

import java.io.File

object ObjectApi {
  def CreateObject(typeOfObject: String, shaValue: String): String =
    {
      // We get the path of our object directory
      val projectPath = System.getProperty("user.dir") + s"/.sgit/objects/$typeOfObject"
      val objectPath = new File(projectPath + "/" + shaValue.substring(0,2))
      objectPath.mkdir()
      // We create our object file
      val objectFile = new File(objectPath.getPath + "/" +  shaValue.substring(2))
      objectFile.createNewFile()
      // We return the path of our object for further use
      objectFile.getPath
    }
}
