package onelone.onelmod.client.features

import onelone.onelmod.client.OneLClient

abstract class Feature(private var state: Boolean = true) {

    fun setState(state: Boolean) {
        if (state == this.state) return
        this.state = state

        if (state) this.enable()
        else this.disable()
    }

    fun isEnabled(): Boolean {
        return state && OneLClient.mode != null && OneLClient.mode?.features?.contains(this) == true
    }

    fun enable() {
        if (isEnabled()) onEnable()
    }

    fun disable() {
        if (!isEnabled()) onDisable()
    }

    fun tick() {
        if (isEnabled()) onTick()
    }

    protected abstract fun onTick()
    protected abstract fun onEnable()
    protected abstract fun onDisable()
}