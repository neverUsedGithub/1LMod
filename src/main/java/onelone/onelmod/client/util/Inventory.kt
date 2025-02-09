package onelone.onelmod.client.util

import net.minecraft.client.MinecraftClient
import net.minecraft.item.ItemStack
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket

object Inventory {
    private fun toPacketSlot(slot: Int): Int {
        if (slot in 0..8) return slot + 36;
        return slot;
    }

    fun giveCreativeItem(stack: ItemStack): Boolean {
        val client = MinecraftClient.getInstance()
        val inventory = client.player?.inventory
        val networkHandler = client.networkHandler!!

        if (inventory == null) return false

        var slot = inventory.getOccupiedSlotWithRoomForStack(stack)
        if (slot == -1) slot = inventory.emptySlot
        if (slot == -1) return false

        inventory.insertStack(slot, stack)
        networkHandler.sendPacket(CreativeInventoryActionC2SPacket(toPacketSlot(slot), inventory.getStack(slot)))

        return true;
    }
}