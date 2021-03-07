package de.shyrik.modularitemframe.common.container;

import de.shyrik.modularitemframe.util.InventoryHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class GhostInventoryContainer extends BaseContainer {

    protected static final int SLOTS_PER_ROW = 9;
    private static final int INV_ROWS = 3;

    private static final int PLAYER_SLOTS = 36;

    protected GhostInventoryContainer(@Nullable ContainerType<?> type, int id, PlayerEntity player) {
        super(type, id, player);
    }

    protected void addPlayerInventory(PlayerEntity player) {
        IItemHandler playerInv = InventoryHelper.getPlayerInv(player);

        for (int row = 0; row < INV_ROWS; ++row) {
            for (int col = 0; col < SLOTS_PER_ROW; ++col) {
                addSlot(new SlotItemHandler(playerInv, col + row * SLOTS_PER_ROW + SLOTS_PER_ROW, 8 + col * 18, 84 + row * 18));
            }
        }

        for (int col = 0; col < SLOTS_PER_ROW; ++col) {
            addSlot(new SlotItemHandler(playerInv, col, 8 + col * 18, 142));
        }
    }

    @NotNull
    @Override
    public ItemStack slotClick(int slotId, int dragType, @NotNull ClickType clickTypeIn, @NotNull PlayerEntity player) {
        if (slotId >= 0 && getSlot(slotId) instanceof GhostSlot) {

            Slot stackSlot = getSlot(slotId);
            ItemStack stackHeld = player.inventory.getItemStack();

            if (stackHeld.isEmpty()) {
                stackSlot.putStack(ItemStack.EMPTY);
            } else {
                ItemStack s = stackHeld.copy();
                s.setCount(1);
                stackSlot.putStack(s);
            }
            stackSlot.onSlotChanged();
            detectAndSendChanges();

            return stackHeld;
        }
        return super.slotClick(slotId, dragType, clickTypeIn, player);
    }

    @Override
    @NotNull
    public ItemStack transferStackInSlot(final @NotNull PlayerEntity player, final int index) {
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
}
