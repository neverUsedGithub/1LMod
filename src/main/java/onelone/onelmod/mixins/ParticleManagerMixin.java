package onelone.onelmod.mixins;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.util.math.BlockPos;
import onelone.onelmod.client.OneLClient;
import onelone.onelmod.client.features.play.CodeHider;
import onelone.onelmod.client.mode.modes.PlayMode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ParticleManager.class)
public class ParticleManagerMixin {
    @Inject(method = "addParticle(Lnet/minecraft/client/particle/Particle;)V", at = @At("HEAD"), cancellable = true)
    public void addParticle(Particle particle, CallbackInfo ci) {
        if (!(OneLClient.INSTANCE.getMode() instanceof PlayMode currMode)) return;
        var accessor = ((ParticleAccessor) particle);
        var pos = new BlockPos((int) accessor.getX(), (int) accessor.getY(), (int) accessor.getZ());
        if (!CodeHider.INSTANCE.shouldRenderPos(true, pos)) ci.cancel();
    }
}
