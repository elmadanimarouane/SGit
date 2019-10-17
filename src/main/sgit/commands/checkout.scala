package main.sgit.commands

import java.io.File

import main.api.{FileApi, SgitApi}

object checkout {

  // This method allows us to switch between commits, branch or tags
  def checkout(paramCheckout: String): Unit =
    {
      // We first need to be sure that the argument is either a branch, tag or commit
      // We first get our list of commits
      val listOfCommits = commit.getCommits()
      // We then get our list of branches
      val listOfBranches = branch.getBranches.map(x => x.getName)
      // We finally get our list of tags
      val listOfTags = tag.getTags.map(x => x.getName)
      // Now that we have the three lists, we can check to what correspond our parameter
      // We start with the commits. We check first if our list of commits is not empty
      if(listOfCommits.nonEmpty)
        {
          // We check if our parameter correspond to a commit
          if(listOfCommits.contains(paramCheckout))
            {
              // Easier case. We simply execute our "doCheckout" method
              doCheckout(paramCheckout)
            }
        }
        // We check if our list of branches is empty
      if(listOfBranches.nonEmpty)
        {
          //We check if our parameter correspond to a branch
          if(listOfBranches.contains(paramCheckout))
            {
              val pathBranch = System.getProperty("user.dir") + s"/.sgit/refs/heads/$paramCheckout"
              // Since a branch can be empty (in the case of a newly created branch), we simply clear our work
              // repository
              if(FileApi.listFromFile(pathBranch,0).isEmpty)
                {
                  FileApi.cleanWorkRepo()
                }
              else
                {
                  doCheckout(FileApi.listFromFile(pathBranch,0).head)
                }
              // We switch our branch
              SgitApi.changeBranch(paramCheckout)
            }
        }
        // We check if our list of tags is empty
      if(listOfTags.nonEmpty)
        {
          // We check if our parameter correspond to a tag
          if(listOfTags.contains(paramCheckout))
            {
              // We get the sha of our commit located in our tag
              val tagPath = System.getProperty("user.dir") + s"/.sgit/refs/tags/$paramCheckout"
              doCheckout(FileApi.listFromFile(tagPath,0).head)
            }
        }
    }

  // This method allows us to recreate our working directory based on the sha of a commit
  def doCheckout(shaValue: String): Unit =
    {
      // We get the path of our project
      val projectPath = System.getProperty("user.dir")
      // We get our sha file
      val shaPath = projectPath+"/.sgit/objects/commits/"+shaValue.substring(0,2)+"/"+shaValue.substring(2)
      // We get our tree stored in our sha. Note : We are forced to do our substring later since our tree contain
      // an empty line
      val treeValue = FileApi.listFromFile(shaPath,0).head.substring(5)
      // We get our tree path
      val treePath = projectPath+"/.sgit/objects/trees/"+treeValue.substring(0,2)+"/"+treeValue.substring(2)
      // We read the whole content of our tree file by removing the "blob" attribute
      val treeContent = FileApi.listFromFile(treePath,5)
      // We separate our tree content into two lists : one containing the shas and one containing the paths
      val shaList = treeContent.map(x => x.substring(0,40))
      val pathList = treeContent.map(x=> new File(x.substring(41)))
      // We have what we need to recreate our index. We can now clear our work directory
      FileApi.cleanWorkRepo()
      // Now that we have a clean work repository, we can recreate the files of our commit. We first create the
      // repositories and then we create the files
      pathList.foreach(x => new File(x.getPath.replace("/" + x.getName,"")).mkdirs())
      pathList.foreach(x => x.createNewFile())
      // Now that we have our files ready, we can rewrite their content in them. We first must get our blob file
      val blobFiles = shaList.map(sha => projectPath + "/.sgit/objects/blobs/" + sha.substring(0,2) +
        "/" + sha.substring(2))
      // We now get their content
      val blobContent = blobFiles.map(path => FileApi.listFromFile(path,0))
      // We now can write their content back to our files created before
      pathList.foreach(pathFile => blobContent.foreach(listLine => listLine.foreach(line =>
        FileApi.utilWriter(pathFile.getPath,line))))
      // We add them back to our index file
      pathList.foreach(file => add.add(file))
    }
}
