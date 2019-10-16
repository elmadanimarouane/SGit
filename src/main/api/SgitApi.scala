package main.api

import java.io.File

import main.sgit.objects.Blob

object SgitApi {

  // This method allows us to get all of our modified files
  def modifiedFiles(): Iterable[(Blob, Boolean)] =
  {
    // We get the files that we kept in our index file
    val listOfKeptFiles = FileApi.getListOfKeptFiles
    // We get the path of our project
    val pathProject = System.getProperty("user.dir")
    // We get all of our files
    val listOfFiles = FileApi.getFilesAllDir(pathProject)
    // We filter the tracked files by excluding from our list of all files the files that are not tracked
    val listOfTrackedFiles = listOfFiles.filter(listOfKeptFiles)
    // We convert our list of tracked files into lists of SHA1 and we keep only the first two char
    val listOfTrackedSha = listOfTrackedFiles.map(file => new Blob(file, FileApi.encodeSha(file).substring(0,2)))
    // We get all of our directories from our "objects" folder
    val listDirOfObjects = FileApi.getSubDir(new File(pathProject + "/.sgit/objects/blobs"))
    // We keep only the files that have been modified (and therefore, we keep only the files that have a SHA not
    // kept in our object directory)
    val listOfModifiedFilesBoolean = listOfTrackedSha.map(x => listDirOfObjects.map(_.getName).contains(x.sha))
    listOfTrackedSha.zip(listOfModifiedFilesBoolean).filter(x => !x._2)
  }

  // This method allows us to get our branch file
  def getBranchFile: File =
    {
      // We get the path of our project
      val projectPath = System.getProperty("user.dir") + "/.sgit/"
      // We first retrieve in which branch we are on
      val actualBranch = FileApi.listFromFile(projectPath + "HEAD", 5).head
      // We get the directory of our branch and initiate a file with it
      new File(projectPath + actualBranch)
    }

  // This method allows us to update our ref file pointing to the last commit
  def updateRef(sha: String): Unit =
    {
      // We get our branch file
      val branchFile = getBranchFile
      // We check if it exists
      if(branchFile.isFile)
        {
          // If it is the case, we have to change the already stored sha value. To do so, we first create a temp file
          val tempFile = new File("/tmp/tempRef.txt")
          // We write in it our new sha value
          FileApi.utilWriter(tempFile.getPath, sha)
          // We rename our temp file as our ref file
          tempFile.renameTo(branchFile)
        }
      else
        {
          // If it is not the case, we create it and write in it the sha of our commit
          branchFile.createNewFile()
          FileApi.utilWriter(branchFile.getPath, sha)
        }
    }

  // This method allows us to get the SHA value of the last commit done
  def getCurrentCommit: List[String] =
    {
      // We get our branch file
      val branchFile = getBranchFile
      // We get the SHA stored in it and return it
      FileApi.listFromFile(branchFile.getPath, 0)
    }

  // This method allows us to change our branch
  def changeBranch(branchName: String): Unit =
    {
      // We get our HEAD file
      val headFile = new File(System.getProperty("user.dir") + "/.sgit/HEAD")
      // We create a temporary file where we write our new pointer to our branch
      val tmpFile = new File("/tmp/tempHEAD")
      FileApi.utilWriter(tmpFile.getPath, s"ref: refs/heads/$branchName")
      // We actualize our HEAD file
      tmpFile.renameTo(headFile)
    }
}
