package api.treeApi

import java.io.File

object TreeApi {

  // This method allows us to get our tree file based on its sha
  def getTreeFile(sha: String, customDir: String = "") : File =
    {
      // We get the path
      val treePath = (System.getProperty("user.dir") + customDir + "/.sgit/objects/trees/" + sha.substring(0,2)
        + "/" + sha.substring(2))
      // We return the file
      new File(treePath)
    }
}
