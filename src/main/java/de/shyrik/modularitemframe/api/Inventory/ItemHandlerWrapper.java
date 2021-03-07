package de.shyrik.modularitemframe.api.Inventory;

import de.shyrik.modularitemframe.api.Inventory.filter.DefaultFilter;
import de.shyrik.modularitemframe.api.Inventory.filter.IItemFilter;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class ItemHandlerWrapper {

    protected TileEntity tile;
    protected IItemHandler handler;
    public ItemHandlerWrapper(IItemHandler handler, TileEntity tile) {
        this.handler = handler;
        this.tile = tile;
    }

    ItemHandlerWrapper() {

    }

    public void markDirty() {
        tile.markDirty();
    }

    public IItemHandler getHandler() {
        return handler;
    }

    public ItemStack extract(int amount, boolean simulate) {
        return extract(DefaultFilter.ANYTHING, amount, simulate);
    }

    public ItemStack extract(boolean simulate) {
        return extract(DefaultFilter.ANYTHING, simulate);
    }

    public ItemStack extract(IItemFilter filter, int amount, boolean simulate) {
        int remaining = amount;
        ItemStack ret = ItemStack.EMPTY;
        for (int slot = 0; slot < handler.getSlots() && remaining > 0; slot++) {
            if (filter.test(handler.getStackInSlot(slot))) {
                ItemStack extracted = handler.extractItem(slot, remaining, simulate);
                if (ret.isEmpty())
                    ret = extracted.copy();
                else
                    ret.grow(extracted.getCount());
                remaining -= extracted.getCount();
            }
        }

        return ret;
    }

    public ItemStack extract(IItemFilter filter, boolean simulate) {
        for (int slot = 0; slot < handler.getSlots(); slot++) {
            if (filter.test(handler.getStackInSlot(slot))) {
                ItemStack test = handler.extractItem(slot, 1, true);
                if (test.isEmpty())
                    continue;

                return handler.extractItem(slot, test.getMaxStackSize(), simulate);
            }
        }

        return ItemStack.EMPTY;
    }

    public ItemStack insert(IItemFilter filter, ItemStack stack, boolean simulate) {
        if (filter.test(stack))
            return insert(stack, simulate);
        return stack;
    }

    public ItemStack insert(ItemStack stack, boolean simulate) {
        int slot = getFittingSlot(stack);
        if (slot < 0) return stack;
        ItemStack remain = handler.insertItem(slot, stack, simulate);
        if (!remain.isEmpty()) insert(remain, simulate);
        return ItemStack.EMPTY;
    }

    public int getFittingSlot(ItemStack stack) {
        int slot = findAvailableSlotForItem(stack);
        return slot < 0 ? getFirstUnOccupiedSlot() : slot;
    }

    public int findAvailableSlotForItem(ItemStack stack) {
        for (int i = 0; i < handler.getSlots(); ++i)
            if (handler.getStackInSlot(i).getCount() < handler.getStackInSlot(i).getMaxStackSize() && ItemStack.areItemsEqual(handler.getStackInSlot(i), stack))
                return i;
        return -1;
    }

    public int getFirstOccupiedSlot(int offset) {
        for (int i = offset; i < handler.getSlots(); ++i) if (!handler.getStackInSlot(i).isEmpty()) return i;
        return -1;
    }

    public int getFirstUnOccupiedSlot() {
        for (int i = 0; i < handler.getSlots(); ++i) if (handler.getStackInSlot(i).isEmpty()) return i;
        return -1;
    }

    public ItemHandlerWrapper copy() {
        ItemStackHandler copy = new ItemStackHandler(handler.getSlots());

        for (int i = 0; i < handler.getSlots(); i++) {
            ItemStack stack = handler.getStackInSlot(i);
            if (!stack.isEmpty()) {
                stack = stack.copy();
            }
            copy.setStackInSlot(i, stack);
        }

        return new ItemHandlerWrapper(copy, tile);
    }
}
