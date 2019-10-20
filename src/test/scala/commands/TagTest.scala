package commands

import java.io.File

import api.FileApi
import org.scalatest.{BeforeAndAfter, FunSpec, Matchers}
import sgit.commands.{Add, Commit, Init, Tag}

import scala.reflect.io.Directory

class TagTest extends FunSpec with BeforeAndAfter with Matchers{
  // We create a temporary directory that we will use for our test
  val fullTestDirPath: String = System.getProperty("user.dir") + "/testDir"
  val testDirFile: File = new File(fullTestDirPath)
  // We create a test file that we will use for our test
  val testFile = new File(fullTestDirPath + "/testFile")
  // Before our test, we create our test directory and we make an init in it. Then we create our file and we add them
  before
  {
    testDirFile.mkdir()
    Init.initSgitDir(fullTestDirPath)
    testFile.createNewFile()
    FileApi.utilWriter(testFile.getPath,"Test")
    Add.add(testFile, fullTestDirPath)
  }

  // After our test, we delete our test directory with everything inside of it
  after
  {
    new Directory(testDirFile).deleteRecursively()
  }

  describe("A tag process")
  {
    it("Should not create a tag if no commit where made")
    {
      // We try to create our tag
      Tag.tag("Tag test",fullTestDirPath)
      // We didn't make a single commit so we should have no tag created
      Tag.getTags(fullTestDirPath).isEmpty shouldBe true
    }
    it("Should create a tag after a commit with the commit sha in it")
    {
      // We do a commit
      Commit.commit(userPath = fullTestDirPath)
      // We create a tag
      Tag.tag("Tag test", fullTestDirPath)
      // We should have one tag now
      Tag.getTags(fullTestDirPath).nonEmpty shouldBe true
      // We get our tag
      val tagPath = fullTestDirPath + "/.sgit/refs/tags/Tag test"
      // We check if it is indeed a file
      new File(tagPath).isFile shouldBe true
      // We get its content
      val tagContent = FileApi.listFromFile(tagPath,0)
      // Its head should contain our commit sha
      tagContent.head == Commit.getCommits(fullTestDirPath).head shouldBe true
    }
  }
}
