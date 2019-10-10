package main.sgit.commands

import java.io.File

import main.api.FileApi

import scala.io.Source

object add {

  // This method allows us to add a single file
  def add(file: File): Unit =
  {
    // We first convert our file into a SHA string
    val shaValue = FileApi.encodeSha(file)

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
        try
          {
            // We get all the content of our file added
            val bufferFile = Source.fromFile(file)
            for(line <- bufferFile.getLines())
              {
                // We copy it in our object file
                FileApi.utilWriter(newObjectFile.getPath,line)
              }
            // We close our buffer
            bufferFile.close()
          }

        val indexPath = System.getProperty("user.dir") + "/.sgit/index"
        // We check our index file if our path is not already in it (in the case of adding a file that we modified)
        val pathsStoredInIndex = FileApi.listFromFile(indexPath)
        if(!pathsStoredInIndex.contains(file))
          {
            // We write the path of our new file and its SHA in our index file
            FileApi.utilWriter(indexPath, shaValue + " " + file.getPath)
          }
      }
    else
      {
        println("This file was already created yo !")
      }
  }

  // This method allows us to add all the file of our project (excluding our .sgit directory)
  def addAll(): Unit =
    {
      val allFiles = FileApi.getFilesAllDir(System.getProperty("user.dir"))
      allFiles.map(file => add(file))
    }
}
