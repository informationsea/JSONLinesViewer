package info.informationsea.jsonview

import java.util
import java.util.Collections

import info.informationsea.jsonview.values._
import javax.swing.tree.TreeNode

import scala.collection.JavaConverters._
import scala.collection.mutable
import scala.collection.mutable.ListBuffer

case class JsonTreeNode(parent: Option[JsonTreeNode], jsonValue: JsonValue, path: Option[JsonPath]) extends TreeNode {

  private val _children: mutable.ListBuffer[JsonTreeNode] = ListBuffer()

  jsonValue match {
    case JsonMapValue(values, _) =>
      for (one <- values) {
        _children += JsonTreeNode(Some(this), one._2, Some(JsonMapPath(one._1)))
      }
    case JsonArrayValue(values, _) =>
      var i = 0
      for (one <- values) {
        _children += JsonTreeNode(Some(this), one, Some(JsonArrayPath(i)))
        i += 1
      }
    case _ =>
  }

  def this(jsonValue: JsonValue) {
    this(None, jsonValue, None)
  }

  def addNode(node: JsonTreeNode): Unit = _children += node

  override def getChildAt(i: Int): TreeNode = _children(i)

  override def getChildCount: Int = _children.size

  override def getParent: TreeNode = parent.orNull

  override def getIndex(treeNode: TreeNode): Int = _children.indexOf(treeNode)

  override def getAllowsChildren: Boolean = true

  override def isLeaf: Boolean = _children.isEmpty

  override def children(): util.Enumeration[_ <: TreeNode] = Collections.enumeration(_children.asJava)

  override def toString: String = (path match {
    case Some(JsonArrayPath(i)) => "[" + i + "] "
    case Some(JsonMapPath(key)) => "" + key + ": "
    case _ => ""
  }) + jsonValue.stringValue
}
