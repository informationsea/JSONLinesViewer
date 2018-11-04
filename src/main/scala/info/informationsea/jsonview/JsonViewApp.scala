package info.informationsea.jsonview

import java.awt.Dimension
import java.util.prefs.Preferences

import javax.swing.UIManager

import scala.swing.{Frame, SimpleSwingApplication}

object JsonViewApp extends SimpleSwingApplication {
  val prefs = Preferences.userNodeForPackage(getClass)

  override def top: Frame = {
    System.setProperty("awt.useSystemAAFontSettings", "on")
    UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
    //UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName)
    new JsonViewMainFrame
  }

  override def startup(args: Array[String]): Unit = {
    val topFrame = top
    topFrame.size = new Dimension(300, 300)
    topFrame.visible = true
  }
}
