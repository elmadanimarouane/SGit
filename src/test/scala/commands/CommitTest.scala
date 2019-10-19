package commands

import java.io.File

import api.FileApi
import sgit.commands.{add, commit, init}
import org.scalatest.{BeforeAndAfter, FunSpec, Matchers}

import scala.reflect.io.Directory

class CommitTest extends FunSpec with BeforeAndAfter with Matchers{
  // We create a temporary directory that we will use for our test
  val testDir = "/testDir"
  val fullTestDirPath: String = System.getProperty("user.dir") + testDir
  val testDirFile: File = new File(fullTestDirPath)
  // We create two test files that we will use for our test, with one in a directory beneath our test directory
  val testFile1 = new File(fullTestDirPath + "/testFile1")
  val subDir = new File(fullTestDirPath + "/subDir")
  val testFile2 = new File(subDir.getPath + "/testFile2")

  // Before our test, we create our test directory and we make an init in it. Then we create our 2 files and we add them
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
    add.add(testFile2, testDir)
  }
  // After our test, we delete our test directory with everything inside of it
  after
  {
    new Directory(testDirFile).deleteRecursively()
  }

  describe("A commit process")
  {
    it("Should create a tree, a commit, a master file and this file should contain the sha of our commit")
    {
      // We make a commit
      commit.commit("Commit test", testDir)
      // We check that our commit is indeed created
      val commitsDir = fullTestDirPath + "/.sgit/objects/commits"
      // We check that we have indeed a directory inside of our commits directory
      FileApi.getSubDir(new File(commitsDir)).nonEmpty shouldBe true
      // We check that we have a tree in our trees directory
      val treesDir = fullTestDirPath + "/.sgit/objects/trees"
      FileApi.getSubDir(new File(treesDir)).nonEmpty shouldBe true
      // We check that we have a sha stored in our master file in our heads directory
      val masterDir = fullTestDirPath + "/.sgit/refs/heads/master"
      val shaValue = FileApi.listFromFile(masterDir,0).head
      shaValue.nonEmpty shouldBe true
      // We check that we have a commit dir with the sha value
      val commitDir = commitsDir + "/" + shaValue.substring(0,2)
      new File(commitDir).isDirectory shouldBe true
      val commitFile = commitDir + "/" + shaValue.substring(2)
      new File(commitFile).isFile shouldBe true
      // We check that our first line is indeed the tree that was created
      val shaTree = FileApi.listFromFile(commitFile,0).head.replace("tree ","")
      val treeDir = treesDir + "/" + shaTree.substring(0,2)
      new File(treeDir).isDirectory shouldBe true
      val treeFile = treeDir + "/" + shaTree.substring(2)
      new File(treeFile).isFile shouldBe true
      // We check that the content of our tree is the two blobs that we added before
      val treeContent = FileApi.listFromFile(treeFile,0)
      treeContent.head == "blob " + FileApi.encodeSha(testFile1) + " " + testFile1.getPath shouldBe true
      treeContent.tail.head == "blob " + FileApi.encodeSha(testFile2) + " " + testFile2.getPath shouldBe true
      // We check that our log was modified with our last commit
      val logDir = fullTestDirPath + "/.sgit/log/log"
      val logContent = FileApi.listFromFile(logDir,0).head
      logContent == "0"*40 + " " + shaValue shouldBe true
      // We check that our commit name is correct
      commit.getCommitName(shaValue,testDir) == "Commit test" shouldBe true
      // We check that we can get our commit
      commit.getCommits(testDir).head == shaValue shouldBe true
    }
  }
}
