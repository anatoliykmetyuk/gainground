package gainground

import scala.language.implicitConversions
import subscript.file

import subscript.swing.SimpleSubscriptApplication
import subscript.swing.Scripts._

object Main extends SimpleSubscriptApplication with Base {
  override def live = subscript.DSL._execute(liveScript)

  case class Landmine(var _position: Int) extends Sprite {
    var timeToExplosion = 3
    var symbol = timeToExplosion.toString

    script..
      live = [
        delay: 1000
        let timeToExplosion -= 1
        let symbol = timeToExplosion.toString
        while (timeToExplosion > 0)
      ] explode

      explode = let symbol   = "<--X-->"
                let position -= 3
                if this.collidesWith(playerChar) then playerDies.trigger
                if this.collidesWith(zombieChar) then zombieDies.trigger
                delay: 250
                display.remove: this

  }

  implicit script..
    key    (??c: Char     )      =  key2:top, ??c
    trigger(  t: Trigger  )      = @{t.addListener {() => there.codeExecutor.executeAA}}: {. .}

  script..
    // Framework
    liveScript  = [world && update] / exitCmd
    exitCmd     = 'x'
    continueCmd = 'r'

    world = let messageObj.symbol = "Welcome! R - Start/Restart/Continue, A - back, D - forward, X - exit"
            display.add: messageObj
            continueCmd
            [init ; (** player && zombie && timer **) / end] ...

    init = display.clear()
           display.add: board, playerChar, zombieChar, timerInd, levelInd, landminesInd, scoreInd, totalScoreInd

    end = gameOverSequence || victorySequnece

    gameOverSequence = gameOver
                       resetWithMessage: "Game Over. Press R to restart."
                       let level      = 1
                       let totalScore = 0
                       let landminesCount = 3

    victorySequnece  = victory
                       let totalScore += score
                       if score > 10 then let landminesCount += 1
                       resetWithMessage: "Victory! Press R to continue."
                       let level += 1

    resetWithMessage(msg: String) = display.remove: board, playerChar, zombieChar
                                    let messageObj.symbol = msg
                                    display.add: messageObj
                                    continueCmd
                                    let playerChar.position = 0
                                    let zombieChar.position = board.size - 1
                                    let timeLeft = roundTime

    // Player
    player         = playerControls && playerSensors / playerDies delay: 250 gameOver.trigger
    playerControls = [keymap / ..] ...
    keymap         =   'd' playerChar.forward()
                     + 'a' playerChar.backward()
                     + 'm' [if landminesCount > 0 then placeLandmine]

    playerSensors = {!if (playerChar.collidesWith(zombieChar) && display.sprites.contains(zombieChar)) gameOver.trigger!} ...

    placeLandmine = var landmine = new Landmine(playerChar.position)
                    display.add: landmine
                    here.launch: landmine.live
                    let landminesCount -= 1

    // Zombie
    zombie   = zombieAI delay: zombieStepDelay ... / zombieDies display.remove: zombieChar
    zombieAI = var goToPlayer = math.random < zombieGoingToPlayerChance
               if goToPlayer then zombieChar.backward() else zombieChar.forward()

    // Timer
    timer = [
      delay: 1000
      let timeLeft -= 1
      while (timeLeft > 0)
    ] victory.trigger

    // Graphics
    update = delay: screenUpdateDelay
             display.update()
             ...

    delay(time: Long) = {* Thread sleep time *}

}