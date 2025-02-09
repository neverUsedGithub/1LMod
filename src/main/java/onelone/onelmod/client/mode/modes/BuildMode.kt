package onelone.onelmod.client.mode.modes

import net.minecraft.item.Item
import net.minecraft.item.Items
import net.minecraft.text.Text
import onelone.onelmod.client.mode.DFMode

object BuildMode : DFMode() {
    override val name: Text = Text.translatable("onelmod.mode.build")
    override val icon: Item = Items.COBBLESTONE

    override fun onEnable() {}
    override fun onDisable() {}
    override fun onTick() {}
}