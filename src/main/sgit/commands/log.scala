package main.sgit.commands

import java.io.File

import main.api.{FileApi, SgitApi, TimeApi}

object log {

  // This method allows us to get our general log file
  def log(): Unit =
    {
      // We get the path of our project
      val projectDir = System.getProperty("user.dir") + "/.sgit/"
      // We get the path of our log
      val logPath = projectDir + "/log/log"
      // We get the content of it in a list of string
      val logContent = FileApi.listFromFile(logPath,0)
      // We print the whole content
      logContent.foreach(x =>
      if(x.length == 81) println("commit " + x.substring(41))
        else if(x.contains("Commit")) println("\n\t" + x.substring(8) + "\n")
        else println(x)
      )
    }

  // This method allows us to add a commit to our log file
  def createLog(shaValue: String, commitMessage: String, committerName: String, shaSubCommit: String = null): Unit =
    {
      // We get the path of our project
      val pathDir = System.getProperty("user.dir") + "/.sgit/"
      // We add it the path of our log file
      val pathFile = pathDir + "log/log"
      // If it is our first commit, we give our sub commit the value "0" 40 time.
      if(shaSubCommit == null)
        {
          FileApi.utilWriter(pathFile,"0"*40 + " " + shaValue)
        }
      else
        {
          FileApi.utilWriter(pathFile, shaSubCommit + " " + shaValue)
        }
      // We write the name of our branch
      FileApi.utilWriter(pathFile, "Branch: " + SgitApi.getBranchFile.getPath.replace(pathDir + "refs/heads/",""))
      // We write the name of the author of the commit
      FileApi.utilWriter(pathFile, "Author: " + committerName)
      // We write the date of the commit
      FileApi.utilWriter(pathFile, "Date: " + TimeApi.getDate)
      // We write the message of our commit
      FileApi.utilWriter(pathFile,"Commit: " + commitMessage + "\n")
    }

}
