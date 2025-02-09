package onelone.onelmod.mixins;

import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.slot.SlotActionType;
import onelone.onelmod.client.OneLClient;
import onelone.onelmod.client.config.Config;
import onelone.onelmod.client.mode.modes.DevMode;
import onelone.onelmod.client.util.Inventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerInteractionManager.class)
abstract public class ScreenHandlerMixin {
    @Inject(method = "clickSlot", at = @At("HEAD"), cancellable = true)
    private void clickSlot(int syncId, int slotId, int button, SlotActionType actionType, PlayerEntity player, CallbackInfo ci) {
        var screenHandler = player.currentScreenHandler;

        if (syncId != screenHandler.syncId ||
            actionType != SlotActionType.CLONE ||
            !(OneLClient.INSTANCE.getMode() instanceof DevMode) ||
            !Config.INSTANCE.getMiddleClickAction()) return;

        var clickSlot = screenHandler.slots.get(slotId);
        if (!clickSlot.hasStack()) return;

        var clickStack = clickSlot.getStack();
        Inventory.INSTANCE.giveCreativeItem(clickStack.copyWithCount(1));
        ci.cancel();
    }
}
