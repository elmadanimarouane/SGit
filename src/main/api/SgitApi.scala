package main.api

import java.io.File

import main.api.treeApi.TreeApi
import main.sgit.objects.Blob

object SgitApi {

  // This method allows us to get all of our modified files
  def modifiedFiles(customDir: String = ""): Iterable[(Blob, Boolean)] =
  {
    // We get the files that we kept in our index file
    val listOfKeptFiles = FileApi.getListOfKeptFiles(customDir)
    // We get the path of our project
    val pathProject = System.getProperty("user.dir") + customDir
    // We get all of our files. If our list of files is empty, it means that we don't have any sub directory.
    // We should then get all of our files in our directory
    val listOfFiles = if(FileApi.getFilesAllDir(pathProject).isEmpty) FileApi.getAllFilesFromSingleDir(new File(pathProject))
    else FileApi.getFilesAllDir(pathProject)
    // We filter the tracked files by excluding from our list of all files the files that are not tracked
    val listOfTrackedFiles = listOfFiles.filter(listOfKeptFiles)
    // We convert our list of tracked files into lists of SHA1 and we keep only the first two char
    val listOfTrackedSha = listOfTrackedFiles.map(file => Blob(file, FileApi.encodeSha(file).substring(0,2)))
    // We get all of our directories from our "objects" folder
    val listDirOfObjects = FileApi.getSubDir(new File(pathProject + "/.sgit/objects/blobs"))
    // We keep only the files that have been modified (and therefore, we keep only the files that have a SHA not
    // kept in our object directory)
    val listOfModifiedFilesBoolean = listOfTrackedSha.map(x => listDirOfObjects.map(_.getName).contains(x.sha))
    listOfTrackedSha.zip(listOfModifiedFilesBoolean).filter(x => !x._2)
  }

  // This method allows us to get our branch file
  def getBranchFile(customDir: String = ""): File =
    {
      // We get the path of our project
      val projectPath = System.getProperty("user.dir") + customDir + "/.sgit/"
      // We first retrieve in which branch we are on
      val actualBranch = FileApi.listFromFile(projectPath + "HEAD", 5).head
      // We check if our branch exist. If it is not the case, we create it
      val branchFile = new File(projectPath + actualBranch)
      if(!branchFile.isFile) branchFile.createNewFile()
      // We get the directory of our branch and initiate a file with it
      branchFile
    }

  // This method allows us to update our ref file pointing to the last commit
  def updateRef(sha: String, customDir: String = ""): Unit =
    {
      // We get our branch file
      val branchFile = getBranchFile(customDir)
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
  def getCurrentCommit(customDir: String = ""): List[String] =
    {
      // We get our branch file
      val branchFile = getBranchFile(customDir)
      // We get the SHA stored in it and return it
      FileApi.listFromFile(branchFile.getPath, 0)
    }

  // This method allows us to change our branch
  def changeBranch(branchName: String, customDir: String = ""): Unit =
    {
      // We get our HEAD file
      val headFile = new File(System.getProperty("user.dir") + customDir + "/.sgit/HEAD")
      // We create a temporary file where we write our new pointer to our branch
      val tmpFile = new File("/tmp/tempHEAD")
      FileApi.utilWriter(tmpFile.getPath, s"ref: refs/heads/$branchName")
      // We actualize our HEAD file
      tmpFile.renameTo(headFile)
    }

  // This method allows us to get the file of a commit based on its sha
  def getCommitBySha(sha: String, customDir: String = ""): File =
  {
    // We get the path of our commit
    val commitPath = (System.getProperty("user.dir") + customDir + "/.sgit/objects/commits/" + sha.substring(0,2)
      + "/" + sha.substring(2))
    // We return the file
    new File(commitPath)
  }

  // This method allows us to compare two file by giving as attribute the list of each line of our two files
  def diffBetweenTwoCommits(firstCommit: String, secondCommit: String): Unit =
  {
    // We get the trees of our commits
    val firstTree = FileApi.listFromFile(getCommitBySha(firstCommit).getPath,0).head.substring(5)
    val secondTree = FileApi.listFromFile(getCommitBySha(secondCommit).getPath,0).head.substring(5)
    // We get the blobs stored in our trees
    val firstBlobsList = FileApi.listFromFile(TreeApi.getTreeFile(firstTree).getPath,5)
    val secondBlobsList = FileApi.listFromFile(TreeApi.getTreeFile(secondTree).getPath,5)
    // We create blobs from our two lists
    val firstBlobs = firstBlobsList.map(x => convertTreeContentToBlob(x))
    val secondBlobs = secondBlobsList.map(x => convertTreeContentToBlob(x))
    // We get our modified Blobs (with the same file but without the same sha)
    val modifiedBlobs = (firstBlobs.filter(blob => secondBlobs.map(x => x.content).contains(blob.content)
      && !secondBlobs.map(x => x.sha).contains(blob.sha)) ::: secondBlobs.filter(blob =>
      firstBlobs.map(x => x.content).contains(blob.content) && !firstBlobs.map(x => x.sha).contains(blob.sha))).groupBy(_.content)
    // Now that we have our two blobs
    println(modifiedBlobs)
  }

  def convertTreeContentToBlob(treeContent: String): Blob =
    {
      Blob(new File(treeContent.substring(41)), treeContent.substring(0,40))
    }
}
