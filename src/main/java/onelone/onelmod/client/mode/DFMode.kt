package onelone.onelmod.client.mode

import net.minecraft.item.Item
import net.minecraft.text.Text
import onelone.onelmod.client.OneLClient
import onelone.onelmod.client.features.Feature

abstract class DFMode(val features: Array<Feature?> = arrayOf()) {
    abstract val name: Text
    abstract val icon: Item

    fun enable() {
        onEnable()
        for (feature in features) feature?.enable()
    }

    fun disable() {
        onDisable()
        for (feature in features) feature?.disable()
    }

    fun tick() {
        onTick()
        for (feature in features) feature?.tick()
    }

    protected abstract fun onEnable()
    protected abstract fun onDisable()
    protected abstract fun onTick()
}