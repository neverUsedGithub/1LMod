package onelone.onelmod.mixins;

import net.fabricmc.fabric.impl.client.indigo.renderer.render.TerrainRenderContext;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import onelone.onelmod.client.OneLClient;
import onelone.onelmod.client.features.play.CodeHider;
import onelone.onelmod.client.mode.modes.PlayMode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TerrainRenderContext.class)
public class TerrainRendererContextMixin {
    @Inject(method = "tessellateBlock", at = @At("HEAD"), cancellable = true)
    public void tessellateBlock(BlockState blockState, BlockPos blockPos, BakedModel model, MatrixStack matrixStack, CallbackInfo ci) {
        if (!CodeHider.INSTANCE.shouldRenderPos(true, blockPos)) ci.cancel();
    }
}
