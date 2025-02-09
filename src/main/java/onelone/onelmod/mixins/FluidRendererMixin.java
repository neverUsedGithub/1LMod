package onelone.onelmod.mixins;

import net.minecraft.block.BlockState;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.block.FluidRenderer;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import onelone.onelmod.client.OneLClient;
import onelone.onelmod.client.features.play.CodeHider;
import onelone.onelmod.client.mode.modes.PlayMode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FluidRenderer.class)
public class FluidRendererMixin {
    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void render(BlockRenderView world, BlockPos pos, VertexConsumer vertexConsumer, BlockState blockState, FluidState fluidState, CallbackInfo ci) {
        if (!(OneLClient.INSTANCE.getMode() instanceof PlayMode play)) return;
        if (!CodeHider.INSTANCE.shouldRenderPos(true, pos)) ci.cancel();
    }
}
