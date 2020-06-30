package de.shyrik.modularitemframe.common.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftResultInventory;

public interface IContainerCallbacks {

    /**
     * Is this usable by the specified player?
     *
     * @param player The player
     * @return Is this usable by the specified player?
     */
    boolean isUsableByPlayer(PlayerEntity player);

    /**
     * Called when a {@link Container} is changed.
     *
     * @param result The crafting result
     */
    void onContainerCraftingResultChanged(CraftResultInventory result);
}
