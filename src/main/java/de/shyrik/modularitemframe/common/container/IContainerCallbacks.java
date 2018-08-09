package de.shyrik.modularitemframe.common.container;


import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryCraftResult;

public interface IContainerCallbacks {

	/**
	 * Is this usable by the specified player?
	 *
	 * @param player The player
	 * @return Is this usable by the specified player?
	 */
	boolean isUsableByPlayer(EntityPlayer player);

	/**
	 * Called when a {@link Container} is changed.
	 *
	 * @param result The crafting result
	 */
	void onContainerCraftingResultChanged(InventoryCraftResult result);
}