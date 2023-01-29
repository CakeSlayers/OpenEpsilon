package studio.coni.epsilon.command.commands

import studio.coni.epsilon.command.Command
import studio.coni.epsilon.command.execute
import studio.coni.epsilon.util.Wrapper
import studio.coni.epsilon.util.text.ChatUtil

object TP : Command(
    name = "TP",
    prefix = "tp",
    description = "Teleport you to the coords",
    syntax = "tp <x> <y> <z>",
    block = {
        try {
            execute { x ->
                execute { y ->
                    execute { z ->
                        Wrapper.mc.player?.setPosition(x.toDouble(), y.toDouble(), z.toDouble())
                        ChatUtil.printChatMessage("Teleported you to $x $y $z")
                    }
                }
            }
        } catch (ignore: Exception) {
            ChatUtil.printChatMessage("Usage : ${TP.syntax}")
        }
    }
)