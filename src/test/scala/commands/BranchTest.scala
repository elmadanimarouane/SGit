package commands

import java.io.File

import api.FileApi
import sgit.commands.{add, branch, commit, init}
import org.scalatest.{BeforeAndAfter, FunSpec, Matchers}

import scala.reflect.io.Directory

class BranchTest extends FunSpec with BeforeAndAfter with Matchers{

  // We create a temporary directory that we will use for our test
  val testDir = "/testDir"
  val fullTestDirPath: String = System.getProperty("user.dir") + testDir
  val testDirFile: File = new File(fullTestDirPath)
  // We create a test file that we will use for our test
  val testFile = new File(fullTestDirPath + "/testFile")
  // Before our test, we create it, init sgit in it and commit a file in it
  before
  {
    testDirFile.mkdir()
    init.initSgitDir(testDir)
    testFile.createNewFile()
    FileApi.utilWriter(testFile.getPath,"Test")
    add.add(testFile, testDir)
    commit.commit("Commit test", testDir)
  }
  // After the test we delete it
  after
  {
    new Directory(testDirFile).deleteRecursively()
  }

  describe("A branch process")
  {
    it("Should create a branch by adding a file in our refs/heads directory")
    {
      // We create our branch
      branch.branch("TestBranch", testDir)
      // We check that our branch file was indeed created
      val branchPath = fullTestDirPath + "/.sgit/refs/heads/TestBranch"
      new File(branchPath).isFile shouldBe true
      // We check that we have indeed two branches (our new and the master one) in our directory
      branch.getBranches(testDir).size shouldBe 2
    }
  }

}
