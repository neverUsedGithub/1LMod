package onelone.onelmod.mixins;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.ScreenHandlerType;
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
        var screen = MinecraftClient.getInstance().currentScreen;

        if (syncId != screenHandler.syncId ||
            actionType != SlotActionType.CLONE ||
            screenHandler.getType() != ScreenHandlerType.GENERIC_9X3 ||
            !(OneLClient.INSTANCE.getMode() instanceof DevMode) ||
            !Config.INSTANCE.getMiddleClickAction()) return;

        if (screen instanceof HandledScreen<?> handledScreen) {
            var title = handledScreen.getTitle();

            if (!title.getString().equals("Chest")) {
                return;
            }
        }

        var clickSlot = screenHandler.slots.get(slotId);
        if (!clickSlot.hasStack()) return;

        var clickStack = clickSlot.getStack();
        Inventory.INSTANCE.giveCreativeItem(clickStack.copyWithCount(1));
        ci.cancel();
    }
}
