package main.api

import java.io._
import java.security.{DigestInputStream, MessageDigest}

import scala.io.Source


object FileApi {
  // This method allows us to write in a given file
  def utilWriter(file: String, content: String): Unit =
    {
      val fw = new FileWriter(file, true)
      try
        {
          fw.write(content + "\n")
        }
      finally fw.close()
    }

  // This method allows us to encode a full file in a SHA string
  def encodeSha(file: File): String =
  {
    // We choose to set our digester to SHA1
    val messageDigest = MessageDigest.getInstance("SHA1")
    // We affect to our buffer a size of 8192 which allows us to not keep a lot of bytes in our memory, but still
    // be quite fast (good ratio speed/memory consumption)
    val buffer = new Array[Byte](8192)

    // We get our file
    val dis = new DigestInputStream(new FileInputStream(file), messageDigest)
    try
      {
        // While we didn't find the end of the file, we keep reading the buffer
        while (dis.read(buffer) != -1)
        {

        }
      }
      // When we are done, we close the digester
    finally
    {
      dis.close()
    }
    // We convert our result in a string and return it
    messageDigest.digest.map("%02x".format(_)).mkString
  }

  // This method allows us to get all the file in our directory
  def getAllFilesFromSingleDir(dir: File) : List[File] =
    {
      // We check first if our dir exist and is indeed a Directory
      if (dir.exists && dir.isDirectory)
        {
          // We first get the full list of files
          dir.listFiles.filter(_.isFile).toList
        }
        // If it is not the case, we return an empty list
      else
        {
          List[File]()
        }
    }

  // This method allows us to get all the subdirectories. It will be used later when we will need to find all of our
  // subdirectories
  def getSubDir(dir: File): List[File] = dir.listFiles().filter(_.isDirectory).toList

  // This method allows us to get all the subdirectories and their subdirectories
  def getAllSubDir(dir: File): List[File] =
   {
     // Here, we use a tailrec recursion. We take as the first parameter the list that we will check each time
     // and in second parameter, we have our result that we will save in each recursion
      @scala.annotation.tailrec
      def tailRecGetAllSubDir(listToCheck: List[File], result: List[File]): List[File] = listToCheck match
        {
         // We make sure that we have still a full list, with a head and a tail
        case head :: tail =>
          // We save all the directories of our first element on the list
          val directories = getSubDir(head)
          // We then check if we have some subdirectories in our directory. If it is not the case, we only return
          // our result. If it's the opposite, we keep our head with our result since we need to check the
          // subdirectories in our directory. We also make sure to exclude our .sgit directory since we don't need
          // to check it
          val recursiveList = if(directories.isEmpty || (head.getPath contains ".sgit") || (head.getPath contains ".git") ||(head.getPath contains "out") || (head.getPath contains ".idea")) result else head :: result
          // We make the recursion again but this time with the tail with the subdirectories that we found earlier.
          // If we didn't find any subdirectory, then our value "directories" is empty and we simply loop in our tail.
          // Still, we keep our result in the recursiveList
          tailRecGetAllSubDir(tail ::: directories, recursiveList)
          // If we hit the end of our list, we simply return our result
        case _ => result
      }
     // We initiate our first recursion by creating a list with the first directories that we have and initialize our
     // result as an empty list
      tailRecGetAllSubDir(getSubDir(dir), Nil)
    }

  // This method allows us to have all the file in a single directory
  def getFilesSingleDir(dir: String): List[File] =  new File(dir).listFiles.filter(_.isFile).toList

  // This method allows us to have all the file in all of our directories
  def getFilesAllDir(dir: String): List[File] =
  {
    val dirFile = new File(dir)
    // Here, we use another tailrec method thanks to our previous method "getAllSubDir" which allows us to have
    // all of our directories
    @scala.annotation.tailrec
    def tailRecGetAllFiles(listOfDirToChek: List[File], result: List[File]): List[File] = listOfDirToChek match
      {
        // If our list of directories is not empty, we get all the file of our first directory and do the operation
        // again with the rest of our list
      case head :: tail =>
        tailRecGetAllFiles(tail, getFilesSingleDir(head.getPath) ::: result)
        // If we hit the end of our list, we simply return our result
      case _ => result
    }
    // We initiate our first recursion by getting all the directories and set our result as an empty list
    tailRecGetAllFiles(getAllSubDir(dirFile), Nil)
  }

  // This method allows us to know if a directory is empty or not
  def isDirEmpty(dir: File): Boolean =
    {
      dir.listFiles().toList.isEmpty
    }

  // This method allows us to get a list of file from each line of a file. Since it is used to get the content of our
  // index file which contains in every line a SHA and the path of a file, we add as attributes which substring of each
  // line we would like to keep. If we don't indicate the last attribute of our substring, it then means that we want
  // our substring to stop until the last character of the line
  def listFromFile(file: String, firstSubstring: Int, lastSubString: Int = -1): List[String] =
    {
      // We get our source file
      val source = Source.fromFile(new File(file))
      // We keep each lines as a file
      val result = (for (line <- source.getLines()) yield line.substring(firstSubstring,
        if(lastSubString == -1) line.length else lastSubString)).toList
      // We close our source
      source.close()
      // We return the result
      result
    }

  // This method allows us to get a Set of the files kept in our Index file
  def getListOfKeptFiles: Set[File] = {
    // We get the path of our project
    val pathProject = System.getProperty("user.dir")
    // We get all of our path kept in our index file
    val listOfPathFile = listFromFile(pathProject + "/.sgit/index",41)
    listOfPathFile.map(x => new File(x)).toSet
  }

  // This method allows us to get a list of our SHA tracked
  def getFullListOfKeptFiles: List[String] =
    {
      // We get the path of our project
      val pathProject = System.getProperty("user.dir")
      // We get all of our SHA kept in our index file
      FileApi.listFromFile(pathProject + "/.sgit/index", 0)
    }

  // This method allows us to compare two file by giving as attribute the list of each line of our two files
  def difBetweenTwoFiles(file1: List[String], file2: List[String]): Unit =
    {

    }

  // This method allows us to clear a file
  def clearFile(file: File): Unit =
    {
      val pw = new PrintWriter(file)
      pw.print("")
      pw.close()
    }

  // This method allows us to clean our work repository by deleting our tracked files and empty repos
  def cleanWorkRepo(): Unit =
    {
      // We get the path of our project
      val projectPath = System.getProperty("user.dir")
      // We get the path of our tracked files located in our index file
      val listFileIndex = FileApi.getFullListOfKeptFiles.map(x => x.substring(41))
      // We delete them
      listFileIndex.foreach(path => new File(path).delete())
      // If we have empty repo, we delete them aswell. First, we get our list of repository from our project and
      // sort them by the length of their path, the longer the deeper in our work repository
      val listRepos = FileApi.getAllSubDir(new File(projectPath)).sortWith(_.getPath.length > _.getPath.length)
      // We check if our repositories are empty or not. If it's the case, we delete it. Since our list in ordered by
      // the longest path, we are sure we won't miss any folders that may contain some empty folders
      listRepos.foreach(file => if(FileApi.isDirEmpty(file)) file.delete())
      // We clear our index file
      clearFile(new File(projectPath + "/.sgit/index"))
    }
}
