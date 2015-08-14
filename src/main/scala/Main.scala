package gainground

import scala.language.implicitConversions

import subscript.file

import scala.swing._
import scala.swing.event._

import subscript.swing.SimpleSubscriptApplication
import subscript.swing.Scripts._

object Main extends SimpleSubscriptApplication with Base {
  override def live = subscript.DSL._execute(liveScript)
  
  implicit script..
    key    (??c: Char     )      =  key2:top, ??c
    trigger(  t: Trigger  )      = @{t.addListener {() => there.codeExecutor.executeAA}}: {. .}

  script..
    // Framework
    liveScript  = [world && update] / exit
    exit        = 'x'
    continueCmd = 'r'

    world = let messageObj.symbol = "Welcome! R - Start/Restart/Continue, A - back, D - forward, X - exit"
            display.add: messageObj
            continueCmd
            [init ; [player && zombie && timer] / end] ...

    init = display.clear()
           display.add: board, playerChar, zombieChar, timerInd, levelInd, scoreInd, totalScoreInd

    end = gameOverSequence || victorySequnece

    gameOverSequence = gameOver
                       resetWithMessage: "Game Over. Press R to restart."
                       let level      = 1
                       let totalScore = 0

    victorySequnece  = victory
                       let totalScore += score
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
    player         = playerControls && playerSensors
    playerControls = [keymap / ..] ...
    keymap         =   'd' playerChar.forward()
                     + 'a' playerChar.backward()

    playerSensors = {!if (playerChar collidesWith zombieChar) gameOver.trigger!} ...


    // Zombie
    zombie   = zombieAI delay: zombieStepDelay ...
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