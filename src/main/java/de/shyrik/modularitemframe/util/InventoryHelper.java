package de.shyrik.modularitemframe.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.Direction;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

public class InventoryHelper {

    public static IItemHandler getPlayerInv(@NotNull PlayerEntity player) {
        return player.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, Direction.UP).resolve().orElse(null);
    }

    public static IItemHandler copyItemHandler(IItemHandler itemHandler) {
        ItemStackHandler copy = new ItemStackHandler(itemHandler.getSlots());

        for (int i = 0; i < itemHandler.getSlots(); i++) {
            ItemStack stack = itemHandler.getStackInSlot(i);
            if (!stack.isEmpty()) {
                stack = stack.copy();
            }
            copy.setStackInSlot(i, stack);
        }
        return copy;
    }

    public static ItemStack givePlayer(@NotNull PlayerEntity player, ItemStack stack) {
        IItemHandler handler = getPlayerInv(player);
        if (handler == null)
            return stack;

        return insertFitting(handler, stack, false);
    }

    public static ItemStack insertFitting(IItemHandler inventory, ItemStack stack, boolean simulate) {
        int slot = getFittingSlot(inventory, stack);
        if (slot < 0) return stack;
        ItemStack remain = inventory.insertItem(slot, stack, simulate);
        if (!remain.isEmpty()) insertFitting(inventory, remain, simulate);
        return ItemStack.EMPTY;
    }

    public static ItemStack extractAll(IItemHandler inventory, int slot, boolean simulate) {
        int size = inventory.getStackInSlot(slot).getCount();
        return inventory.extractItem(slot, size, simulate);
    }

    public static boolean canCraft(IItemHandler inventory, ICraftingRecipe recipe) {
        IItemHandler copy = copyItemHandler(inventory);
        for (Ingredient ingredient : recipe.getIngredients()) {
            if (ingredient.hasNoMatchingItems())
                continue;

            int slot = findSlotOfIngredient(copy, ingredient);
            if (slot >= 0)
                copy.getStackInSlot(slot).shrink(1);
            else
                return false;
        }
        return true;
    }

    public static int countPossibleCrafts(IItemHandler inventory, ICraftingRecipe recipe) {
        IItemHandler copy = copyItemHandler(inventory);
        int count = 0, slot = 0;
        while (slot >= 0) {
            for (Ingredient ingredient : recipe.getIngredients()) {
                if (ingredient.hasNoMatchingItems())
                    continue;

                slot = findSlotOfIngredient(copy, ingredient);
                if (slot < 0)
                    break;

                copy.getStackInSlot(slot).shrink(1);
            }
            count++;
        }
        return count - 1;
    }

    public static int findSlotOfIngredient(IItemHandler inventory, Ingredient ingredient) {
        for (int slot = 0; slot < inventory.getSlots(); slot++) {
            ItemStack stack = inventory.getStackInSlot(slot);
            if (!stack.isEmpty() && ingredient.test(stack)) {
                return slot;
            }
        }
        return -1;
    }

    public static void removeIngredients(IItemHandler inventory, ICraftingRecipe recipe) {
        for (Ingredient ingredient : recipe.getIngredients()) {
            for (int invSlot = 0; invSlot < inventory.getSlots(); invSlot++) {
                ItemStack stack = inventory.getStackInSlot(invSlot);
                if (!stack.isEmpty() && ingredient.test(stack)) {
                    //if (stack.getItem().isDamageable())
                    //    inventory.setStack(invSlot, stack.getItem().getContainerItem(stack));
                    //else
                    inventory.extractItem(invSlot, 1, false);
                    break;
                }
            }
        }
    }

    public static void giveAllPossibleStacks(IItemHandler target, IItemHandler source, ItemStack stack, ItemStack prioritySourceStack) {
        ItemStack remain = insertFitting(target, prioritySourceStack, false);
        prioritySourceStack.setCount(remain.getCount());
        if (!remain.isEmpty())
            return;

        for (int i = 0; i < source.getSlots(); i++) {
            ItemStack tmpSourceStack = extractAll(source, i, true);
            if (ItemHelper.simpleAreStacksEqual(stack, tmpSourceStack)) {
                remain = insertFitting(target, tmpSourceStack, false);
                source.extractItem(i, tmpSourceStack.getCount() - remain.getCount(), false);
                if (!remain.isEmpty()) break;
            }
        }
    }

    public static int getFittingSlot(IItemHandler inventory, ItemStack stack) {
        int slot = findAvailableSlotForItem(inventory, stack);
        return slot < 0 ? getFirstUnOccupiedSlot(inventory) : slot;
    }

    public static int findAvailableSlotForItem(IItemHandler inventory, ItemStack stack) {
        for (int i = 0; i < inventory.getSlots(); ++i)
            if (inventory.getStackInSlot(i).getCount() < inventory.getStackInSlot(i).getMaxStackSize() && ItemStack.areItemsEqual(inventory.getStackInSlot(i), stack))
                return i;
        return -1;
    }

    public static int getFirstUnOccupiedSlot(IItemHandler inventory) {
        for (int i = 0; i < inventory.getSlots(); ++i) if (inventory.getStackInSlot(i).isEmpty()) return i;
        return -1;
    }

    public static int getLastUnOccupiedSlot(IItemHandler inventory) {
        for (int i = inventory.getSlots() - 1; i >= 0; i--) if (inventory.getStackInSlot(i).isEmpty()) return i;
        return -1;
    }

    public static int getFirstOccupiedSlot(IItemHandler inventory) {
        for (int i = 0; i < inventory.getSlots(); ++i) if (!inventory.getStackInSlot(i).isEmpty()) return i;
        return -1;
    }
}
