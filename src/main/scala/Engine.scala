package gainground

trait Sprite {
  def symbol: String
  def size = symbol.size

  def bound: Int = Int.MaxValue

  var _position: Int
  def position_=(newPos: Int) {
    _position = if (newPos >= bound) bound - 1
           else if (newPos < 0     ) 0
           else                      newPos
  }
  def position = _position  

  def collidesWith(another: Sprite) =
    (position <= another.position && position + size - 1 >= another.position                   ) ||
    (position >  another.position && position            <= another.position + another.size - 1)
  
  def forward (d: Int = 1) = position += d
  def backward(d: Int = 1) = position -= d
}

class SimpleSprite  (var _position: Int, val symbol: String, override val bound: Int = Int.MaxValue) extends Sprite
class VariableSprite(var _position: Int, var symbol: String, override val bound: Int = Int.MaxValue) extends Sprite

trait Display {
  type Surface
  def newSurface: Surface

  def flush(sfc: Surface): Unit

  val size       : Int

  var sprites: List[Sprite] = List()
  def add   (objs: Sprite*): Unit = sprites ++= objs
  def remove(objs: Sprite*): Unit = sprites = sprites diff objs
  def clear ()             : Unit = sprites = List()

  def draw(s: Sprite, sfc: Surface): Unit

  def update(): Unit = {
    val sfc = newSurface
    for (s <- sprites) draw(s, sfc)
    flush(sfc)
  }
}

class ConsoleDisplay(val size: Int) extends Display {
  type Surface = StringBuffer
  def newSurface = new StringBuffer(" " * size)

  override def draw (s  : Sprite, sfc: Surface): Unit = sfc.replace(s.position, s.position + s.size, s.symbol)
  override def flush(sfc: Surface             ): Unit = print(s"\r${" " * size}\r$sfc")
}

class Trigger {
  var listeners = List[() => Unit]()
  def trigger = listeners.foreach(_())
  def addListener(f: () => Unit) {listeners ::= f}
}