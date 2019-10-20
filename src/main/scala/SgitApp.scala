import java.io.File

import scopt.OptionParser
import sgit.commands._

case class Arguments(command: String = "", inputArg: String = "", listFile: List[String] = List()
                     ,customBool: Boolean = false)

object SgitApp extends App {
  val parser = new OptionParser[Arguments]("sgit") {
    head("Sgit", "Made by Marouane El Madani")
    help("help").text("How to use sgit")

    cmd("sgit").required().children(
      cmd("init").
        text("Initiate a sgit directory")
        .action((_, c) => c.copy(command = "init")),
      cmd("status")
        .text("Display the changed and untracked files")
        .action((_,c) => c.copy(command = "status")),
      cmd("diff")
        .text("Show the differences in the files between the index and the working directory")
        .action((_,c) => c.copy(command = "diff")),
      cmd("add")
        .text("Update the index by adding current content in the working directory")
        .action((_,c) => c.copy(command = "add"))
        .children(
          arg[String]("<filename/filenames or . or regexp>")
          .text("The file to add in the index")
          .unbounded()
          .required()
          .action((x,c) => c.copy(listFile = c.listFile :+ x))
      ),
      cmd("commit")
        .text("Save your changes in the working directory")
        .action((_,c) => c.copy(command = "commit"))
        .children(
        opt[String]('m', "message")
          .text("Optional message to add")
          .action((x,c) => c.copy(inputArg = x))
        ),
      cmd("log")
        .text("Show the history of the different commits made")
        .action((_,c) => c.copy(command = "log"))
        .children(
        opt[Unit]('p', "patch")
          .text("Show the difference in the files between the different commits")
          .action((_,c)=> c.copy(customBool = true))),
      cmd("branch")
        .text("Create a new branch. Note : It doesn't switch to the newly created branch")
        .action((_,c) => c.copy(command = "branch"))
        .children(
        opt[String]("<branch name>")
          .text("The name of the branch to create")
          .optional()
          .action((x,c) => c.copy(inputArg = x)),
        opt[Unit]("av")
          .text("List all the branches")
          .action((_,c)=> c.copy(customBool = true))),
      cmd("checkout")
        .text("Switch to a branch, tag or commit hash")
        .action((_,c) => c.copy(command = "checkout"))
        .children(
        arg[String]("<branch or tag or commit hash>")
          .text("Name of the branch, tag or commit hash")
          .required()
          .action((x,c) => c.copy(inputArg = x))),
      cmd("tag")
        .text("Create a new tag")
        .action((_,c) => c.copy(command = "tag"))
        .children(
        arg[String]("<tag name>")
          .text("Name of the tag")
          .required()
          .action((x,c) => c.copy(inputArg = x)))
    )
  }

  def run(arguments: Arguments): Unit =
    {
      // We make sure that our repo is indeed a sgit repo
      val isSgitRepo = Init.isSgitDir(System.getProperty("user.dir"))
      arguments.command match
        {
        case "init" => Init.initSgitDir()
        case "status" if isSgitRepo => Status.status()
        case "diff" if isSgitRepo => Diff.diff()
        case "add" if isSgitRepo =>
          if(arguments.listFile.nonEmpty)
            {
              // We don't use HeadOption here because we already made sure our list is not empty
              if(arguments.listFile.head == ".") Add.addAll() else arguments.listFile.foreach(file => Add.add(new File(file)))
            }
        case "commit" if isSgitRepo => Commit.commit(Some(arguments.inputArg))
        case "log" if isSgitRepo => if(!arguments.customBool) Log.log() else Log.logP()
        case "branch" if isSgitRepo => if(!arguments.customBool) Branch.branch(arguments.inputArg) else Branch.listBranches()
        case "checkout" if isSgitRepo => Checkout.checkout(arguments.inputArg)
        case "tag" if isSgitRepo => Tag.tag(arguments.inputArg)
        case _ => println("Unknown command. Check your command or if your repository is indeed a sgit repo")}
    }

  parser.parse(args, Arguments()) match
  {
    case Some(arguments) => run(arguments)
    case None =>
  }
}
