package de.shyrik.modularitemframe.api.Inventory.filter;

import net.minecraft.item.ItemStack;

public class NegatedItemFilter implements IItemFilter{

    public final IItemFilter original;

    public NegatedItemFilter(IItemFilter original) {
        this.original = original;
    }

    @Override
    public boolean test(ItemStack stack) {
        return !original.test(stack);
    }

    @Override
    public IItemFilter negate() {
        return original;
    }
}
