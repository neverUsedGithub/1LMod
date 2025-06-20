package onelone.onelmod.client.mode.modes

import net.minecraft.item.Item
import net.minecraft.item.Items
import net.minecraft.text.Text
import onelone.onelmod.client.features.play.WorldHider
import onelone.onelmod.client.mode.DFMode

object PlayMode : DFMode(features = arrayOf(WorldHider)) {
    override val name: Text = Text.translatable("onelmod.mode.play")
    override val icon: Item = Items.BOW

    var currentPlot: Pair<String, String>? = null;

    override fun onEnable() {}
    override fun onDisable() {}
    override fun onTick() {}
}