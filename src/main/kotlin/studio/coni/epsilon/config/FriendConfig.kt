package studio.coni.epsilon.config

import studio.coni.epsilon.EpsilonPlus
import studio.coni.epsilon.common.extensions.isNotExist
import studio.coni.epsilon.management.FriendManager

class FriendConfig : Config("Friend.json") {
    override val dirPath = EpsilonPlus.DEFAULT_CONFIG_PATH + "config/"

    override fun saveConfig() {
        if (configFile.isNotExist()) {
            configFile.parentFile.mkdirs()
            configFile.createNewFile()
        } else {
            val friendList = FriendManager.friendList
            var text = ""
            for (friend in friendList) {
                text += friend + "\n"
            }
            configFile.writeText(text)
        }
    }

    override fun loadConfig() {
        if (configFile.exists()) {
            FriendManager.friendList.addAll(configFile.readLines())
        }
    }
}