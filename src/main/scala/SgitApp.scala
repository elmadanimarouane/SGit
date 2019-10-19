import java.io.File

import scopt.OptionParser
import sgit.commands.{add, branch, checkout, commit, diff, init, log, status, tag}

case class Arguments(command: String = "Choose a command to do ", inputArg: String = "", listFile: List[String] = List()
                    , customBool: Boolean = false)

object SgitApp extends App {
  val parser = new OptionParser[Arguments]("sgit") {
    head("Sgit", "Made by Marouane El Madani")
    help("help").text("How to use sgit")

    cmd("sgit").required().children(
      cmd("init").action((_, c) => c.copy(command = "init")),
      cmd("status").action((_,c) => c.copy(command = "status")),
      cmd("diff").action((_,c) => c.copy(command = "diff")),
      cmd("add").action((_,c) => c.copy(command = "add")).children(
        arg[String]("<filename/filenames or . or regexp>").unbounded().required().action((x,c) =>
          c.copy(listFile = c.listFile :+ x))
      ),
      cmd("commit").action((_,c) => c.copy(command = "commit")).children(
        opt[String]('m', "message").action((x,c) => c.copy(inputArg = x))
      ),
      cmd("log").action((_,c) => c.copy(command = "log")).children(
        opt[Unit]('p', "patch").action((_,c)=> c.copy(customBool = true))),
      cmd("branch").action((_,c) => c.copy(command = "branch")).children(
        opt[String]("<branch name>").optional().action((x,c) => c.copy(inputArg = x)),
        opt[Unit]("av").action((_,c)=> c.copy(customBool = true))),
      cmd("checkout").action((_,c) => c.copy(command = "checkout")).children(
        arg[String]("<branch or tag or commit hash>").required().action((x,c) => c.copy(inputArg = x))),
      cmd("tag").action((_,c) => c.copy(command = "tag")).children(
        arg[String]("<tag name>").required().action((x,c) => c.copy(inputArg = x)))
    )
  }

  def run(arguments: Arguments): Unit =
    {
      // We make sure that our repo is indeed a sgit repo
      val isSgitRepo = init.isSgitDir(System.getProperty("user.dir"))
      arguments.command match
        {
        case "init" => init.initSgitDir()
        case "status" if isSgitRepo => status.status()
        case "diff" if isSgitRepo => diff.diff()
        case "add" if isSgitRepo =>
          if(arguments.listFile.nonEmpty)
            {
              if(arguments.listFile.head == ".") add.addAll() else arguments.listFile.foreach(file => add.add(new File(file)))
            }
        case "commit" if isSgitRepo => commit.commit(arguments.inputArg)
        case "log" if isSgitRepo => if(!arguments.customBool) log.log() else log.logP()
        case "branch" if isSgitRepo => if(!arguments.customBool) branch.branch(arguments.inputArg) else branch.listBranches()
        case "checkout" if isSgitRepo => checkout.checkout(arguments.inputArg)
        case "tag" if isSgitRepo => tag.tag(arguments.inputArg)
        case _ => println("Unknown command. Check your command or if your repository is indeed a sgit repo")}
    }

  parser.parse(args, Arguments()) match
  {
    case Some(arguments) => run(arguments)
    case None =>
  }
}
