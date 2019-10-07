package main.api

import java.io._

object FileApi {



  // This method allows us to write in a given file
  def utilWriter(file: String, content: String): Unit =
    {
      val fw = new FileWriter(file, true)
      try
        {
          fw.write(content + "\n")
        }
      finally fw.close()
    }
}
