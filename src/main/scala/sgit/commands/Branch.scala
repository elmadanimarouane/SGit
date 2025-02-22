package sgit.commands

import java.io.File

import api.{FileApi, SgitApi}

object Branch {

  // This method allows us to create a new branch
  def branch(nameBranch: String, userPath: String): Unit =
    {
      // We get the path of our heads from our project
      val headsPath = userPath + "/.sgit/refs/heads/"
      // We create a file object of our branch
      val branchObject = new File(headsPath + nameBranch)
      // We check if it exists. If it is the case, we tell to the user that this branch already exists. If not, we
      // create it
      if(branchObject.isFile)
        {
          println("A branch with the same name already exists")
        }
      else
        {
          branchObject.createNewFile()
        }
    }

  // This method allows us to get all of our branches
  def getBranches(userPath: String): List[File] =
    {
      // We get the path of our heads
      val headsPath = userPath + "/.sgit/refs/heads"
      // We get all of our heads files with the name of their branch
      FileApi.getFilesSingleDir(headsPath)
    }

  // This method allows us to print all of our branches
  def listBranches(userPath: String): Unit = getBranches(userPath).foreach(x => printBranch(x, userPath))

  // This method allows us to print a branch
  def printBranch(branchFile: File, userPath: String): Unit =
    {
      // We get our current branch
      val currentBranch = SgitApi.getBranchFile(userPath)
      // If the branch is our actual branch, we add a little "*" before it
      if(branchFile == currentBranch)
        {
          print("* " + branchFile.getName + "\t\t")
        }
      else
        {
          print("  " + branchFile.getName + "\t\t")
        }
      // We get the content of our branch file
      val branchContent = FileApi.listFromFile(branchFile.getPath,0)
      // If it is empty, we don't print anything more. Else, we print the last commit and its name
      if(branchContent.nonEmpty)
        {
          println(branchContent.head.substring(0,7) + " " + Commit.getCommitName(branchContent.head, userPath))
        }
      else
        {
          println()
        }
    }
}
