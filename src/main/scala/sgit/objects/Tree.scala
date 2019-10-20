package sgit.objects

import api.{FileApi, ObjectApi}


// A tree is what hold the state of our files in the different commits. It also have a sha so it can be unique and
// recognizable. It contains a list of blob which represent the state of our object and it may contain an subtree
// which correspond to the state of our file from the previous commit
case class Tree(sha: String, listOfBlob: List[Blob])

object Tree{

  def createTree(sha: String, listOfBlob: List[Blob],userPath: String): Tree =
    {
      // We create our tree directory and retrieve the path of our tree file
      val treeFile = ObjectApi.createObject("trees", sha, userPath)
      // We write each of our blob in the file
      listOfBlob.foreach(x => FileApi.utilWriter(treeFile, "blob " + x.sha + " " + x.content.getPath))
      // We return our tree
      new Tree(sha, listOfBlob)
    }
}