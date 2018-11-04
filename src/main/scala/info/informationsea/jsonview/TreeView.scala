package info.informationsea.jsonview

import javax.swing.JTree
import javax.swing.tree.TreeModel

import scala.swing.{Component, Scrollable}

class TreeView(model: TreeModel) extends Component with Scrollable.Wrapper {
  override lazy val peer: JTree = new JTree(model) with SuperMixin

  override protected def scrollablePeer: javax.swing.Scrollable = peer
}
