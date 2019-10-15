package main.sgit.commands

import java.io.File

import main.api.{FileApi, SgitApi}

object tag {

  // This method allows us to create a tag
  def tag(tagName: String): Unit =
    {
      // We get our current branch file (located in refs/heads)
      val currentBranch = SgitApi.getBranchFile
      // We check if we have already done a commit before or not
      if(currentBranch.isFile)
        {
          // If it is the case, we get the last commit made in the branch
          val currentShaCommit = FileApi.listFromFile(currentBranch.getPath,0)
          // Now that we have our commit, we can create our tag
          val tagFile = new File(System.getProperty("user.dir") + s"/.sgit/refs/tags/$tagName")
          tagFile.createNewFile()
          // We write in it our current commit
          FileApi.utilWriter(tagFile.getPath,currentShaCommit.head)
        }
      else
        {
          // If we don't have any commit on our branch, we ask the user to make a commit before trying to create a tag
          println("No commit made on this branch. Please make at least one commit before trying to create a tag")
        }
    }

  // This method allows us to get all of our tags
  def getTags: List[File] =
    {
      // We get the path of our tags
      val pathTags = System.getProperty("user.dir") + "/.sgit/refs/tags"
      // We get all of our tags
      FileApi.getFilesSingleDir(pathTags)
    }
}
