package main.sgit.objects

// A tree is what hold the state of our files in the different commits. It also have a sha so it can be unique and
// recognizable. It contains a list of blob which represent the state of our object and it may contain an subtree
// which correspond to the state of our file from the previous commit
case class Tree(sha: String, listOfBlob: List[Blob], subTree: Tree = null)

object Tree{

}