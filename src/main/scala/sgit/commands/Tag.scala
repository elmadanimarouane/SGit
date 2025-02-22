package sgit.commands

import java.io.File

import api.{FileApi, SgitApi}

object Tag {

  // This method allows us to create a tag
  def tag(tagName: String, userPath: String): Unit =
    {
      // We get our current branch file (located in refs/heads)
      val currentBranch = SgitApi.getBranchFile(userPath)
      // We check if we have already done a commit before or not
      if(Commit.getCommits(userPath).nonEmpty)
        {
          // If it is the case, we get the last commit made in the branch
          val currentShaCommit = FileApi.listFromFile(currentBranch.getPath,0)
          // Now that we have our commit, we can create our tag
          val tagFile = new File(userPath + s"/.sgit/refs/tags/$tagName")
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
  def getTags(userPath: String): List[File] =
    {
      // We get the path of our tags
      val pathTags = userPath + "/.sgit/refs/tags"
      // We get all of our tags
      FileApi.getFilesSingleDir(pathTags)
    }
}
