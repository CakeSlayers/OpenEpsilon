package studio.coni.epsilon.command.commands

import studio.coni.epsilon.command.Command
import studio.coni.epsilon.command.execute
import studio.coni.epsilon.management.CommandManager
import studio.coni.epsilon.util.Wrapper
import studio.coni.epsilon.util.text.ChatUtil
import net.minecraft.client.audio.PositionedSoundRecord
import net.minecraft.init.SoundEvents

object Prefix : Command(
    name = "Prefix",
    prefix = "prefix",
    description = "Change command prefix",
    syntax = "prefix <char>",
    block = {
        execute { newPrefix ->
            if (newPrefix.length != 1) {
                ChatUtil.sendNoSpamErrorMessage("Please specify a new prefix!")
            } else {
                ChatUtil.sendNoSpamMessage("Prefix set to " + ChatUtil.SECTION_SIGN + "b" + newPrefix + ChatUtil.SECTION_SIGN + "r" + " !")
                CommandManager.prefix = newPrefix
                Wrapper.mc.soundHandler.playSound(
                    PositionedSoundRecord.getMasterRecord(
                        SoundEvents.BLOCK_ANVIL_USE,
                        1F
                    )
                )
            }
        }
    }
)