package studio.coni.epsilon.command.commands

import studio.coni.epsilon.EpsilonPlus.VERSION
import studio.coni.epsilon.command.Command
import studio.coni.epsilon.command.execute
import studio.coni.epsilon.management.CommandManager
import studio.coni.epsilon.management.ModuleManager
import studio.coni.epsilon.module.client.RootGUI
import studio.coni.epsilon.util.text.ChatUtil
import studio.coni.epsilon.util.text.ChatUtil.BLUE
import studio.coni.epsilon.util.text.ChatUtil.DARK_AQUA
import studio.coni.epsilon.util.text.ChatUtil.GOLD
import studio.coni.epsilon.util.text.ChatUtil.GRAY
import studio.coni.epsilon.util.text.ChatUtil.YELLOW

object Help : Command(
    name = "Help",
    prefix = "help",
    description = "Help command",
    syntax = "help",
    block = {
        var returned = false

        execute { moduleName ->
            ModuleManager.modules.find { it.name.equals(moduleName, true) }?.let {
                ChatUtil.printChatMessage("[${it.name}]${it.description}")
                returned = true
            }
        }

        if (!returned) {
            ChatUtil.printChatMessage("${BLUE}Epsilon+ ${DARK_AQUA}V$VERSION")
            ChatUtil.printChatMessage("${BLUE}Author ${DARK_AQUA}B312 KillRED")
            ChatUtil.printChatMessage("${BLUE}ClickGUI ${DARK_AQUA}${RootGUI.keyBind.displayValue}")
            ChatUtil.printChatMessage("${BLUE}CommandPrefix ${DARK_AQUA}${CommandManager.prefix}")
            ChatUtil.printChatMessage("${BLUE}Available Commands : ")
            CommandManager.commands.forEach {
                ChatUtil.printChatMessage("${GOLD}${it.name} ${YELLOW}${it.syntax} ${GRAY}[${it.description}]")
            }
        }
    }
)