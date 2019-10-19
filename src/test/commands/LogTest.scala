package commands

import java.io.File

import main.api.FileApi
import main.sgit.commands.{add, commit, init}
import org.scalatest.{BeforeAndAfter, FunSpec, Matchers}

import scala.reflect.io.Directory

class LogTest extends FunSpec with BeforeAndAfter with Matchers{

  // We create a temporary directory that we will use for our test
  val testDir = "/testDir"
  val fullTestDirPath: String = System.getProperty("user.dir") + testDir
  val testDirFile: File = new File(fullTestDirPath)
  // We create two test files that we will use for our test, with one in a directory beneath our test directory
  val testFile1 = new File(fullTestDirPath + "/testFile1")
  val subDir = new File(fullTestDirPath + "/subDir")
  val testFile2 = new File(subDir.getPath + "/testFile2")

  // Before our test, we create our test directory and we make an init in it. Then we create our 2 files and we add
  // one of them
  before
  {
    testDirFile.mkdir()
    init.initSgitDir(testDir)
    testFile1.createNewFile()
    FileApi.utilWriter(testFile1.getPath,"Test1")
    subDir.mkdir()
    testFile2.createNewFile()
    FileApi.utilWriter(testFile2.getPath, "Test2")
    add.add(testFile1, testDir)
  }
  // After our test, we delete our test directory with everything inside of it
  after
  {
    new Directory(testDirFile).deleteRecursively()
  }

  describe("A log test")
  {
    it("Should, after a commit, change our log file with the previous commit and the new commit in it")
    {
      // We do our first commit
      commit.commit("Test commit 1",testDir)
      // We check that the first line of our log contains our new commit
      val logContent = FileApi.listFromFile(fullTestDirPath + "/.sgit/log/log",0)
      logContent.head == "0"*40 + " " + commit.getCommits(testDir).head shouldBe true
      // We check that the name of our commit was well written
      val logContentReversed = logContent.reverse
      // Since we add a \n after writing in our log file, we should take the penultimate element of our content
      logContentReversed.tail.head == "Commit: Test commit 1" shouldBe true
      // We add our second file and do another commit afterward
      add.add(testFile2, testDir)
      commit.commit(customDir = testDir)
      // We get back our new log content
      val newLogContent = FileApi.listFromFile(fullTestDirPath + "/.sgit/log/log",0)
      println(newLogContent)
      // We check that our log content contains our two commits
      val commitNumberOne = commit.getCommits(testDir).head
      val commitNumberTwo = commit.getCommits(testDir).tail.head
      newLogContent.contains(commitNumberOne + " " + commitNumberTwo) ||
        newLogContent.contains(commitNumberTwo + " " + commitNumberOne) shouldBe true
      // We check that we have our second commit message, which, because we didn't specify a commit message, should
      // be "Commit number 2"
      newLogContent.contains("Commit: Commit number 2") shouldBe true
    }
  }
}
