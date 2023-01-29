package studio.coni.epsilon.command.commands

import studio.coni.epsilon.command.Command
import studio.coni.epsilon.module.misc.FakePlayer
import studio.coni.epsilon.util.text.ChatUtil

object FP : Command(
    name = "FakePlayer",
    prefix = "fp",
    description = "Toggle fake player",
    syntax = "fp",
    block = {
        FakePlayer.toggle()
        ChatUtil.printChatMessage("Toggled fake player!")
    }
)