package commands

import java.io.File

import main.api.FileApi
import main.sgit.commands.{add, branch, checkout, commit, init, tag}
import org.scalatest.{BeforeAndAfter, FunSpec, Matchers}

import scala.reflect.io.Directory

class CheckoutTest extends FunSpec with BeforeAndAfter with Matchers{
  // We create a temporary directory that we will use for our test
  val testDir = "/testDir"
  val fullTestDirPath: String = System.getProperty("user.dir") + testDir
  val testDirFile: File = new File(fullTestDirPath)
  // We create three test files that we will use for our test, with one in a directory beneath our test directory
  val testFile1 = new File(fullTestDirPath + "/testFile1")
  val subDir = new File(fullTestDirPath + "/subDir")
  val testFile2 = new File(subDir.getPath + "/testFile2")
  val testFile3 = new File(fullTestDirPath + "/testFile3")

  // Before our test, we create our test directory and we make an init in it. Then we create only one of our file,
  // add it and commit it. We then create a new branch. We also save our commit for further use
  before
  {
    testDirFile.mkdir()
    init.initSgitDir(testDir)
    testFile1.createNewFile()
    FileApi.utilWriter(testFile1.getPath,"Test")
    add.add(testFile1, testDir)
    commit.commit(customDir = testDir)
    branch.branch("TestBranch", testDir)
  }
  // After our test, we delete our test directory with everything inside of it
  after
  {
    new Directory(testDirFile).deleteRecursively()
  }

  describe("A checkout process with branches")
  {
    it("Should, when doing our first checkout, clear the repository and, when going back to our master branch, " +
      "get back our first file")
    {
      // We add and commit our first file
      add.add(testFile1, testDir)
      commit.commit(customDir = testDir)
      // We first check that we have indeed our first file
      testFile1.isFile shouldBe true
      // We then checkout to our test branch in which we did no commits. This should clear our repository
      checkout.checkout("TestBranch", testDir)
      testFile1.isFile shouldBe false
      // We add our second file
      subDir.mkdir()
      testFile2.createNewFile()
      add.add(testFile2,testDir)
      commit.commit(customDir = testDir)
      // We check that we have indeed our second file
      testFile2.isFile shouldBe true
      // We then checkout to our first branch
      checkout.checkout("master", testDir)
      testFile2.isFile shouldBe false
      // We check that we have our first file
      testFile1.isFile shouldBe true
    }
  }

  describe("A checkout process with commits")
  {
    it("Should get back to our first commit when we checkout, then get back to our second commit")
    {
      // We add and commit our first file
      add.add(testFile1, testDir)
      commit.commit(customDir = testDir)
      // We get the sha value of our first commit
      val firstCommit = commit.getCommits(testDir).head
      // We do a second commit with our second file
      subDir.mkdir()
      testFile2.createNewFile()
      add.add(testFile2,testDir)
      commit.commit(customDir = testDir)
      // We get the sha value of our first commit
      val secondCommit = commit.getCommits(testDir).filter(sha => sha != firstCommit).head
      // We check that we have indeed our second file
      testFile2.isFile shouldBe true
      // We checkout to our first commit
      checkout.checkout(firstCommit, testDir)
      // We should no longer have our second file and have our first file
      testFile2.isFile shouldBe false
      testFile1.isFile shouldBe true
      // We go back to our second commit
      checkout.checkout(secondCommit, testDir)
      // We should have back our second file
      testFile2.isFile shouldBe true
    }
  }

  describe("A checkout process with tags")
  {
    it("Should get back to our commit when we checkout with a tag")
    {
      // We add and commit our first file
      add.add(testFile1, testDir)
      commit.commit(customDir = testDir)
      // We create a tag
      tag.tag("TestTag", testDir)
      // We add and commit our second file
      subDir.mkdir()
      testFile2.createNewFile()
      add.add(testFile2,testDir)
      commit.commit(customDir = testDir)
      // We check that we have our second file
      testFile2.isFile shouldBe true
      // We checkout with our tag
      checkout.checkout("TestTag", testDir)
      // We should no longer have our second file
      testFile2.isFile shouldBe false
      // We should still have our first file
      testFile1.isFile shouldBe true
    }
  }

}
