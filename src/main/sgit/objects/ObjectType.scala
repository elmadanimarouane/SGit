package main.sgit.objects

import main.sgit.objects

// Our type of SGit objects
object ObjectType extends Enumeration {
  type ObjectType = Value

  val Commit: objects.ObjectType.Value = Value("commit")
  val Tree: objects.ObjectType.Value = Value("tree")
  val Blob: objects.ObjectType.Value = Value("blob")
  val Tag: objects.ObjectType.Value = Value("tag")
  val Note: objects.ObjectType.Value = Value("note")

  def toString(objectVal: Value): String = objectVal match
  {
    case Commit => "commit"
    case Blob => "blob"
  }
}
