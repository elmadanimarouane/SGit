package commands

import java.io.File

import api.FileApi
import org.scalatest.{BeforeAndAfter, FunSpec, Matchers}
import sgit.commands.{Add, Init}

import scala.reflect.io.Directory


class AddTest extends FunSpec with BeforeAndAfter with Matchers{
  // We create a temporary directory that we will use for our test
  val fullTestDirPath: String = System.getProperty("user.dir") + "/testDir"
  val testDirFile: File = new File(fullTestDirPath)
  // We create two test files that we will use for our test, with one in a directory beneath our test directory
  val testFile1 = new File(fullTestDirPath + "/testFile1")
  val subDir = new File(fullTestDirPath + "/subDir")
  val testFile2 = new File(subDir.getPath + "/testFile2")

  // Before our test, we create our test directory and we make an init in it. Then we create our 2 files
  before
  {
    testDirFile.mkdir()
    Init.initSgitDir(fullTestDirPath)
    testFile1.createNewFile()
    FileApi.utilWriter(testFile1.getPath,"Test1")
    subDir.mkdir()
    testFile2.createNewFile()
    FileApi.utilWriter(testFile2.getPath, "Test2")
  }
  // After our test, we delete our test directory with everything inside of it
  after
  {
    new Directory(testDirFile).deleteRecursively()
  }

  describe("An add process")
  {
    it("Should create two blobs and change our index file")
    {
      // We add our first file
      Add.add(testFile1, fullTestDirPath)
      // We get our blobs directory
      val blobsDir = fullTestDirPath + "/.sgit/objects/blobs"
      // We should have a folder in our blobs directory
      FileApi.getSubDir(new File(blobsDir)).nonEmpty shouldBe true
      // We check that our folder is the two first string of the sha generated from our file
      val shaValue = FileApi.encodeSha(testFile1)
      val shaDir = blobsDir + "/" + shaValue.substring(0,2)
      new File(shaDir).isDirectory shouldBe true
      // We check that our file beneath our previous directory is the rest of the sha
      val shaFile = shaDir + "/" + shaValue.substring(2)
      new File(shaFile).isFile shouldBe true
      // We check that our index is indeed changed
      val indexPath = fullTestDirPath + "/.sgit/index"
      val indexContent = FileApi.listFromFile(indexPath,0)
      indexContent.head.substring(0,40) == shaValue shouldBe true
      indexContent.head.substring(41) == testFile1.getPath shouldBe true
      // We check that the content of our blob is indeed the content of our file
      val file1Content = FileApi.listFromFile(testFile1.getPath,0).head
      val blobContent = FileApi.listFromFile(blobsDir + "/" + shaValue.substring(0,2) + "/"
        + shaValue.substring(2),0).head
      file1Content == blobContent shouldBe true
      // We add our second file by giving as input our directory instead of the file
      Add.add(subDir, fullTestDirPath)
      // We check that we have indeed two blobs
      FileApi.getSubDir(new File(blobsDir)).size == 2 shouldBe true
      // We check that our index has two lines
      FileApi.listFromFile(indexPath,0).size == 2 shouldBe true
    }
  }

}
