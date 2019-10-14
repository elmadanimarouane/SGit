package main.sgit.commands

import java.io.File

object branch {

  // This method allows us to create a new branch
  def branch(nameBranch: String): Unit =
    {
      // We get the path of our heads from our project
      val headsPath = System.getProperty("user.dir") + "/.sgit/refs/heads/"
      // We create a file object of our branch
      val branchObject = new File(headsPath + nameBranch)
      // We check if it exists. If it is the case, we tell to the user that this branch already exists. If not, we
      // create it
      if(branchObject.isFile)
        {
          println("A branch with the same name already exists")
        }
      else
        {
          branchObject.createNewFile()
        }
    }

}
