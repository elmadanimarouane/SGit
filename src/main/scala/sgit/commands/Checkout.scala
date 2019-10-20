package sgit.commands

import java.io.File

import api.{FileApi, SgitApi}

object Checkout {

  // This method allows us to switch between commits, branch or tags
  def checkout(paramCheckout: String, userPath: String): Unit =
    {
      // We first need to be sure that the argument is either a branch, tag or commit
      // We first get our list of commits
      val listOfCommits = Commit.getCommits(userPath)
      // We then get our list of branches
      val listOfBranches = Branch.getBranches(userPath).map(x => x.getName)
      // We finally get our list of tags
      val listOfTags = Tag.getTags(userPath).map(x => x.getName)
      // Now that we have the three lists, we can check to what correspond our parameter
      // We start with the commits. We check first if our list of commits is not empty
      if(listOfCommits.nonEmpty)
        {
          // We check if our parameter correspond to a commit
          if(listOfCommits.contains(paramCheckout))
            {
              // Easier case. We simply execute our "doCheckout" method
              doCheckout(paramCheckout, userPath)
            }
        }
        // We check if our list of branches is empty
      if(listOfBranches.nonEmpty)
        {
          //We check if our parameter correspond to a branch
          if(listOfBranches.contains(paramCheckout))
            {
              val pathBranch = userPath +  s"/.sgit/refs/heads/$paramCheckout"
              // Since a branch can be empty (in the case of a newly created branch), we simply clear our work
              // repository
              if(FileApi.listFromFile(pathBranch,0).isEmpty)
                {
                  FileApi.cleanWorkRepo(userPath)
                }
              else
                {
                  doCheckout(FileApi.listFromFile(pathBranch,0).head, userPath)
                }
              // We switch our branch
              SgitApi.changeBranch(paramCheckout, userPath)
            }
        }
        // We check if our list of tags is empty
      if(listOfTags.nonEmpty)
        {
          // We check if our parameter correspond to a tag
          if(listOfTags.contains(paramCheckout))
            {
              // We get the sha of our commit located in our tag
              val tagPath = userPath + s"/.sgit/refs/tags/$paramCheckout"
              doCheckout(FileApi.listFromFile(tagPath,0).head, userPath)
            }
        }
    }

  // This method allows us to recreate our working directory based on the sha of a commit
  def doCheckout(shaValue: String, userPath: String): Unit =
    {
      // We get our sha file
      val shaPath = userPath+"/.sgit/objects/commits/"+shaValue.substring(0,2)+"/"+shaValue.substring(2)
      // We get our tree stored in our sha. Note : We are forced to do our substring later since our tree contain
      // an empty line
      val treeValue = FileApi.listFromFile(shaPath,0).headOption.getOrElse(
        throw new RuntimeException("Error: Impossible to get the tree sha. Empty line found instead of a sha value")
      ).substring(5)
      // We get our tree path
      val treePath = userPath+"/.sgit/objects/trees/"+treeValue.substring(0,2)+"/"+treeValue.substring(2)
      // We read the whole content of our tree file by removing the "blob" attribute
      val treeContent = FileApi.listFromFile(treePath,5)
      // We separate our tree content into two lists : one containing the shas and one containing the paths
      val shaList = treeContent.map(x => x.substring(0,40))
      val pathList = treeContent.map(x=> new File(x.substring(41)))
      // We have what we need to recreate our index. We can now clear our work directory
      FileApi.cleanWorkRepo(userPath)
      // Now that we have a clean work repository, we can recreate the files of our commit. We first create the
      // repositories and then we create the files
      pathList.foreach(x => new File(x.getPath.replace("/" + x.getName,"")).mkdirs())
      pathList.foreach(x => x.createNewFile())
      // Now that we have our files ready, we can rewrite their content in them. We first must get our blob file
      val blobFiles = shaList.map(sha => userPath + "/.sgit/objects/blobs/" + sha.substring(0,2) +
        "/" + sha.substring(2))
      // We now get their content
      val blobContent = blobFiles.map(path => FileApi.listFromFile(path,0))
      // We now can write their content back to our files created before
      pathList.foreach(pathFile => blobContent.foreach(listLine => listLine.foreach(line =>
        FileApi.utilWriter(pathFile.getPath,line))))
      // We add them back to our index file
      pathList.foreach(file => Add.add(file, userPath))
    }
}
