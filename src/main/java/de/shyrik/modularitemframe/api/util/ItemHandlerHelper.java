package de.shyrik.modularitemframe.api.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;

public class ItemHandlerHelper {
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

    public static boolean canCraft(IItemHandler inventory, NonNullList<Ingredient> ingredients) {
        IItemHandler copy = copyItemHandler(inventory);
        for (Ingredient ingredient : ingredients) {
            if (ingredient.getMatchingStacks().length > 0) {
                int slot = findSlotOfIngredient(copy, ingredient);
                if (slot >= 0)
                    copy.extractItem(slot, 1, false);
                else
                    return false;
            }
        }
        return true;
    }

    public static int countPossibleCrafts(IItemHandler inventory, IRecipe recipe) {
        IItemHandler copy = copyItemHandler(inventory);
        int count = 0, slot = 0;
        while (slot >= 0) {
            for (Ingredient ingredient : ItemHelper.getIngredients(recipe)) {
                if (ingredient.getMatchingStacks().length > 0) {
                    slot = findSlotOfIngredient(copy, ingredient);
                    if (slot >= 0)
                        copy.extractItem(slot, 1, false);
                }
            }
            count++;
        }
        return count - 1;
    }

    public static int findSlotOfIngredient(IItemHandler inventory, Ingredient ingredient) {
        for (int slot = 0; slot < inventory.getSlots(); slot++) {
            ItemStack stack = inventory.extractItem(slot, 1, true);
            if (!stack.isEmpty() && ItemHelper.areItemsEqualIgnoreDurability(ingredient.getMatchingStacks(), stack)) {
                return slot;
            }
        }
        return -1;
    }

    public static void removeFromInventory(IItemHandlerModifiable inventory, ItemStack[] toRemove) {
        for (int invSlot = 0; invSlot < inventory.getSlots(); invSlot++) {
            ItemStack stack = inventory.extractItem(invSlot, 1, true);
            if (!stack.isEmpty() && ItemHelper.areItemsEqualIgnoreDurability(toRemove, stack)) {
                if (stack.getItem().hasContainerItem(stack))
                    inventory.setStackInSlot(invSlot, stack.getItem().getContainerItem(stack));
                else
                    inventory.extractItem(invSlot, 1, false);
                break;
            }
        }
    }

    public static ItemStack giveStack(IItemHandlerModifiable inventory, ItemStack stack) {
        int slot = getFittingSlot(inventory, stack);
        if (slot < 0) return stack;
        ItemStack remain = inventory.insertItem(slot, stack, false);
        if (!remain.isEmpty()) return giveStack(inventory, remain);
        return ItemStack.EMPTY;
    }

    public static void giveAllPossibleStacks(IItemHandlerModifiable target, IItemHandlerModifiable source, ItemStack stack, ItemStack prioSourceStack) {
        ItemStack remain = giveStack(target, prioSourceStack.copy());
        prioSourceStack.setCount(remain.getCount());
        if (!remain.isEmpty())
            return;

        for (int i = 0; i < source.getSlots(); i++) {
            ItemStack sourceStack = source.getStackInSlot(i);
            if (ItemHelper.simpleAreStacksEqual(stack, sourceStack)) {
                remain = giveStack(target, sourceStack.copy());
                sourceStack.setCount(remain.getCount());
                if (!remain.isEmpty()) break;
            }
        }
    }

    public static IItemHandlerModifiable getPlayerInv(@Nonnull PlayerEntity player) {
        LazyOptional<IItemHandler> lz = player.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, Direction.UP);
        return lz.isPresent() ? (IItemHandlerModifiable)lz.orElse(null) : null;
    }

    public static IItemHandlerModifiable getItemHandler(@Nonnull ICapabilityProvider provider, Direction face) {
        LazyOptional<IItemHandler> lz = provider.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, face);
        return lz.isPresent() ? (IItemHandlerModifiable)lz.orElse(null) : null;
    }
}
