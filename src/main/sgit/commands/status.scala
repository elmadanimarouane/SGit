package main.sgit.commands

import java.io.File

import main.api.FileApi
import main.sgit.objects.Blob

object status {

  def status(): Unit =
    {
      // We get the path of our project
      val pathProject = System.getProperty("user.dir")
      // We get all of our files
      val listOfFiles = FileApi.getFilesAllDir(pathProject)
      // We get all of our path kept in our index file
      val listOfKeptFiles = FileApi.listFromFile(pathProject + "/.sgit/index").toSet
      // We filter our list of files to keep only the files that weren't stored in our index file (and therefore, our
      // new files)
      val listOfUntracktedFiles = listOfFiles.filterNot(listOfKeptFiles)
      // We filter the tracked files by excluding from our list of all files the files that are not tracked
      val listOfTrackedFiles = listOfFiles.filter(listOfKeptFiles)
      // We convert our list of tracked files into lists of SHA1 and we keep only the first two char
      val listOfTrackedSha = listOfTrackedFiles.map(file => new Blob(file, FileApi.encodeSha(file).substring(0,2)))
      // We get all of our directories from our "objects" folder
      val listDirOfObjects = FileApi.getSubDir(new File(pathProject + "/.sgit/objects"))
      // We keep only the files that have been modified (and therefore, we keep only the files that have a SHA not
      // kept in our object directory)
      val listOfModifiedFiles = listOfTrackedSha.filter(_.sha )

      // If our list of new files is not empty, we print all the new files on the console
      if(listOfUntracktedFiles.nonEmpty)
        {
          println("Untracked files:")
          println("\t(use 'sgit add <file>...' to include in what will be committed)\n")
          for (files <- listOfUntracktedFiles)
            {
              println("\t\t" + files.getPath.replace(pathProject + "/",""))
            }
        }
    }

}
