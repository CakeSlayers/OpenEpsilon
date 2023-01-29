package studio.coni.epsilon.command.commands

import studio.coni.epsilon.command.Command
import studio.coni.epsilon.command.execute
import studio.coni.epsilon.management.ModuleManager
import studio.coni.epsilon.util.text.ChatUtil

object Toggle : Command(
    name = "Toggle",
    prefix = "toggle",
    description = "Toggle module",
    syntax = "toggle <module>",
    block = {
        execute { name ->
            ModuleManager.modules.forEach {
                if (it.name.equals(name, ignoreCase = true)) {
                    it.toggle()
                    ChatUtil.printChatMessage("Toggled ${name}!")
                }
            }
        }
    }
)