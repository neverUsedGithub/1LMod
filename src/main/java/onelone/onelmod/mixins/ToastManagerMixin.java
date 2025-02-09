package onelone.onelmod.mixins;

import net.minecraft.client.toast.SystemToast;
import net.minecraft.client.toast.Toast;
import net.minecraft.client.toast.ToastManager;
import onelone.onelmod.client.config.Config;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ToastManager.class)
public class ToastManagerMixin {
    @Inject(method = "add", at = @At("HEAD"), cancellable = true)
    public void add(Toast toast, CallbackInfo ci) {
        if (toast instanceof SystemToast systemToast &&
            systemToast.getType() == SystemToast.Type.UNSECURE_SERVER_WARNING &&
            Config.INSTANCE.getNoUnverifiedToast()) {
            ci.cancel();
        }
    }
}
