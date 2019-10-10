package main.sgit.objects

import java.io.File

// A blob is what allows us to store the content of each file. It keeps the content of the file and generate a SHA
// of it so we can check later if the file was modified or not
case class Blob(content: File, sha: String)

object Blob{

}

