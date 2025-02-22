package sgit.commands

import api.{FileApi, SgitApi}

object Status {

  def status(userPath: String): Unit =
    {
      // We get the files that we kept in our index file
      val listOfKeptFiles = FileApi.getListOfKeptFiles(userPath)
      // We get all of our files
      val listOfFiles = FileApi.getFilesAllDir(userPath)
      // We filter our list of files to keep only the files that weren't stored in our index file (and therefore, our
      // new files)
      val listOfUntracktedFiles = listOfFiles.filterNot(listOfKeptFiles)
      // We get the list of the files that have been modified
      val listOfModifiedFiles = SgitApi.modifiedFiles(userPath)
      //If our list of modified files is not empty, we print all the modified files on the console
      if(listOfModifiedFiles.nonEmpty)
        {
          println("Changes not staged for commit:")
          println("\t(use 'sgit add <file>...' to update what will be committed)")
          for (files <- listOfModifiedFiles)
            {
              println("\t\t" + files._1.content.getPath.replace(userPath + "/",""))
            }
          println()
        }

      // If our list of new files is not empty, we print all the new files on the console
      if(listOfUntracktedFiles.nonEmpty)
        {
          println("Untracked files:")
          println("\t(use 'sgit add <file>...' to include in what will be committed)\n")
          for (files <- listOfUntracktedFiles)
            {
              println("\t\t" + files.getPath.replace(userPath + "/",""))
            }
        }
    }
}
