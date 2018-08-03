package de.shyrik.justcraftingframes.common.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;

public class ContainerCraftingFrame extends Container {

	private static final int FRAME_SLOTS_PER_ROW = 3;
	private static final int SLOTS_PER_ROW = 9;
	private static final int INV_ROWS = 3;
	/**
	 * The object to send callbacks to.
	 */
	private IContainerCallbacks callbacks;
	/**
	 * The player inventory.
	 */
	private IItemHandlerModifiable playerInventory;
	/**
	 * The chest inventory.
	 */
	private IItemHandlerModifiable frameInventory;

	public ContainerCraftingFrame(IItemHandlerModifiable playerInventory, IItemHandlerModifiable frameInventory, EntityPlayer player, IContainerCallbacks containerCallbacks) {
		this.playerInventory = playerInventory;
		this.frameInventory = frameInventory;

		this.callbacks = containerCallbacks;
		callbacks.onContainerOpened(player);

		//this.addSlotToContainer(new SlotCrafting(playerInventory.player, this.craftMatrix, this.craftResult, 0, 124, 35));
		for (int row = 0; row < FRAME_SLOTS_PER_ROW; ++row) {
			for (int col = 0; col < FRAME_SLOTS_PER_ROW; ++col) {
				addSlotToContainer(new SlotItemHandler(frameInventory, col + row * FRAME_SLOTS_PER_ROW, 30 + col * 18, 17 + row * 18));
			}
		}

		for (int row = 0; row < INV_ROWS; ++row) {
			for (int col = 0; col < SLOTS_PER_ROW; ++col) {
				addSlotToContainer(new SlotItemHandler(playerInventory, col + row * SLOTS_PER_ROW + SLOTS_PER_ROW, 8 + col * 18, 84 + row * 18));
			}
		}

		for (int col = 0; col < SLOTS_PER_ROW; ++col) {
			addSlotToContainer(new SlotItemHandler(playerInventory, col, 8 + col * 18, 142));
		}
	}


	@Override
	@Nonnull
	public ItemStack transferStackInSlot(final EntityPlayer player, final int index) {
		final Slot slot = this.inventorySlots.get(index);

		if (slot != null && !slot.getStack().isEmpty()) {
			final ItemStack stack = slot.getStack();
			final ItemStack originalStack = stack.copy();

			if (index < INV_ROWS * SLOTS_PER_ROW) {
				if (!this.mergeItemStack(stack, INV_ROWS * SLOTS_PER_ROW, this.inventorySlots.size(), true)) {
					return ItemStack.EMPTY;
				}
			} else if (!this.mergeItemStack(stack, 0, INV_ROWS * SLOTS_PER_ROW, false)) {
				return ItemStack.EMPTY;
			}

			if (stack.isEmpty()) {
				slot.putStack(ItemStack.EMPTY);
			} else {
				slot.onSlotChanged();
			}

			return originalStack;
		}

		return ItemStack.EMPTY;
	}

	@Override
	public boolean canInteractWith(final EntityPlayer playerIn) {
		return callbacks.isUsableByPlayer(playerIn);
	}

	@Override
	public void onContainerClosed(final EntityPlayer playerIn) {
		super.onContainerClosed(playerIn);

		callbacks.onContainerClosed(playerIn);
	}

	/*@Override
	@Nonnull
	public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, EntityPlayer player) {
		if (slotId < 9) {
			ItemStack stack = ItemStack.EMPTY;

			if (clickTypeIn == ClickType.QUICK_CRAFT || (dragType == 0) || (dragType == 1)) {
				Slot stackSlot = getSlot(slotId);
				ItemStack stackHeld = player.inventory.getCurrentItem();

				stack = stackHeld;
				if (stackHeld.isEmpty()) {
					stackSlot.putStack(ItemStack.EMPTY);
				} else {
					ItemStack s = stackHeld.copy();
					s.setCount(1);
					stackSlot.putStack(s);
				}
			}

			return stack;
		}
		return super.slotClick(slotId, dragType, clickTypeIn, player);
	}*/


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
	}
}
