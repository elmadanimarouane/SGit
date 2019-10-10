package main.api

import java.io.File

import main.sgit.objects.Blob

object SgitApi {

  // This method allows us to get all of our modified files
  def modifiedFiles(): Iterable[(Blob, Boolean)] =
  {
    // We get the files that we kept in our index file
    val listOfKeptFiles = FileApi.getListOfKeptFiles
    // We get the path of our project
    val pathProject = System.getProperty("user.dir")
    // We get all of our files
    val listOfFiles = FileApi.getFilesAllDir(pathProject)
    // We filter the tracked files by excluding from our list of all files the files that are not tracked
    val listOfTrackedFiles = listOfFiles.filter(listOfKeptFiles)
    // We convert our list of tracked files into lists of SHA1 and we keep only the first two char
    val listOfTrackedSha = listOfTrackedFiles.map(file => new Blob(file, FileApi.encodeSha(file).substring(0,2)))
    // We get all of our directories from our "objects" folder
    val listDirOfObjects = FileApi.getSubDir(new File(pathProject + "/.sgit/objects"))
    // We keep only the files that have been modified (and therefore, we keep only the files that have a SHA not
    // kept in our object directory)
    val listOfModifiedFilesBoolean = listOfTrackedSha.map(x => listDirOfObjects.map(_.getName).contains(x.sha))
    listOfTrackedSha.zip(listOfModifiedFilesBoolean).filter(x => !x._2)
  }

}
