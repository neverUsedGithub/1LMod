package onelone.onelmod.client

import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.fabricmc.fabric.api.networking.v1.PacketSender
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayNetworkHandler
import net.minecraft.item.ItemStack
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import onelone.onelmod.client.commands.OneLCommand
import onelone.onelmod.client.config.Config
import onelone.onelmod.client.features.global.TrayNotifications
import onelone.onelmod.client.mode.DFMode
import onelone.onelmod.client.mode.ModeEvents
import onelone.onelmod.client.toast.OneLToast.Companion.show
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.awt.SystemTray
import java.awt.Toolkit
import java.awt.TrayIcon

object OneLClient {
    val mc: MinecraftClient = MinecraftClient.getInstance()
    var mode: DFMode? = null
        private set
    var modeEvents: ModeEvents? = null
    val logger: Logger = LoggerFactory.getLogger("OneL Mod")
    var shouldWorldRerender = false

    fun onInitialize() {
        modeEvents = ModeEvents()

        val trayNotifications = TrayNotifications()

        ClientLifecycleEvents.CLIENT_STARTED.register {
            Config.handler.load()
            trayNotifications.load()
        }
        ClientPlayConnectionEvents.JOIN.register { _: ClientPlayNetworkHandler, _: PacketSender, _: MinecraftClient ->
            if (!onDiamondFire()) {
                setMode(null)
            }
        }
        ClientCommandRegistrationCallback.EVENT.register { dispatcher, _ -> OneLCommand.register(dispatcher) }
        ClientTickEvents.START_CLIENT_TICK.register {
            if (shouldWorldRerender) {
                shouldWorldRerender = false
                mc.worldRenderer.reload()
            }
            mode?.tick()
        }
    }

    fun setMode(mode: DFMode?) {
        if (mode != null && Config.modeChangeToasts) {
            show(
                ItemStack(mode.icon),
                Text.of("Changed Mode"),
                mode.name
            )
        }

        this.mode?.disable()
        this.mode = mode
        mode?.enable()
    }

    private fun formatText(str: String): Text {
        return formatText(Text.of(str))
    }

    private fun formatText(str: Text): Text {
        return Text.literal("")
            .append(Text.of("§6§l[1LMod] "))
            .append(str)
    }

    fun logChat(str: String) {
        mc.player!!.sendMessage(formatText(str), false)
    }

    fun logChat(text: Text) {
        mc.player!!.sendMessage(formatText(text), false)
    }

    fun onDiamondFire(): Boolean {
        return (mc.currentServerEntry ?: return false).address.endsWith("mcdiamondfire.com")
    }
}