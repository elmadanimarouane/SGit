package sgit.commands

import java.io.File

import api.FileApi

object Init {

  def isSgitDir(directoryName: String): Boolean =
  {
    val directory = new File(directoryName)
    // We check if we have a directory named '.sgit'
    directory.listFiles().map(_.getName).contains(".sgit")
  }

  private[this] def sgitDirBuilder(userPath: String): Unit =
    {
      // We get our current repository
      val currSgitDir = userPath + "/.sgit"
      // We initialize all of our directories that we will need
      val sgitDir = new File(currSgitDir)
      sgitDir.mkdir()
      // Objects store
      val objectsDir = new File(s"$currSgitDir/objects")
      objectsDir.mkdir()
      // Blob store
      val blobDir = new File(s"$currSgitDir/objects/blobs")
      blobDir.mkdir()
      // Tree store
      val treeDir = new File(s"$currSgitDir/objects/trees")
      treeDir.mkdir()
      // Commit store
      val commitDir = new File(s"$currSgitDir/objects/commits")
      commitDir.mkdir()
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
      // Index file
      val indexFile = new File(s"$currSgitDir/index")
      indexFile.createNewFile()
      // Log directory
      val logDir = new File(s"$currSgitDir/log")
      logDir.mkdir()
      // Log file
      val logFile = new File(s"$currSgitDir/log/log")
      logFile.createNewFile()
    }

  def initSgitDir(userPath: String): Unit =
  {
    if(isSgitDir(userPath))
      {
        println("This repo is already a Sgit repo")
      }
    else
      {
        // Creating our .sgit directory
        sgitDirBuilder(userPath)
        println("Initialized empty SGit repository in " + userPath + "/.sgit")
      }
  }
}
