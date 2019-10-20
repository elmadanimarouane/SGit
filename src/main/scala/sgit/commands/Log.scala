package sgit.commands

import api.{FileApi, SgitApi, TimeApi}

object Log {

  // This method allows us to get our general log file
  def log(): Unit =
    {
      // We get the path of our project
      val projectDir = System.getProperty("user.dir") + "/.sgit/"
      // We get the path of our log
      val logPath = projectDir + "/log/log"
      // We get the content of it in a list of string that we group to have a list of lists, each list representing
      // a commit written in our log file
      val logContent = FileApi.listFromFile(logPath,0).grouped(6).toList
      // We reverse it to have our newest commit at the beginning
      val logContentReversed = logContent.reverse
      // We print the whole content
      logContentReversed.foreach(x => x.foreach(line =>
      if(line.length == 81) println("commit " + line.substring(41)) else if (line.contains("Commit"))
      println("\n\t" + line.substring(8) + "\n") else println(line)))
    }

  // This method allows us to add a commit to our log file
  def createLog(shaValue: String, commitMessage: String, committerName: String, shaSubCommit: String
                ,customDir: String = ""): Unit =
    {
      // We get the path of our project
      val pathDir = System.getProperty("user.dir") + customDir + "/.sgit/"
      // We add it the path of our log file
      val pathFile = pathDir + "log/log"
      FileApi.utilWriter(pathFile, shaSubCommit + " " + shaValue)
      // We write the name of our branch
      FileApi.utilWriter(pathFile, "Branch: " + SgitApi.getBranchFile(customDir).getName)
      // We write the name of the author of the commit
      FileApi.utilWriter(pathFile, "Author: " + committerName)
      // We write the date of the commit
      FileApi.utilWriter(pathFile, "Date: " + TimeApi.getDate)
      // We write the message of our commit
      FileApi.utilWriter(pathFile,"Commit: " + commitMessage + "\n")
    }

  // This method allows us to get all the difference between all of our commits
  def logP(): Unit =
    {
      // We get all of our commits from our log file and reverse it to have our newest at the beginning
      val logContent = FileApi.listFromFile(System.getProperty("user.dir") + "/.sgit/log/log",0).grouped(6).toList
      val logContentSha = logContent.map(x=> x.headOption.getOrElse(
        throw new RuntimeException("Error: Impossible to get the sha of the commits for the log to print")
      ))
      val logContentUniqueSha = logContentSha.map(x => x.substring(41)).reverse
      // We compare each commit with its subcommit
      logContentUniqueSha.foreach(x => SgitApi.diffBetweenCommits(x))
    }
}
