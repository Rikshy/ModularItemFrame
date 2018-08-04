package de.shyrik.justcraftingframes.common.container;


import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.Slot;

public interface IContainerCallbacks {
	/**
	 * Called when the {@link Container} is opened by a player.
	 *
	 * @param player The player
	 */
	void onContainerOpened(EntityPlayer player);

	/**
	 * Called when the {@link Container} is closed by a player.
	 *
	 * @param player The player
	 */
	void onContainerClosed(EntityPlayer player);

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