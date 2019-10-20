package commands

import java.io.File

import org.scalatest.{BeforeAndAfter, FunSpec, Matchers}
import sgit.commands.Init

import scala.reflect.io.Directory

class InitTest extends FunSpec with BeforeAndAfter with Matchers {
  // We create a temporary directory that we will use for our test
  val testDir = "/testDir"
  val fullTestDirPath: String = System.getProperty("user.dir") + testDir
  val testDirFile: File = new File(fullTestDirPath)
  // Before our test, we create it
  before
  {
    testDirFile.mkdir()
  }
  // After the test we delete it
  after
  {
    new Directory(testDirFile).deleteRecursively()
  }
  // We do our test
  describe("An init process")
  {
    it("Should create our full .sgit repo with every file needed")
    {
      Init.initSgitDir(testDir)
      // Our testDir should be a .sgit directory now
      Init.isSgitDir(fullTestDirPath) shouldBe true
      // We now check that every file was created
      val logDir = new File(fullTestDirPath + "/.sgit/log")
      logDir.isDirectory shouldBe true
      val logFile = new File(logDir.getPath + "/log")
      logFile.isFile shouldBe true
      val objectsDir = new File(fullTestDirPath + "/.sgit/objects")
      objectsDir.isDirectory shouldBe true
      val blobsDir = new File(objectsDir.getPath + "/blobs")
      blobsDir.isDirectory shouldBe true
      val commitDir = new File(objectsDir.getPath + "/commits")
      commitDir.isDirectory shouldBe true
      val treesDir = new File(objectsDir.getPath + "/trees")
      treesDir.isDirectory shouldBe true
      val refsDir = new File(fullTestDirPath + "/.sgit/refs")
      refsDir.isDirectory shouldBe true
      val headsDir = new File(refsDir.getPath + "/heads")
      headsDir.isDirectory shouldBe true
      val tagsDir = new File(refsDir.getPath + "/tags")
      tagsDir.isDirectory shouldBe true
      val configFile = new File(fullTestDirPath + "/.sgit/config")
      configFile.isFile shouldBe true
      val descriptionFile = new File(fullTestDirPath + "/.sgit/description")
      descriptionFile.isFile shouldBe true
      val headFile = new File(fullTestDirPath + "/.sgit/HEAD")
      headFile.isFile shouldBe true
      val indexFile = new File(fullTestDirPath + "/.sgit/index")
      indexFile.isFile shouldBe true
    }
  }
}
