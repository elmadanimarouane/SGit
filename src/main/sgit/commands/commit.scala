package main.sgit.commands

import java.io.File

import main.api.{CustomHasher, FileApi, ObjectApi, SgitApi}
import main.sgit.objects.{Blob, Tree}

case class commit (shaValue: String, associatedTree: Tree, commitMessage: String = null, subCommit: commit = null)

object commit {

  def commit(commitMessage: String = null): Unit =
    {
      val pathProject = System.getProperty("user.dir")
      // We get the path of our index
      val pathIndex = pathProject + "/.sgit/index"
      // We get our full list of tracked files
      val fullIndexList = FileApi.listFromFile(pathIndex,0)
      // We create a list of blobs of our index
      val listOfBlobs = fullIndexList.map(x => new Blob(new File(x.substring(41)), x.substring(0,40)))
      // We create a SHA value out of our list of blobs
      val shaTree = CustomHasher.hashObjectIntoSha1(listOfBlobs)
      // We create our tree
      val commitTree = Tree.createTree(shaTree,listOfBlobs)
      // We get the SHA of our commit thanks to our tree
      val shaCommit = CustomHasher.hashObjectIntoSha1(commitTree)
      // We check first if it is our first commit
      val pathCommits = pathProject + "/.sgit/objects/commits"
      if(FileApi.getSubDir(new File(pathCommits)).isEmpty)
        {
          // We create our commit without giving him a previous commit
          createCommit(shaCommit, commitTree, commitMessage)
        }
      else
        {
          // We create our commit and we give it as sub commit the previous commit made
          createCommit(shaCommit,commitTree,commitMessage, SgitApi.getCurrentCommit)
        }
      // We update our ref file so that it points toward our new commit
      SgitApi.updateRef(shaCommit)
    }

  def createCommit(shaValue: String, associatedTree: Tree, commitMessage: String = null, shaSubCommit: String = null): Unit =
  {
    // We create our commit directory and get the path of our commit file
    val commitFile = ObjectApi.CreateObject("commits", shaValue)
    // We write the sha of our associated tree in it
    FileApi.utilWriter(commitFile, "tree " + associatedTree.sha)
    // We write our commit message if one was given
    if(commitMessage != null)
      {
        FileApi.utilWriter(commitFile, commitMessage)
      }
      // Else, we simply write the number of the commit
    else
      {
        // We get the list of our commits directories
        val commitsDir = FileApi.getSubDir(new File(System.getProperty("user.dir") + "/.sgit/objects/commits"))
        // We write our default commit message
        FileApi.utilWriter(commitFile, "Commit number " + (commitsDir.size))
      }
    // If we have a sub commit, we add it aswell in our file
    if(shaSubCommit != null)
      {
        FileApi.utilWriter(commitFile, "subcommit " + shaSubCommit)
      }
    // We then need to put in our ref file the sha of our new commit
    SgitApi.updateRef(shaValue)
  }
}
