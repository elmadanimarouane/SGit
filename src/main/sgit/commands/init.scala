package main.sgit.commands

import java.io.File

import main.api.{FileApi, SgitApi}

object init {

  def isSgitDir(directoryName: String): Boolean =
  {
    val directory = new File(directoryName)
    // We check if we have a directory named '.sgit'
    directory.listFiles().map(_.getName).contains(".sgit")
  }

  private def sgitDirBuilder(customDir: String = ""): Unit =
    {
      // We get our current repository
      val currSgitDir = System.getProperty("user.dir") + customDir + "/.sgit"
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

  def initSgitDir(customDir: String = ""): Unit =
  {
    // We get the name of our directory
    val currDir = System.getProperty("user.dir")
    if(isSgitDir(currDir + customDir))
      {
        println("This repo is already a Sgit repo")
      }
    else
      {
        // Creating our .sgit directory
        sgitDirBuilder(customDir)
        println("Initialized empty SGit repository in " + currDir + customDir + "/.sgit")
      }
  }

  def main(args: Array[String]): Unit = {
    // status.status()
    // initSgitDir()
    // add.add(new File("/home/marouane/Desktop/IG5/Sgit/src/test2.txt"))
    // commit.commit("Third commit")
    // log.log()
    // branch.listBranches()
    // branch.branch("Test branch")
    // tag.tag("First tag")
    // checkout.checkout("master") C17385C11B873CC6A5602AE8830FF4FF77543062
    // diff.diff()
    SgitApi.diffBetweenTwoCommits("4798F9C9F2463DD623EA03ACED56A9EBFECBE348","8A12A75E259BFBAE9A1A8E6F05A116446961616E")
  }

}
