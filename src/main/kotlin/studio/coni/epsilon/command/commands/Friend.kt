package studio.coni.epsilon.command.commands

import studio.coni.epsilon.command.Command
import studio.coni.epsilon.command.execute
import studio.coni.epsilon.management.AltManager
import studio.coni.epsilon.management.FriendManager
import studio.coni.epsilon.util.text.ChatUtil
import studio.coni.epsilon.util.text.ChatUtil.AQUA
import studio.coni.epsilon.util.text.ChatUtil.RESET

object Friend : Command(
    name = "Friend",
    prefix = "friend",
    description = "Add friend",
    syntax = "friend <name>",
    block = {
        execute { name ->
            Thread {
                val uuid = AltManager.getUUID(name)
                if (uuid.isEmpty()) {
                    ChatUtil.printChatMessage("Cannot find a player called $AQUA$name")
                } else {
                    if (FriendManager.addFriend(uuid)) {
                        ChatUtil.printChatMessage("$AQUA$name$RESET has been friended.")
                    } else {
                        ChatUtil.printChatMessage("$AQUA$name$RESET already is your friend.")
                    }
                }
                Thread.currentThread().interrupt()
            }.start()
        }
    }
)
