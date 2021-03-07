package modularitemframe.api.inventory;


import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;

public class ItemStackHandlerWrapper extends ItemHandlerWrapper implements INBTSerializable<CompoundNBT> {

    private final OpenItemStackHandler inventory;

    public ItemStackHandlerWrapper(OpenItemStackHandler handler) {
        this.handler = inventory = handler;
    }

    public int getSlots() {
        return inventory.getSlots();
    }

    public void setSize(int newCapacity) {
        inventory.setSize(newCapacity);
    }

    public ItemStack getStackInSlot(int slot) {
        return inventory.getStackInSlot(slot);
    }

    public void setStackInSlot(int slot, ItemStack stack) {
        inventory.setStackInSlot(slot, stack);
    }

    public ItemStack extractStackInSlot(int slot) {
        ItemStack stack = inventory.getStackInSlot(slot);
        setStackInSlot(slot, ItemStack.EMPTY);
        return stack;
    }

    @Override
    public CompoundNBT serializeNBT() {
        return inventory.serializeNBT();
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        inventory.deserializeNBT(nbt);
    }
}
