package de.shyrik.modularitemframe.common.container;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.NotNull;

public class FrameCrafting extends CraftingInventory {

    private final int length;
    private final Container eventHandler;
    private final IItemHandlerModifiable parent;

    public FrameCrafting(Container eventHandler, IItemHandlerModifiable parent, int width, int height) {
        super(eventHandler, width, height);
        int k = width * height;

        assert (k == parent.getSlots());

        this.parent = parent;
        this.length = k;
        this.eventHandler = eventHandler;
    }

    @Override
    public int getSizeInventory() {
        return this.length;
    }

    @NotNull
    @Override
    public ItemStack getStackInSlot(int index) {
        return index >= getSizeInventory() ? ItemStack.EMPTY : parent.getStackInSlot(index);
    }

    @Override
    public void setInventorySlotContents(int index, @NotNull ItemStack stack) {
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

    public void onCraftMatrixChanged() {
        this.eventHandler.onCraftMatrixChanged(this);
    }
}