package commands

import java.io.File

import api.FileApi
import org.scalatest.{BeforeAndAfter, FunSpec, Matchers}
import sgit.commands._

import scala.reflect.io.Directory

class CheckoutTest extends FunSpec with BeforeAndAfter with Matchers{
  // We create a temporary directory that we will use for our test
  val fullTestDirPath: String = System.getProperty("user.dir") + "/testDir"
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
    Init.initSgitDir(fullTestDirPath)
    testFile1.createNewFile()
    FileApi.utilWriter(testFile1.getPath,"Test")
    Add.add(testFile1, fullTestDirPath)
    Commit.commit(userPath = fullTestDirPath)
    Branch.branch("TestBranch", fullTestDirPath)
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
      Add.add(testFile1, fullTestDirPath)
      Commit.commit(userPath = fullTestDirPath)
      // We first check that we have indeed our first file
      testFile1.isFile shouldBe true
      // We then checkout to our test branch in which we did no commits. This should clear our repository
      Checkout.checkout("TestBranch",fullTestDirPath)
      testFile1.isFile shouldBe false
      // We add our second file
      subDir.mkdir()
      testFile2.createNewFile()
      Add.add(testFile2,fullTestDirPath)
      Commit.commit(userPath = fullTestDirPath)
      // We check that we have indeed our second file
      testFile2.isFile shouldBe true
      // We then checkout to our first branch
      Checkout.checkout("master", fullTestDirPath)
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
      Add.add(testFile1, fullTestDirPath)
      Commit.commit(userPath = fullTestDirPath)
      // We get the sha value of our first commit
      val firstCommit = Commit.getCommits(fullTestDirPath).head
      // We do a second commit with our second file
      subDir.mkdir()
      testFile2.createNewFile()
      Add.add(testFile2,fullTestDirPath)
      Commit.commit(userPath = fullTestDirPath)
      // We get the sha value of our first commit
      val secondCommit = Commit.getCommits(fullTestDirPath).filter(sha => sha != firstCommit).head
      // We check that we have indeed our second file
      testFile2.isFile shouldBe true
      // We checkout to our first commit
      Checkout.checkout(firstCommit, fullTestDirPath)
      // We should no longer have our second file and have our first file
      testFile2.isFile shouldBe false
      testFile1.isFile shouldBe true
      // We go back to our second commit
      Checkout.checkout(secondCommit, fullTestDirPath)
      // We should have back our second file
      testFile2.isFile shouldBe true
    }
  }

  describe("A checkout process with tags")
  {
    it("Should get back to our commit when we checkout with a tag")
    {
      // We add and commit our first file
      Add.add(testFile1, fullTestDirPath)
      Commit.commit(userPath = fullTestDirPath)
      // We create a tag
      Tag.tag("TestTag", fullTestDirPath)
      // We add and commit our second file
      subDir.mkdir()
      testFile2.createNewFile()
      Add.add(testFile2,fullTestDirPath)
      Commit.commit(userPath = fullTestDirPath)
      // We check that we have our second file
      testFile2.isFile shouldBe true
      // We checkout with our tag
      Checkout.checkout("TestTag", fullTestDirPath)
      // We should no longer have our second file
      testFile2.isFile shouldBe false
      // We should still have our first file
      testFile1.isFile shouldBe true
    }
  }

}
