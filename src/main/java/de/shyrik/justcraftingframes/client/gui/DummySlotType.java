package de.shyrik.justcraftingframes.client.gui;

import com.teamwizardry.librarianlib.features.container.ITransferRule;
import com.teamwizardry.librarianlib.features.container.SlotType;
import com.teamwizardry.librarianlib.features.container.internal.SlotBase;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

public class DummySlotType extends SlotType {

    @Override
    public int stackLimit(@Nonnull SlotBase slot,@Nonnull ItemStack stack) {
        return 1;
    }

    @Override
    @NotNull
    public ITransferRule.AutoTransferResult autoTransferInto(@Nonnull SlotBase slot,@Nonnull ItemStack stack) {
        return new ITransferRule.AutoTransferResult(stack, false, false);

/*
        if (!slot.isItemValid(stack))
            return new ITransferRule.AutoTransferResult(stack, false, false);
        ItemStack slotStack = slot.getStack();

        if (slotStack.isEmpty()) {
            ItemStack leftOver = stack.copy();
            int quantity = Math.min(slot.getSlotStackLimit(), leftOver.getCount());

            ItemStack insert = leftOver.copy();
            insert.setCount(quantity);
            slot.putStack(insert);

            leftOver.setCount(leftOver.getCount() - quantity);
            return new ITransferRule.AutoTransferResult(leftOver.getCount() <= 0 ? ItemStack.EMPTY : leftOver, true, true);
        }
        if (ITransferRule.Companion.areItemStacksEqual(stack, slotStack)) {
            int combinedSize = stack.getCount() + slotStack.getCount();
            int maxStackSize = Math.min(slot.getItemStackLimit(stack), stack.getMaxStackSize());

            if (combinedSize <= maxStackSize) {
                ItemStack newStack = slotStack.copy();
                newStack.setCount(combinedSize);
                slot.putStack(newStack);

                return new ITransferRule.AutoTransferResult(ItemStack.EMPTY, true, true);
            } else {
                ItemStack leftoverStack = stack.copy();
                leftoverStack.setCount( leftoverStack.getCount() - maxStackSize - slotStack.getCount());

                ItemStack newStack = slotStack.copy();
                newStack.setCount(maxStackSize);
                slot.putStack(newStack);

                return new ITransferRule.AutoTransferResult(leftoverStack, true, true);
            }
        }

        return new ITransferRule.AutoTransferResult(stack, false, false);*/
    }
}
