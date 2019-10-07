package main.sgit.commands

import java.io.{File, FileOutputStream, PrintWriter}
import java.nio.file.Files

import scala.io.Source
import main.sgit.objects.Blob

object status {

  def add(file: File): Unit =
  {
    // We first convert our file into a SHA string
    val shaValue = Blob.encodeSha(file)

    // We take only the first two characters to know if the add was already done
    val shaValueFirstTwo = shaValue.substring(0,2)

    // We check if a directory with the same two char of our SHA exist. If it is the case, we don't do something. Else,
    // We initiate the add process
    val projectDir = System.getProperty("user.dir") + "/.sgit/objects"
    val objectDir = new File(projectDir)
    if(!objectDir.listFiles().map(_.getName).contains(shaValueFirstTwo))
      {
        // We take the rest of our SHA to create our content file later
        val restShaValue = shaValue.substring(3,shaValue.length)
        // We initialize our new repository to store the content
        val newObjectPath = projectDir + s"/$shaValueFirstTwo"
        val newObjectDir = new File(newObjectPath)
        newObjectDir.mkdir()
        // We then create our SHA file content
        val newObjectFile = new File(s"$newObjectPath/$restShaValue")
        newObjectFile.createNewFile()
        // After that, we write our content it in our newly created file
        val writer = new PrintWriter(newObjectFile)
        try
          {
            // We get all the content of our file added
            val bufferFile = Source.fromFile(file)
            for(line <- bufferFile.getLines())
              {
                // We copy it in our object file
                writer.write(line + "\n")
              }
            // We close our buffer
            bufferFile.close()
          }
        // We close our Writer
        writer.close()
      }
    else
      {
        println("This file was already created yo !")
      }
  }

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
