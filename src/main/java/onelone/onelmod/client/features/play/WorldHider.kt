package onelone.onelmod.client.features.play

import onelone.onelmod.client.OneLClient
import onelone.onelmod.client.features.Feature

object WorldHider : Feature() {
    override fun onTick() {}

    override fun onEnable() {
        OneLClient.shouldWorldRerender = true
    }

    override fun onDisable() {
        OneLClient.shouldWorldRerender = true
    }
}