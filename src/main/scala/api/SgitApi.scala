package api

import java.io.File

import api.treeApi.TreeApi
import sgit.objects.Blob

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
      val actualBranch = FileApi.listFromFile(projectPath + "HEAD", 5).headOption
      // We check if our branch exist. If it is not the case, we create it
      val branchFile = new File(projectPath + actualBranch.getOrElse())
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

  // This method allows us to get the file of a blob based on its sha
  def getBlobBySha(sha: String, customDir: String = ""): File =
    {
      val blobPath = (System.getProperty("user.dir") + customDir + "/.sgit/objects/blobs" + "/" + sha.substring(0,2)
        + "/" + sha.substring(2))
      new File(blobPath)
    }

  // This method allows us to compare a commit with its subcommit
  def diffBetweenCommits(commitSha: String): Unit =
  {
    println("commit " + commitSha)
    // We get the file of our commit
    val commitFile = getCommitBySha(commitSha)
    // We get the content
    val commitContent = FileApi.listFromFile(commitFile.getPath,0)
    // We get the content of our blobs from our commit
    val commitTree = FileApi.listFromFile(commitFile.getPath,0).head.substring(5)
    val commitTreeContent = FileApi.listFromFile(TreeApi.getTreeFile(commitTree).getPath,5)
    // We check if we have a subcommit
    if(commitContent.reverse.head.contains("subcommit"))
      {
        // If it is the case, we get our subcommit
        val subCommitSha = commitContent.reverse.head.substring(10)
        // We get the content of our commits (the sha of the tree associated to our commit)
        val subCommitTree = FileApi.listFromFile(getCommitBySha(subCommitSha).getPath,0).head.substring(5)
        // We get the content of our trees (the sha and the file path)
        val subCommitTreeContent = FileApi.listFromFile(TreeApi.getTreeFile(subCommitTree).getPath,5)
        // We check if we haven't added a new file
        val newFiles = commitTreeContent.map(x => x.substring(41)).filterNot(y =>
          subCommitTreeContent.map(z => z.substring(41)).contains(y))
        if(newFiles.nonEmpty)
          {
            // We get its sha by filtering our commitTreeContent
            val newFilesAndSha = commitTreeContent.filter(x => newFiles.contains(x.substring(41)))
            // We get the content of the blob of the sha gathered earlier
            val newFilesAndContent = newFilesAndSha.map(x =>
              (x.substring(41), FileApi.listFromFile(getBlobBySha(x.substring(0,40)).getPath,0)))
            // We print its content
            printAddedFiles(newFilesAndContent)
          }
        // We filter our two contents so we can get the files with the same path but not the same sha
        val modifiedFilesList = for {
          firstTree <- commitTreeContent
          secondTree <- subCommitTreeContent
          if firstTree.contains(secondTree.substring(41)) && !firstTree.contains(secondTree.substring(0,40))
        } yield (firstTree.substring(41), firstTree.substring(0,40), secondTree.substring(0,40))
        // We get the datas
        val finalData = modifiedFilesList.map(x => FileApi.listFromFile(getBlobBySha(x._2).getPath,0))
        val initialData = modifiedFilesList.map(x => FileApi.listFromFile(getBlobBySha(x._3).getPath,0))
        // We combine the two data with the name of our file
        val fullCombinedList = (modifiedFilesList.map(x => x._1), initialData, finalData).zipped.toList
        // We print the differences
        for(x <- fullCombinedList)
          {
            println("Modification made in file " + x._1.replace(System.getProperty("user.dir"),""))
            diffBetweenTwoContent(x._2, x._3)
            println("")
          }
      }
      // Else, we simply print the new content as addition of line
    else
      {
        val newFileContent = commitTreeContent.map(x => FileApi.listFromFile(getBlobBySha(x.substring(0,40)).getPath,0))
        val newFileName = commitTreeContent.map(x => x.substring(41))
        // We fuse the name with the content
        val combinedNameAndContent = (newFileName, newFileContent).zipped.toList
        // We print the addition of lines
        printAddedFiles(combinedNameAndContent)
      }
  }

  // This method allows us to get the differences between two content, represented as list of strings
  def diffBetweenTwoContent(initialContent: List[String], finalContent: List[String]): Unit =
    {
      // We fuse the two lists
      val combinedList = finalContent.zipAll(initialContent,"","")
      // We compare each line
      for(line <- combinedList)
      {
        if(line._1 == line._2)
        {
          println("   " + line._1)
        }
        else
        {
          // We check if our new file is smaller than our previous, which means some lines were removed
          if(initialContent.size >= finalContent.size)
          {
            // If our new line is not in the old file
            if((combinedList.reverse.head._1 != line._1 || !line._1.isEmpty) && !initialContent.contains(line._1))
            {
              // This means the line was added
              println(Console.GREEN + " + " + line._1)
              print(Console.WHITE)
            }
            // If our old line is not in the new file
            if(!finalContent.contains(line._2))
            {
              // This means the line was removed
              println(Console.RED + " - " + line._2)
              print(Console.WHITE)
            }
            // If our new line is in the old file
            else
            {
              // This means the line was simply moved
              println("   " + line._2)
            }
          }
          //Otherwise, this means that some lines were added
          else
          {
            // If our new line is not in the old file
            if(!initialContent.contains(line._1))
            {
              // This means the line was added
              println(Console.GREEN + " + " + line._1)
              print(Console.WHITE)
            }
            // If our old line is not in the new file
            if((combinedList.reverse.head._2 != line._2 || !line._2.isEmpty) && !finalContent.contains(line._2))
            {
              // This means the line was removed
              println(Console.RED + " - " + line._2)
              print(Console.WHITE)
            }
            // If our new line is in the old file
            if(initialContent.contains(line._1))
            {
              // This means the line was simply moved
              println("   " + line._1)
            }
          }
        }
      }
    }

  def printAddedFiles(addedFiles: Iterable[(String, List[String])]): Unit =
    {
      for(x <- addedFiles)
      {
        println("Added file " + x._1.replace(System.getProperty("user.dir"),""))
        x._2.foreach(line => println(Console.GREEN + " + " + line))
        println(Console.WHITE)
      }
    }
}
