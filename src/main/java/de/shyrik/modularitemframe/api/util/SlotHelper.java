package de.shyrik.modularitemframe.api.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;

import java.util.List;

public class SlotHelper {

    public static ItemStack ghostSlotClick(Slot slot, int mouseButton, ClickType clickTypeIn, PlayerEntity player) {
        ItemStack stack = ItemStack.EMPTY;
        ItemStack stackSlot = slot.getStack();
        ItemStack stackHeld = player.inventory.getItemStack();

        if (mouseButton == 0 || mouseButton == 1) {
            if (stackSlot.isEmpty()) {
                if (!stackHeld.isEmpty()) {
                    fillGhostSlot(slot, stackHeld, mouseButton);
                }
            } else if (stackHeld.isEmpty()) {
                adjustGhostSlot(slot, mouseButton, clickTypeIn);
            } else if (slot.isItemValid(stackHeld)) {
                if (ItemHelper.simpleAreItemsEqual(stackSlot, stackHeld)) {
                    adjustGhostSlot(slot, mouseButton, clickTypeIn);
                } else {
                    fillGhostSlot(slot, stackHeld, mouseButton);
                }
            }
        } else if (mouseButton == 5) {
            if (!slot.getHasStack()) {
                fillGhostSlot(slot, stackHeld, mouseButton);
            }
        }
        return stack;
    }

    private static void adjustGhostSlot(Slot slot, int mouseButton, ClickType clickTypeIn) {
        ItemStack stackSlot = slot.getStack();
        int stackSize;
        if (clickTypeIn == ClickType.QUICK_MOVE) {
            stackSize = mouseButton == 0 ? (stackSlot.getCount() + 1) / 2 : stackSlot.getCount() * 2;
        } else {
            stackSize = mouseButton == 0 ? stackSlot.getCount() - 1 : stackSlot.getCount() + 1;
        }

        if (stackSize > slot.getSlotStackLimit()) {
            stackSize = slot.getSlotStackLimit();
        }

        stackSlot.setCount(stackSize);

        slot.putStack(stackSlot);
    }

    private static void fillGhostSlot(Slot slot, ItemStack stackHeld, int mouseButton) {
        if (stackHeld.isEmpty()) {
            slot.putStack(ItemStack.EMPTY);
            return;
        }

        int stackSize = mouseButton == 0 ? stackHeld.getCount() : 1;
        if (stackSize > slot.getSlotStackLimit()) {
            stackSize = slot.getSlotStackLimit();
        }
        ItemStack phantomStack = stackHeld.copy();
        phantomStack.setCount(stackSize);

        slot.putStack(phantomStack);
    }

    public static ItemStack transferStackInSlot(List<Slot> inventorySlots, PlayerEntity player, int slotIndex) {
        Slot slot = inventorySlots.get(slotIndex);
        if (slot == null || !slot.getHasStack()) {
            return ItemStack.EMPTY;
        }

        int numSlots = inventorySlots.size();
        ItemStack stackInSlot = slot.getStack();
        ItemStack originalStack = stackInSlot.copy();

        //if (!shiftItemStack(inventorySlots, stackInSlot, slotIndex, numSlots, fromCraftingSlot)) {
        //    return ItemStack.EMPTY;
        //}

        slot.onSlotChange(stackInSlot, originalStack);
        if (stackInSlot.isEmpty()) {
            slot.putStack(ItemStack.EMPTY);
        } else {
            slot.onSlotChanged();
        }

        if (stackInSlot.getCount() == originalStack.getCount()) {
            return ItemStack.EMPTY;
        }

        slot.onTake(player, stackInSlot);
        return originalStack;
    }
}
