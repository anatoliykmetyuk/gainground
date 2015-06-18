import subscript.file

import scala.swing._
import subscript.swing.SimpleSubscriptApplication
import subscript.swing.Scripts._

object Main extends SimpleSubscriptApplication {
  
  def getTitle: String = "Sample swing app"

  val A = new Button("A")           {enabled       = false}
  val B = new Button("B")           {enabled       = false}
  val X = new Button("Exit")        {enabled       = false}
  val ABLabel  = new Label("..A;B") {preferredSize = new Dimension(45,26)}
  val outputTA = new TextArea       {editable      = false}

  val top          = new MainFrame {
    title          = getTitle 
    location       = new Point    (0,0)
    preferredSize  = new Dimension(300,70)
    contents       = new BorderPanel {
      add(new FlowPanel(A, B, X, ABLabel), BorderPanel.Position.North) 
    }
  }

  override def live = subscript.DSL._execute(liveScript)

  script..
    liveScript = A B ... || doExit
    doExit = X
           
}