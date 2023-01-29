package studio.coni.epsilon.management

import studio.coni.epsilon.command.Command
import studio.coni.epsilon.command.ExecutionScope
import studio.coni.epsilon.common.interfaces.Helper
import studio.coni.epsilon.command.commands.*
import studio.coni.epsilon.module.setting.GuiSetting
import java.util.*

object CommandManager : Helper {

    private val prefixSetting = GuiSetting.prefix
    var prefix by prefixSetting

    val commands = mutableListOf<Command>()

    init {
        commands.add(Config)
        commands.add(FP)
        commands.add(Friend)
        commands.add(Help)
        commands.add(Prefix)
        commands.add(Toggle)
        commands.add(TP)
        commands.add(UnFriend)
    }

    fun String.runCommand(): Boolean {
        val cachedPrefix = prefix
        if (!this.startsWith(cachedPrefix)) return false

        val queue = LinkedList<String>()

        removePrefix(cachedPrefix).split(" ").forEach { queue.add(it) }

        queue.poll()?.let {

            commands.forEach { command ->
                if (command.prefix.equals(it, true)) {
                    command.block(ExecutionScope(queue.toList().toTypedArray(), command))
                    return true
                }
            }

        }
        return false
    }

}