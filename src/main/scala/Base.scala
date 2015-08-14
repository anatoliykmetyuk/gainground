package gainground

import scala.swing._
import scala.swing.event._

import subscript.swing.SimpleSubscriptApplication
import subscript.swing.Scripts._

trait Base extends ModelBase with ViewBase

trait ModelBase {
  // Domain classes
  case class Character (pos: Int, sym: String) extends SimpleSprite  (pos, sym, board.size) {
    val death = new Trigger
  }

  case class Decoration(pos: Int, sym: String) extends VariableSprite(pos, sym)
  case class Indicator(var _position: Int, sym: () => String) extends Sprite {
    def symbol = sym()
  }

  // Scene objects
  val display    = new ConsoleDisplay(100)

  val board      = Decoration(0             , "_" * 20   )
  val messageObj = Decoration(3             , ""         )

  val playerChar = Character (0             , "P->"      )
  val zombieChar = Character (board.size - 1, "<-Z"      )
  
  val timerInd      = Indicator(40, () => s"Time: $timeLeft"   )
  val levelInd      = Indicator(50, () => s"Level: $level"     )
  val landminesInd  = Indicator(60, () => s"Mines: $landminesCount")
  val scoreInd      = Indicator(70, () => s"Score: $score"     )
  val totalScoreInd = Indicator(80, () => s"Total: $totalScore")

  // Triggers
  val gameOver   = new Trigger
  val victory    = new Trigger
  
  // Constants
  val roundTime         = 10
  val zombieStepDelay   = 500
  val screenUpdateDelay = 10

  // Game state
  var timeLeft   = roundTime
  var level      = 1
  def score      = playerChar.position + 1
  var totalScore = 0
  def zombieGoingToPlayerChance = 2 * math.atan(level) / math.Pi
  var landminesCount = 3
}

trait ViewBase {
  val target = new Label("Focus me to capture keys from the keyboard") {focusable = true}

  val top          = new MainFrame {
    title          = "Gain the ground" 
    location       = new Point    (0,0)
    preferredSize  = new Dimension(300,70)
    contents       = new BorderPanel {
      add(target, BorderPanel.Position.Center) 
    }
  }

  top.listenTo(target.keys)
}