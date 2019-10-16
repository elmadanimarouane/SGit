package main.sgit.commands

import java.io.File

import main.api.{FileApi, SgitApi}

object diff {

  // This method allows us to get what was modified in our added files
  def diff(): Unit =
    {
      // We get the list of our modified blob only
      val listOfModifiedBlob = SgitApi.modifiedFiles().map(x => x._1)
      // We get a list of our tracked files
      val listOfTrackedFiles = FileApi.getFullListOfKeptFiles
      // We combined the two lists with a for method. Note : we use a for method here because it is simpler to read
      // and understand than to use maps and filters
      val combinedList = for
        {
          blob <- listOfModifiedBlob
          file <- listOfTrackedFiles
        if file.contains(blob.content.getPath)
        }
        yield (blob,new File(file))
      // We get the path of our objects directory
      val currentObjectPath = System.getProperty("user.dir") + "/.sgit/objects/blobs/"
      // We create a list of our initial data based on their content stored in our objects directory
      val initialData = combinedList.map(x => FileApi.listFromFile(currentObjectPath + x._2.getPath.substring(0,2)
        + "/" + x._2.getPath.substring(2,40),0))
      // We create a list of our data based on their current content
      val finalData = combinedList.map(x => FileApi.listFromFile(x._1.content.getPath,0))
      // We merge the list of our blob with our list of initial data and final data so we can know which file
      // was modified and what were the modifications made
      val fullCombinedList = (listOfModifiedBlob,initialData,finalData).zipped.toList
      // We print our modifications
      for (x <- fullCombinedList)
        {
          println("Modification made to file " + x._1.content.getPath.replace(System.getProperty("user.dir") + "/"
            ,""))
              val combinedListOfData = x._3.zipAll(x._2, "", "")
              for(y <- combinedListOfData)
                {
                  if(y._1 == y._2)
                    {
                      println("   " + y._1)
                    }
                  else
                    {
                      // We check if our new file is smaller than our previous, which means some lines were removed
                      if(x._2.size >= x._3.size)
                        {
                          // If our new line is not in the old file
                          if((combinedListOfData.reverse.head._1 != y._1 || !y._1.isEmpty) && !x._2.contains(y._1))
                          {
                            // This means the line was added
                            println(Console.GREEN + " + " + y._1)
                            print(Console.WHITE)
                          }
                          // If our old line is not in the new file
                          if(!x._3.contains(y._2))
                          {
                            // This means the line was removed
                            println(Console.RED + " - " + y._2)
                            print(Console.WHITE)
                          }
                          // If our new line is in the old file
                          else
                          {
                            // This means the line was simply moved
                            println("   " + y._2)
                          }
                        }
                        //Otherwise, this means that some lines were added
                      else
                        {
                          // If our new line is not in the old file
                          if(!x._2.contains(y._1))
                          {
                            // This means the line was added
                            println(Console.GREEN + " + " + y._1)
                            print(Console.WHITE)
                          }
                          // If our old line is not in the new file
                          if((combinedListOfData.reverse.head._2 != y._2 || !y._2.isEmpty) && !x._3.contains(y._2))
                          {
                            // This means the line was removed
                            println(Console.RED + " - " + y._2)
                            print(Console.WHITE)
                          }
                          // If our new line is in the old file
                          if(x._2.contains(y._1))
                          {
                            // This means the line was simply moved
                            println("   " + y._1)
                          }
                        }
                    }
                }
        }
    }
}
