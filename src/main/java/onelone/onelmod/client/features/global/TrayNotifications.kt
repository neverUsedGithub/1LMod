package onelone.onelmod.client.features.global

import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import onelone.onelmod.client.OneLClient
import onelone.onelmod.client.OneLClient.onDiamondFire
import onelone.onelmod.client.config.Config
import onelone.onelmod.client.toast.OneLToast
import java.awt.SystemTray
import java.awt.Toolkit
import java.awt.TrayIcon

class TrayNotifications {
    private val dmRegex = Regex("^\\[([a-zA-Z0-9_]{2,16}) → You\\] (.*)$")

    init {
        System.setProperty("java.awt.headless", "false")
    }

    fun load() {
        if (SystemTray.isSupported()) {
            val tray = SystemTray.getSystemTray()

            val iconStream = OneLClient.mc.resourceManager
                .getResource(Identifier.of("onelmod", "onelmod.png"))
                .get()
                .inputStream

            val trayImage = Toolkit.getDefaultToolkit().createImage(iconStream.readBytes())
            iconStream.close()

            val trayIcon = TrayIcon(trayImage, "1L Mod")
            trayIcon.isImageAutoSize = true
            tray.add(trayIcon)

            ClientReceiveMessageEvents.GAME.register { text, _ ->
                if (!onDiamondFire()) return@register
                if (OneLClient.mc.isWindowFocused) return@register
                if (!Config.directMessageNotifications) return@register
                if (!text.string.matches(dmRegex)) return@register
                val (author, message) = dmRegex.find(text.string)!!.destructured

                val caption = Text.translatable("onelmod.notification.direct_message", author).string
                trayIcon.displayMessage(caption, message, TrayIcon.MessageType.INFO)
            }
        }
    }
}