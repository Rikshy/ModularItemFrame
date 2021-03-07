package de.shyrik.modularitemframe.api.Inventory.filter;

import net.minecraft.item.ItemStack;

public enum DefaultFilter implements IItemFilter{
    ANYTHING(true),
    NOTHING(false);


    private boolean anything;

    DefaultFilter(boolean anything) {
        this.anything = anything;
    }

    @Override
    public boolean test(ItemStack stack) {
        return stack.isEmpty() ? false : anything;
    }
}
