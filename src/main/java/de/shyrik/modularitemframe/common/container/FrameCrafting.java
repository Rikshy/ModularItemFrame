package de.shyrik.modularitemframe.common.container;

import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nonnull;

public class FrameCrafting extends InventoryCrafting {

    private final int length;
    private final Container eventHandler;
    private final IItemHandlerModifiable parent;
    private boolean doNotCallUpdates;

    public FrameCrafting(Container eventHandler, IItemHandlerModifiable parent, int width, int height) {
        super(eventHandler, width, height);
        int k = width * height;

        assert (k == parent.getSlots());

        this.parent = parent;
        this.length = k;
        this.eventHandler = eventHandler;
        this.doNotCallUpdates = false;
    }

    @Override
    public int getSizeInventory() {
        return this.length;
    }

    @Nonnull
    @Override
    public ItemStack getStackInSlot(int index) {
        return index >= getSizeInventory() ? ItemStack.EMPTY : parent.getStackInSlot(index);
    }

    @Override
    public boolean hasCustomName() {
        return false;
    }

    @Override
    public void setInventorySlotContents(int index, @Nonnull ItemStack stack) {
        parent.setStackInSlot(index, stack);
        onCraftMatrixChanged();
    }

    @Override
    public void markDirty() {
        //this.parent.markDirty();
        onCraftMatrixChanged();
    }

    @Override
    public void clear() {
        // inventory can't clear the tile container
    }

    /**
     * If set to true no eventhandler.onCraftMatrixChanged calls will be made.
     * This is used to prevent recipe check when changing the item slots when something is crafted
     * (since each slot with an item is reduced by 1, it changes -> callback)
     */
    public void setDoNotCallUpdates(boolean doNotCallUpdates) {
        this.doNotCallUpdates = doNotCallUpdates;
    }

    public void onCraftMatrixChanged() {
        if (!doNotCallUpdates) {
            this.eventHandler.onCraftMatrixChanged(this);
        }
    }
}