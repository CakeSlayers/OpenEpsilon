package studio.coni.epsilon.command.commands

import studio.coni.epsilon.command.Command
import studio.coni.epsilon.command.execute
import studio.coni.epsilon.config.ConfigManager
import studio.coni.epsilon.util.text.ChatUtil

object Config : Command(
    name = "Config",
    prefix = "config",
    description = "Used for save or load config",
    syntax = "config <save/load>",
    block = {
        execute {
            if (it.equals("save", true)) {
                ConfigManager.saveAll()
                ChatUtil.printChatMessage("Saved all Spartan configs")
            } else if (it.equals("load", true)) {
                ConfigManager.loadAll()
                ChatUtil.printChatMessage("Loaded all Spartan configs")
            }
        }
    }
)

