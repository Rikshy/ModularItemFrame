package de.shyrik.modularitemframe.common.container.crafting;

import net.minecraft.item.crafting.ICraftingRecipe;

public interface IContainerCallbacks {
    /**
     * Called when a {@link net.minecraft.inventory.container} is changed.
     *
     * @param recipe The crafting matrix
     */
    void setRecipe(ICraftingRecipe recipe);
}