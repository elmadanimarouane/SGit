package main.sgit.commands

object checkout {

  // This method allows us to switch between commits, branch or tags
  def checkout(paramCheckout: String): Unit =
    {
      // We first need to be sure that the argument is either a branch, tag or commit
      // We first get our list of commits
      val listOfCommits = commit.getCommits
      // We then get our list of branches
      val listOfBranches = branch.getBranches.map(x => x.getName)
      // We finally get our list of tags
      val listOfTags = tag.getTags.map(x => x.getName)
      // Now that we have the three lists, we can check to what correspond our parameter
      // We start with the commits. We check first if our list of commits is not empty
      if(listOfCommits.nonEmpty)
        {
          // We check if our parameter correspond to a commit
          if(listOfCommits.contains(paramCheckout))
            {

            }
        }
        // We check if our list of branches is empty
      if(listOfBranches.nonEmpty)
        {
          //We check if our parameter correspond to a branch
          if(listOfBranches.contains(paramCheckout))
            {

            }
        }
        // We check if our list of tags is empty
      if(listOfTags.nonEmpty)
        {
          // We check if our parameter correspond to a tag
          if(listOfTags.contains(paramCheckout))
            {

            }
        }
    }

}
