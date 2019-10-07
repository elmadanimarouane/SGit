package main.sgit

import java.io.File

import main.api.FileApi
import main.sgit.commands.status
import main.sgit.objects.Blob

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
    status.add(new File("/home/marouane/Desktop/IG5/Sgit/src/main/sgit/test.txt"))
    val blobTest = status.readContentBlob(new File("/home/marouane/Desktop/IG5/Sgit/.sgit/objects/40/0b114b3e48bd8b44ba48c026b4356b566dfc3"))
    println(blobTest.content)
  }

}
