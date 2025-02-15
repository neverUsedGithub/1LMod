package onelone.onelmod.mixins;

import net.minecraft.client.network.ClientCommonNetworkHandler;
import net.minecraft.item.Items;
import net.minecraft.network.packet.Packet;
import net.minecraft.text.Text;
import onelone.onelmod.client.OneLClient;
import onelone.onelmod.client.config.Config;
import onelone.onelmod.client.toast.OneLToast;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientCommonNetworkHandler.class)
public class ClientCommonNetworkHandlerMixin {
    @Inject(method = "onPacketException", at = @At("HEAD"), cancellable = true)
    public void onPacketException(Packet<?> packet, Exception exception, CallbackInfo ci) {
        if (OneLClient.INSTANCE.onDiamondFire() && Config.INSTANCE.getNoNetworkProtocol()) {
            OneLToast.Companion.show(Items.BARRIER, Text.of("Network Protocol Error"), Text.translatable("onelmod.toast.network_protocol_error"));
            ci.cancel();
        }
    }
}
