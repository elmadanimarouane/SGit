package main.sgit.commands

import java.io.File

import main.api.FileApi

import scala.io.Source

object add {

  // This method allows us to add a single file
  def add(file: File, customDir: String = ""): Unit =
  {
    // We first convert our file into a SHA string
    val shaValue = FileApi.encodeSha(file)

    // We take only the first two characters to know if the add was already done
    val shaValueFirstTwo = shaValue.substring(0,2)

    // We check if a directory with the same two char of our SHA exist. If it is the case, we don't do something. Else,
    // We initiate the add process
    val projectDir = System.getProperty("user.dir") + customDir + "/.sgit/objects/blobs"
    val objectDir = new File(projectDir)
    if(!objectDir.listFiles().map(_.getName).contains(shaValueFirstTwo))
      {
        // We take the rest of our SHA to create our content file later
        val restShaValue = shaValue.substring(2)
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
      }

    val indexPath = System.getProperty("user.dir") + customDir + "/.sgit/index"
    // We check our index file if our path is not already in it (in the case of adding a file that we modified)
    val pathsStoredInIndex = FileApi.listFromFile(indexPath, 41)
    // We convert our list of string into a list of file
    if(!pathsStoredInIndex.contains(file.getPath))
    {
      // We write the path of our new file and its SHA in our index file
      FileApi.utilWriter(indexPath, shaValue + " " + file.getPath)
    }
    // If the path of our file is already stored in our index file, we check if we want to add a new version
    // of our file
    else
    {
      // We create a source from our index file
      val indexSource = Source.fromFile(new File(indexPath))
      // We create a temporary new file
      val tempFile = new File("/tmp/tempIndex.txt")
      // We check if the SHA is already registered in our index file. If it is not the case, we modify it
      indexSource.getLines().map {
        x =>
          if (x.contains(file.getPath) && !x.contains(shaValue)) {
            shaValue + " " + file.getPath
          }
          else {
            x
          }
      }
        .foreach(x => FileApi.utilWriter(tempFile.getPath,x))
      // We close our source from the index
      indexSource.close()
      // We rename our temp file as our new index file
      tempFile.renameTo(new File(indexPath))
    }
  }

  // This method allows us to add all the file of our project (excluding our .sgit directory)
  def addAll(customDir: String = ""): Unit =
    {
      val allFiles = FileApi.getFilesAllDir(System.getProperty("user.dir") + customDir)
      allFiles.foreach(file => add(file))
    }
}
