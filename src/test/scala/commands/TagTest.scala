package commands

import java.io.File

import api.FileApi
import sgit.commands.{add, commit, init, tag}
import org.scalatest.{BeforeAndAfter, FunSpec, Matchers}

import scala.reflect.io.Directory

class TagTest extends FunSpec with BeforeAndAfter with Matchers{
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
    init.initSgitDir(testDir)
    testFile.createNewFile()
    FileApi.utilWriter(testFile.getPath,"Test")
    add.add(testFile, testDir)
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
      tag.tag("Tag test",testDir)
      // We didn't make a single commit so we should have no tag created
      tag.getTags(testDir).isEmpty shouldBe true
    }
    it("Should create a tag after a commit with the commit sha in it")
    {
      // We do a commit
      commit.commit(customDir = testDir)
      // We create a tag
      tag.tag("Tag test", testDir)
      // We should have one tag now
      tag.getTags(testDir).nonEmpty shouldBe true
      // We get our tag
      val tagPath = fullTestDirPath + "/.sgit/refs/tags/Tag test"
      // We check if it is indeed a file
      new File(tagPath).isFile shouldBe true
      // We get its content
      val tagContent = FileApi.listFromFile(tagPath,0)
      // Its head should contain our commit sha
      tagContent.head == commit.getCommits(testDir).head shouldBe true
    }
  }
}
