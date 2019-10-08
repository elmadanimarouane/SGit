package main.sgit

import java.io.File

import main.api.FileApi

object Init {

  private def isSgitDir(directoryName: String): Boolean =
  {
    val directory = new File(directoryName)
    // We check if we have a directory named '.sgit'
    directory.listFiles().map(_.getName).contains(".sgit")
  }

  private def sgitDirBuilder(): Unit =
    {
      // We get our current repository
      val currSgitDir = System.getProperty("user.dir") + "/.sgit"
      // We initialize all of our directories that we will need
      val sgitDir = new File(".sgit")
      sgitDir.mkdir()
      // Objects store
      val objectsDir = new File(s"$currSgitDir/objects")
      objectsDir.mkdir()
      // Reference store
      val refsDir = new File(s"$currSgitDir/refs")
      refsDir.mkdir()
      // Heads directory
      val headsDir = new File(s"$currSgitDir/refs/heads")
      headsDir.mkdir()
      // Tags directory
      val tagsDir = new File(s"$currSgitDir/refs/tags")
      tagsDir.mkdir()
      // HEAD file
      val headFile = new File(s"$currSgitDir/HEAD")
      headFile.createNewFile()
      FileApi.utilWriter(s"$currSgitDir/HEAD","ref: refs/heads/master")
      // Config file
      val configFile = new File(s"$currSgitDir/config")
      configFile.createNewFile()
      FileApi.utilWriter(s"$currSgitDir/config", System.getProperty("user.dir"))
      // Description file
      val descriptionFile = new File(s"$currSgitDir/description")
      descriptionFile.createNewFile()
      FileApi.utilWriter(s"$currSgitDir/description","Unnamed repository; edit this file 'description' to name the repository.")
    }

  private def initSgitDir(): Unit =
  {
    // We get the name of our directory
    val currDir = System.getProperty("user.dir")
    if(isSgitDir(currDir))
      {
        println("This repo is already a Sgit repo")
      }
    else
      {
        // Creating our .sgit directory
        sgitDirBuilder()
        println("Initialized empty SGit repository in " + currDir + "/.sgit")
      }
  }

  def main(args: Array[String]): Unit = {
    println(FileApi.getFilesAllDir(new File(System.getProperty("user.dir"))))
  }

}
