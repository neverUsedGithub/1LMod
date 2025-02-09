package onelone.onelmod.client

import net.fabricmc.api.ClientModInitializer

class ClientInitializer : ClientModInitializer {
    override fun onInitializeClient() {
        OneLClient.onInitialize()
    }
}