package onelone.onelmod.mixins;

import net.minecraft.network.ClientConnection;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.Packet;
import onelone.onelmod.client.OneLClient;
import onelone.onelmod.client.mode.ModeEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientConnection.class)
abstract public class ClientConnectionMixin {
    @Inject(method = "handlePacket", at = @At("HEAD"))
    private static <T extends PacketListener> void handlePacket(Packet<T> packet, PacketListener listener, CallbackInfo ci) {
        OneLClient.INSTANCE.getModeEvents().handlePacket(packet, listener);
    }
}
