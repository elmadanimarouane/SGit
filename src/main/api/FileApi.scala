package main.api

import java.io._
import java.security.{DigestInputStream, MessageDigest}


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
    val messageDigest = MessageDigest.getInstance("SHA1");
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
          val recursiveList = if(directories.isEmpty || (head.getPath contains ".sgit")) result else head :: result
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
}
