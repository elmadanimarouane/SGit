package commands

import java.io.File

import api.{FileApi, SgitApi}
import org.scalatest.{BeforeAndAfter, FunSpec, Matchers}
import sgit.commands.{Add, Init}

import scala.reflect.io.Directory

class StatusTest extends FunSpec with BeforeAndAfter with Matchers {
  // We create a temporary directory that we will use for our test
  val testDir = "/testDir"
  val fullTestDirPath: String = System.getProperty("user.dir") + testDir
  val testDirFile: File = new File(fullTestDirPath)
  // We create a test file that we will use for our test
  val testFile = new File(fullTestDirPath + "/testFile")

  // Before our test, we create our test directory and we make an init in it. Then we create our file and we add them
  before
  {
    testDirFile.mkdir()
    Init.initSgitDir(testDir)
    testFile.createNewFile()
    FileApi.utilWriter(testFile.getPath,"Test")
    Add.add(testFile, testDir)
  }

  // After our test, we delete our test directory with everything inside of it
  after
  {
    new Directory(testDirFile).deleteRecursively()
  }

  describe("A status process")
  {
    it("Should return a list of modified files with our test file after we modified it")
    {
      // We return the list of modified files and make sure it is empty
      val modifiedList = SgitApi.modifiedFiles(testDir)
      modifiedList.isEmpty shouldBe true
      // We modify our file
      FileApi.utilWriter(testFile.getPath, "Another line")
      // We return a new list of modified files and make sure it contains our modified file
      val newModifiedList = SgitApi.modifiedFiles(testDir)
      newModifiedList.nonEmpty shouldBe true
      val modifiedFile = newModifiedList.head
      modifiedFile._1.content == testFile shouldBe true
    }
  }
}
