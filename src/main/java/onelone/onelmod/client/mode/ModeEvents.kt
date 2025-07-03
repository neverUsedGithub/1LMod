package onelone.onelmod.client.mode

import com.mojang.datafixers.TypeRewriteRule.One
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.fabricmc.fabric.api.entity.event.v1.EntityElytraEvents.Custom
import net.minecraft.component.ComponentType
import net.minecraft.component.DataComponentTypes
import net.minecraft.component.type.CustomModelDataComponent
import net.minecraft.item.Items
import net.minecraft.network.listener.PacketListener
import net.minecraft.network.packet.Packet
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket
import net.minecraft.network.packet.s2c.play.InventoryS2CPacket
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket
import onelone.onelmod.client.OneLClient
import onelone.onelmod.client.mode.modes.BuildMode
import onelone.onelmod.client.mode.modes.DevMode
import onelone.onelmod.client.mode.modes.PlayMode
import onelone.onelmod.client.mode.modes.SpawnMode

class ModeEvents {
    private val joinRegex = Regex("^» Joined game: (.+?) by ([A-Za-z_0-9]+)\\.$")

    init {
        ClientReceiveMessageEvents.GAME.register { text, _ ->
            if (text.string.equals("» You are now in dev mode.")) OneLClient.setMode(DevMode)
            if (text.string.equals("» You are now in build mode.")) OneLClient.setMode(BuildMode)
            if (text.string.matches(joinRegex)) {
                val match = joinRegex.find(text.string)!!
                val (owner, plot) = match.destructured

                OneLClient.setMode(PlayMode)
                PlayMode.currentPlot = Pair(owner, plot)
            }
        }
    }

    fun <T : PacketListener> handlePacket(packet: Packet<T>, listener: PacketListener) {
        when (packet) {
            is ScreenHandlerSlotUpdateS2CPacket -> {
                if (!OneLClient.onDiamondFire()) return
                val syncId = OneLClient.mc.player!!.currentScreenHandler.syncId

                if (packet.syncId != syncId ||
                    packet.slot != 40 ||
                    packet.stack.isEmpty ||
                    packet.stack.item != Items.EMERALD ||
                    packet.stack.name.string != "◇ Game Menu ◇") return

                val modelData = packet.stack.components.get(DataComponentTypes.CUSTOM_MODEL_DATA)
                val customData = packet.stack.components.get(DataComponentTypes.CUSTOM_DATA)

                if (modelData == null || customData == null || customData.isEmpty || !modelData.floats.contains(5053f)) return

                val compound = customData.copyNbt()
                val bukkitValues = compound.getCompound("PublicBukkitValues")
                if (bukkitValues.isEmpty || !bukkitValues.get().contains("hypercube:item_instance")) return

                // {minecraft:custom_name=>empty[style={!italic}, siblings=[literal{◇ }[style={color=red}], literal{Game Menu}[style={color=green}], literal{ ◇}[style={color=red}]]], minecraft:lore=>LoreComponent[lines=[empty[siblings=[literal{Click to open the Game Menu.}[style={color=gray,!bold,!italic,!underlined,!strikethrough,!obfuscated}]]], empty[siblings=[literal{Hold and type in chat to search.}[style={color=gray,!bold,!italic,!underlined,!strikethrough,!obfuscated}]]]], styledLines=[empty[style={color=dark_purple,italic}, siblings=[literal{Click to open the Game Menu.}[style={color=gray,!bold,!italic,!underlined,!strikethrough,!obfuscated}]]], empty[style={color=dark_purple,italic}, siblings=[literal{Hold and type in chat to search.}[style={color=gray,!bold,!italic,!underlined,!strikethrough,!obfuscated}]]]]], minecraft:custom_model_data=>CustomModelDataComponent[value=5053], minecraft:hide_additional_tooltip=>INSTANCE, minecraft:attribute_modifiers=>AttributeModifiersComponent[modifiers=[], showInTooltip=false], minecraft:damage=>0, minecraft:custom_data=>{PublicBukkitValues:{"hypercube:item_instance":"b052690b-4f6a-49db-9d53-b1c722f7d6ac"}}, minecraft:max_stack_size=>64, minecraft:enchantments=>ItemEnchantments{enchantments={}, showInTooltip=true}, minecraft:repair_cost=>0, minecraft:rarity=>COMMON}

                if (OneLClient.mode != SpawnMode) OneLClient.setMode(SpawnMode)
            }
        }
    }
}