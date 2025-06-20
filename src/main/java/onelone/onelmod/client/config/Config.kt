package onelone.onelmod.client.config

import onelone.onelmod.client.config.builder.*
import onelone.onelmod.client.features.play.WorldHider

@ConfigProvider(namespace = "onelmod", categoryOrder = ["general"])
@ConfigGroup(name = "code_chest", image = "textures/gui/codeblocks.png", imageWidth = 591, imageHeight = 361)
object Config {
    val handler = AutoConfig(Config::class, Config::settingChanged)

    @ConfigField(category = "general", group = "code_chest", webpImage = "textures/gui/middle_click_action.webp",)
    var middleClickAction = true

    @ConfigField(category = "general", group = "misc")
    var noNetworkProtocol = true

    @ConfigField(category = "general", group = "misc")
    var noUnverifiedToast = true

    @ConfigField(category = "general", group = "misc")
    var modeChangeToasts = false

    @ConfigField(category = "general", group = "misc")
    var directMessageNotifications = false

    private fun settingChanged(setting: String, value: Any) {}
}