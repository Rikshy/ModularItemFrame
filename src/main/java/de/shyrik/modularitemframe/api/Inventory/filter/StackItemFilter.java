package de.shyrik.modularitemframe.api.Inventory.filter;

import de.shyrik.modularitemframe.util.ItemHelper;
import net.minecraft.item.ItemStack;

public class StackItemFilter implements IItemFilter {
    private final ItemStack stack;

    public StackItemFilter(ItemStack stack) {
        this.stack = stack;
    }

    @Override
    public boolean test(ItemStack stack) {
        return ItemHelper.areStacksEqualIgnoreAmount(this.stack, stack);
    }
}
