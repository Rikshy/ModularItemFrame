package de.shyrik.modularitemframe.api.Inventory.filter;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemClassFilter implements IItemFilter {

    private final Class<? extends Item> clsFilter;

    public ItemClassFilter(Class<? extends Item> cls) {
        clsFilter = cls;
    }

    @Override
    public boolean test(ItemStack stack) {
        return clsFilter.isInstance(stack.getItem());
    }
}
