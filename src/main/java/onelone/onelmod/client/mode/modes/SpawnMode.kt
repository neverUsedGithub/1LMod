package onelone.onelmod.client.mode.modes

import net.minecraft.item.Item
import net.minecraft.item.Items
import net.minecraft.text.Text
import onelone.onelmod.client.mode.DFMode

object SpawnMode : DFMode() {
    override val name: Text = Text.translatable("onelmod.mode.spawn")
    override val icon: Item = Items.EMERALD

    override fun onEnable() {}
    override fun onDisable() {}
    override fun onTick() {}
}