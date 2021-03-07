package modularitemframe.api.inventory.filter;

import net.minecraft.item.ItemStack;

public class StackItemFilter implements IItemFilter {
    private final ItemStack stack;

    public StackItemFilter(ItemStack stack) {
        this.stack = stack;
    }

    @Override
    public boolean test(ItemStack stack) {
        return ItemStack.areItemsEqualIgnoreDurability(this.stack, stack) && ItemStack.areItemStackTagsEqual(this.stack, stack);
    }
}
