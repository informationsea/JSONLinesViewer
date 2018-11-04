package info.informationsea.jsonview

import java.awt.Dimension
import java.awt.datatransfer.{StringSelection, Transferable}
import java.awt.event.{InputEvent, KeyEvent, MouseAdapter, MouseEvent}
import java.io._
import java.lang
import java.util.zip.GZIPInputStream

import com.fasterxml.jackson.core.JsonFactory
import info.informationsea.jsonview.values._
import javax.swing.event.TreeSelectionEvent
import javax.swing.filechooser.FileFilter
import javax.swing.tree.{DefaultMutableTreeNode, DefaultTreeModel, TreePath}
import javax.swing.{JComponent, KeyStroke, TransferHandler}

import scala.collection.mutable.ListBuffer
import scala.swing.event.Key
import scala.swing.{Action, BorderPanel, BoxPanel, Button, Dialog, FileChooser, Label, MainFrame, Menu, MenuBar, MenuItem, Orientation, PopupMenu, ScrollPane, TextField}


class JsonViewMainFrame extends MainFrame {
  title = "JSON Viewer"
  size = new Dimension(800, 600)

  private val OPEN_DEFAULT_DIRECTORY: String = "OPEN_DEFAULT_DIRECTORY"
  private val jsonFactory = new JsonFactory()

  private val model = new DefaultTreeModel(new DefaultMutableTreeNode("root"))
  private val jsonTree = new TreeView(model)
  private val status = new Label("")
  private val status2 = new Label("")
  private val searchField = new TextField()


  def nextTree(tree: TreePath): Option[TreePath] = {
    var currentPath = tree

    //println(currentPath)
    if (currentPath.getLastPathComponent.asInstanceOf[JsonTreeNode].getChildCount > 0) {
      currentPath = currentPath.pathByAddingChild(currentPath.getLastPathComponent.asInstanceOf[JsonTreeNode].getChildAt(0))
    } else {
      var finished = false
      while (!finished) {
        val parent = currentPath.getParentPath
        if (parent == null) {
          return None
        } else {
          var index = 0
          while (index < parent.getLastPathComponent.asInstanceOf[JsonTreeNode].getChildCount &&
            parent.getLastPathComponent.asInstanceOf[JsonTreeNode].getChildAt(index) != currentPath.getLastPathComponent) {
            index += 1
          }
          index += 1

          if (index < parent.getLastPathComponent.asInstanceOf[JsonTreeNode].getChildCount) {
            currentPath = parent.pathByAddingChild(parent.getLastPathComponent.asInstanceOf[JsonTreeNode].getChildAt(index))
            finished = true
          } else {
            currentPath = parent
          }
        }
      }
    }
    Some(currentPath)
  }

  /*
  // Fix this
  def previousTree(tree: TreePath): Option[TreePath] = {
    var currentPath = tree

    val parent = currentPath.getParentPath


    //println(currentPath)
    if (currentPath.getLastPathComponent.asInstanceOf[JsonTreeNode].getChildCount > 0) {
      currentPath = currentPath.pathByAddingChild(currentPath.getLastPathComponent.asInstanceOf[JsonTreeNode].getChildAt(0))
    } else {
      var finished = false
      while (!finished) {
        val parent = currentPath.getParentPath
        if (parent == null) {
          return None
        } else {
          var index = 0
          while (index < parent.getLastPathComponent.asInstanceOf[JsonTreeNode].getChildCount &&
            parent.getLastPathComponent.asInstanceOf[JsonTreeNode].getChildAt(index) != currentPath.getLastPathComponent) {
            index += 1
          }
          index -= 1

          if (index >= 0) {
            currentPath = parent.pathByAddingChild(parent.getLastPathComponent.asInstanceOf[JsonTreeNode].getChildAt(index))
            finished = true
          } else {
            currentPath = parent
          }
        }
      }
    }
    Some(currentPath)
  }
  */

  private def findEvent(next: TreePath => Option[TreePath]): Unit = {
    val findText = searchField.text
    if (findText.isEmpty)
      return
    var currentPath: Option[TreePath] = Option(jsonTree.peer.getSelectionPath)
    if (currentPath.isEmpty) currentPath = Some(jsonTree.peer.getPathForRow(0))
    if (currentPath.isEmpty) return
    var isFirst = true

    while (currentPath.isDefined) {
      if (isFirst) {
        isFirst = false
      } else {
        val node = currentPath.get.getLastPathComponent.asInstanceOf[JsonTreeNode]
        node.path match {
          case Some(x) => if (x.pathAsString.contains(findText)) {
            jsonTree.peer.setSelectionPath(currentPath.get)
            jsonTree.peer.scrollPathToVisible(currentPath.get)
            return
          }
        }

        if (node.jsonValue match {
          case JsonStringValue(x, _) => x.contains(findText)
          case JsonFloatValue(x, _) => lang.Float.toString(x).contains(findText)
          case JsonIntValue(x, _) => lang.Integer.toString(x).contains(findText)
          case _ => false
        }) {
          jsonTree.peer.setSelectionPath(currentPath.get)
          jsonTree.peer.scrollPathToVisible(currentPath.get)
          return
        }
      }
      currentPath = nextTree(currentPath.get)

    }
  }

