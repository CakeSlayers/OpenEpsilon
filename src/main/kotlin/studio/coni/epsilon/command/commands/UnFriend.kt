package studio.coni.epsilon.command.commands

import studio.coni.epsilon.command.Command
import studio.coni.epsilon.command.execute
import studio.coni.epsilon.management.AltManager
import studio.coni.epsilon.management.FriendManager
import studio.coni.epsilon.util.text.ChatUtil
import studio.coni.epsilon.util.text.ChatUtil.AQUA
import studio.coni.epsilon.util.text.ChatUtil.RESET

object UnFriend : Command(
    name = "UnFriend",
    prefix = "unfriend",
    description = "Delete friend",
    syntax = "unfriend <name>",
    block = {
        execute { name ->
            Thread {
                val uuid = AltManager.getUUID(name)
                if (uuid.isEmpty()) {
                    ChatUtil.printChatMessage("Cannot find a player called $AQUA$name")
                } else {
                    if (FriendManager.removeFriend(uuid)) {
                        ChatUtil.printChatMessage("$AQUA$name$RESET has been unfriended.")
                    } else {
                        ChatUtil.printChatMessage("$AQUA$name$RESET is not your friend.")
                    }
                }
                Thread.currentThread().interrupt()
            }.start()
        }
    }
)
