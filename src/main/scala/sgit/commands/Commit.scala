package sgit.commands

import java.io.File

import api._
import sgit.objects.{Blob, Tree}

object Commit {

  def commit(commitMessage: Option[String] = None, userPath: String): Unit =
    {
      // We get the path of our index
      val pathIndex = userPath + "/.sgit/index"
      // We get our full list of tracked files
      val fullIndexList = FileApi.listFromFile(pathIndex,0)
      // We create a list of blobs of our index
      val listOfBlobs = fullIndexList.map(x => Blob(new File(x.substring(41)), x.substring(0,40)))
      // We create a SHA value out of our list of blobs
      val shaTree = CustomHasher.hashObjectIntoSha1(listOfBlobs)
      // We create our tree
      val commitTree = Tree.createTree(shaTree,listOfBlobs,userPath)
      // We get the SHA of our commit thanks to our tree
      val shaCommit = CustomHasher.hashObjectIntoSha1(commitTree)
      // We check first if it is our first commit
      if(SgitApi.getCurrentCommit(userPath).isEmpty)
        {
          // We create our commit without giving him a previous commit
          createCommit(shaCommit, commitTree, commitMessage,userPath = userPath)
        }
      else
        {
          // We get our list of commits
          val listOfCommits = getCommits(userPath)
          // We make sure that the same commit wasn't done before
          if(!listOfCommits.contains(shaCommit))
            {
              // We create our commit and we give it as sub commit the previous commit made
              createCommit(shaCommit,commitTree,commitMessage, Some(SgitApi.getCurrentCommit(userPath).head), userPath = userPath)
            }
          else
            {
              println("The same commit was already made")
            }
        }
      // We update our ref file so that it points toward our new commit
      SgitApi.updateRef(shaCommit, userPath)
    }

  def createCommit(shaValue: String, associatedTree: Tree, commitMessage: Option[String] = None,
                   shaSubCommit: Option[String] = None, userPath: String): Unit =
  {
    // We create our commit directory and get the path of our commit file
    val commitFile = ObjectApi.createObject("commits", shaValue, userPath)
    // We write the sha of our associated tree in it
    FileApi.utilWriter(commitFile, "tree " + associatedTree.sha)
    // We get the name of our committer
    val committerName = System.getenv("USER")
    FileApi.utilWriter(commitFile, "author " + committerName)
    // We write the current date so we can get back later the date of the commit in our log
    FileApi.utilWriter(commitFile, TimeApi.getDate + "\n")
    // We get the list of our commits directories
    val commitsDir = FileApi.getSubDir(new File(userPath + "/.sgit/objects/commits"))
    // We write our commit message if one was given
    commitMessage match {
      case Some(commitMessage) => FileApi.utilWriter(commitFile, commitMessage)
      // Else, we simply write the number of the commit
      case None => FileApi.utilWriter(commitFile, "Commit number " + commitsDir.size)
    }
    // If we have a sub commit, we add it aswell in our file
    shaSubCommit match {
      case Some(sha) => FileApi.utilWriter(commitFile, "subcommit " + sha)
      case None =>
    }
    // We then need to put in our ref file the sha of our new commit
    SgitApi.updateRef(shaValue,userPath)
    // We add our commit to our log
    Log.createLog(shaValue,
      commitMessage match {
        case Some(message) => message
        case None => "Commit number " + commitsDir.size
      }, committerName, shaSubCommit.getOrElse("0"*40), userPath)
  }

  // This method allows us, with a sha, to retrieve the name of the commit associated with it
  def getCommitName(sha: String, userPath: String): String =
    {
      // We get the path of our commit
      val commitPath = userPath + "/.sgit/objects/commits/" + sha.substring(0,2) + "/" +
      sha.substring(2)
      // We read the content of it and convert our list to a vector since it is faster to index it, which we will do
      // later
      val commitFile = FileApi.listFromFile(commitPath,0).toVector
      // We return the 5th line which correspond to our commit message and remove the tab
      commitFile(4)
    }

  // This method allows us to get all of our commits
  def getCommits(userPath: String): List[String] =
    {
      // We get the path of our commits
      val commitsPath = userPath + "/.sgit/objects/commits"
      // We get all of our directories in our commit directory
      val commitsDir = FileApi.getSubDir(new File(commitsPath))
      // We then get all of our commits
      commitsDir.map(x => x.getName + FileApi.getFilesSingleDir(x.getPath).headOption.getOrElse(
        throw new RuntimeException("Error: Impossible to get a commit sha. Empty line encountered")
      ).getName)
    }
}