  private val actionFindNext = new Action("Find next") {
    mnemonic = Key.N.id
    accelerator = Some(KeyStroke.getKeyStroke(KeyEvent.VK_G, InputEvent.CTRL_DOWN_MASK))

    override def apply(): Unit = {
      findEvent(nextTree)
    }
  }

  searchField.action = actionFindNext

  /*
  private val actionFindPrevious = new Action("Find previous") {
    mnemonic = Key.P.id
    accelerator = Some(KeyStroke.getKeyStroke(KeyEvent.VK_G, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK))

    override def apply(): Unit = {
      println("Find previous")
      findEvent(previousTree)
    }
  }
  */

  private val searchNext = new Button(actionFindNext) {
    defaultCapable = true
  }
  //private val searchPrevious = new Button(actionFindPrevious)
  private val searchPane = new BoxPanel(Orientation.Horizontal) {
    contents += searchField
    //contents += searchPrevious
    contents += searchNext
    visible = false
  }

  jsonTree.peer.setRootVisible(false)
  private val actionCopy = new Action("Copy Content") {
    mnemonic = Key.C.id
    accelerator = Some(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK))

    override def apply(): Unit = {
      val selected = jsonTree.peer.getLastSelectedPathComponent.asInstanceOf[JsonTreeNode]
      val clipboard = jsonTree.peer.getToolkit.getSystemClipboard
      val stringData = selected.jsonValue.stringValue
      val ss = new StringSelection(stringData)
      clipboard.setContents(ss, ss)
    }
  }

  jsonTree.peer.unregisterKeyboardAction(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK))
  jsonTree.peer.setDragEnabled(true)
  jsonTree.peer.setTransferHandler(new TransferHandler() {
    override def getSourceActions(c: JComponent): Int = TransferHandler.COPY

    override def createTransferable(c: JComponent): Transferable = {
      val selected = jsonTree.peer.getLastSelectedPathComponent.asInstanceOf[JsonTreeNode]
      val stringData = selected.jsonValue.stringValue
      val ss = new StringSelection(stringData)
      ss
    }
  })

  jsonTree.peer.getSelectionModel.addTreeSelectionListener((e: TreeSelectionEvent) => {
    val pathStr = StringBuilder.newBuilder
    for (one <- e.getPath.getPath) {
      val node = one.asInstanceOf[JsonTreeNode]
      if (pathStr.nonEmpty) {
        pathStr ++= "."
      }
      node.path match {
        case Some(x) => pathStr ++= x.pathAsString
        case _ =>
      }
    }

    status.text = e.getPath.getLastPathComponent.asInstanceOf[JsonTreeNode].jsonValue match {
      case JsonMapValue(x, _) => "Object (" + x.seq.size + " items)"
      case JsonArrayValue(x, _) => "Array (" + x.seq.size + " items)"
      case JsonStringValue(x, _) => "String \"" + x + "\""
      case JsonIntValue(x, _) => "Integer " + x + ""
      case JsonFloatValue(x, _) => "Float " + x + ""
      case JsonBooleanValue(x, _) => "Boolean " + x + ""
      case JsonNullValue(_) => "Null"
      case _ => ""
    }

    status2.text = pathStr.mkString
  })

  jsonTree.peer.addMouseListener(new MouseAdapter {
    override def mousePressed(e: MouseEvent): Unit = if (!treeContextMenu(e)) {
      super.mousePressed(e)
    }

    override def mouseReleased(e: MouseEvent): Unit = if (!treeContextMenu(e)) {
      super.mouseReleased(e)
    }
  })

  contents = new BorderPanel() {
    add(new ScrollPane(jsonTree), BorderPanel.Position.Center)
    add(new BoxPanel(Orientation.Vertical) {
      contents += status
      contents += status2
      contents += searchPane
    }, BorderPanel.Position.South)
  }

  menuBar = new MenuBar {
    contents += new Menu("File") {
      mnemonic = Key.F
      contents += new MenuItem(new Action("Open") {
        mnemonic = Key.O.id
        accelerator = Some(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK))

        override def apply(): Unit = {
          val choose = new FileChooser
          val defaultDirectory = JsonViewApp.prefs.get(OPEN_DEFAULT_DIRECTORY, System.getProperty("user.home"))
          choose.peer.setCurrentDirectory(new File(defaultDirectory))
          choose.fileFilter = new FileFilter {
            override def accept(f: File): Boolean = {
              val name = f.getName
              name.endsWith(".json") || name.endsWith(".jsonl") || name.endsWith(".json.gz") || name.endsWith(".jsonl.gz") || f.isDirectory
            }

            override def getDescription: String = "Supported JSON files (.json, .jsonl, .json.gz, jsonl.gz)"
          }

          val result = choose.showOpenDialog(JsonViewMainFrame.this)
          if (result != FileChooser.Result.Approve) return

          JsonViewApp.prefs.put(OPEN_DEFAULT_DIRECTORY, choose.selectedFile.getParent)
          openFile(choose.selectedFile)
        }
      })
      contents += new MenuItem(new Action("Quit") {
        mnemonic = Key.Q.id
        accelerator = Some(KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_DOWN_MASK))

        override def apply(): Unit = JsonViewApp.quit()
      })
    }
    contents += new Menu("Edit") {
      mnemonic = Key.E
      contents += new MenuItem(actionCopy)

      contents += new MenuItem(new Action("Expand all") {
        mnemonic = Key.E.id
        accelerator = Some(KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.CTRL_DOWN_MASK))

        override def apply(): Unit = {
          var i = 0
          while (i < jsonTree.peer.getRowCount) {
            jsonTree.peer.expandRow(i)
            i += 1
          }
        }
      })

      contents += new MenuItem(new Action("Collapse all") {
        mnemonic = Key.L.id
        accelerator = Some(KeyStroke.getKeyStroke(KeyEvent.VK_L, InputEvent.CTRL_DOWN_MASK))

        override def apply(): Unit = {
          var i = jsonTree.peer.getRowCount - 1
          while (i >= 0) {
            if (i >= jsonTree.peer.getRowCount) {
              i = jsonTree.peer.getRowCount - 1
            }
            jsonTree.peer.collapseRow(i)
            i -= 1
          }
        }
      })

      contents += new MenuItem(new Action("Find") {
        mnemonic = Key.F.id
        accelerator = Some(KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_DOWN_MASK))

        override def apply(): Unit = {
          searchPane.visible = !searchPane.visible
          if (searchPane.visible) {
            searchField.requestFocus()
          }
        }
      })
      contents += new MenuItem(actionFindNext)
      //contents += new MenuItem(actionFindPrevious)
    }

    contents += new Menu("Help") {
      mnemonic = Key.H

      contents += new MenuItem(new Action("About JSON Viewer") {
        mnemonic = Key.A.id

        override def apply(): Unit = {
          Dialog.showMessage(JsonViewMainFrame.this, "JSON Viewer\nCopyright(C) 2018 Yasunobu OKAMURA\nokamura@informationsea.info", "About JSON Viewer")
        }
      })
    }
  }

  def openFile(file: File): Unit = {
    title = file.getName + "   JSON Viewer"
    if (file.getName.endsWith(".json") || file.getName.endsWith(".json.gz")) {
      val parser = if (file.getName.endsWith(".json.gz")) {
        jsonFactory.createParser(new GZIPInputStream(new FileInputStream(file)))
      } else {
        jsonFactory.createParser(file)
      }
      val value = JsonValue.load(parser)
      val node = JsonTreeNode(None, value, None)
      model.setRoot(node)
      jsonTree.peer.treeDidChange()
      jsonTree.peer.setRootVisible(true)
    } else if (file.getName.endsWith(".jsonl") || file.getName.endsWith(".jsonl.gz")) {
      val reader = new BufferedReader(new InputStreamReader(if (file.getName.endsWith(".gz")) {
        new GZIPInputStream(new FileInputStream(file))
      } else {
        new FileInputStream(file)
      }, "UTF-8"))

      val data: ListBuffer[JsonValue] = ListBuffer()
      var line = reader.readLine()
      while (line != null) {
        val value = JsonValue.load(jsonFactory.createJsonParser(line))
        data += value
        line = reader.readLine()
      }

      model.setRoot(JsonTreeNode(None, JsonArrayValue(data.toIndexedSeq, None), None))
      jsonTree.peer.treeDidChange()
      jsonTree.peer.setRootVisible(false)
    }
  }

  def keyForTreePath(treePath: TreePath): String = {
    val pathStr = StringBuilder.newBuilder

    for (one <- treePath.getPath) {
      val node = one.asInstanceOf[JsonTreeNode]
      if (pathStr.nonEmpty) {
        pathStr ++= "."
      }
      node.path match {
        case Some(x) => pathStr ++= x.pathAsString
        case _ =>
      }
    }

    pathStr.mkString
  }

  def copyString(string: String): Unit = {
    val clipboard = jsonTree.peer.getToolkit.getSystemClipboard
    val ss = new StringSelection(string)
    clipboard.setContents(ss, ss)
  }

  def treeContextMenu(event: MouseEvent): Boolean = {
    if (event.isPopupTrigger) {
      val path = jsonTree.peer.getPathForLocation(event.getX, event.getY)
      if (path == null) {
        return false
      }
      val context = new PopupMenu {
        contents += new MenuItem(Action("Copy Content") {
          copyString(path.getLastPathComponent.asInstanceOf[JsonTreeNode].jsonValue.stringValue)
        })
        contents += new MenuItem(Action("Copy Key") {
          copyString(keyForTreePath(path))
        })
        contents += new MenuItem(Action("Expand All") {
          var i = jsonTree.peer.getRowForPath(path)
          while (i < jsonTree.peer.getRowCount && path.isDescendant(jsonTree.peer.getPathForRow(i))) {
            jsonTree.peer.expandRow(i)
            i += 1
          }
        })
      }
      context.show(jsonTree, event.getX, event.getY)
      true
    } else {
      false
    }
  }
}
