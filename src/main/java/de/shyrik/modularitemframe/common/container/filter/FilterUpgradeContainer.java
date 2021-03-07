package de.shyrik.modularitemframe.common.container.filter;

import de.shyrik.modularitemframe.common.container.GhostInventoryContainer;
import de.shyrik.modularitemframe.common.container.GhostSlot;
import de.shyrik.modularitemframe.common.container.SlotEx;
import de.shyrik.modularitemframe.common.item.FilterUpgradeItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

public class FilterUpgradeContainer extends GhostInventoryContainer {

    private static final int SLOTS_PER_ROW = 9;

    private final ItemStackHandler inv;
    private final ItemStack filterStack;

    public FilterUpgradeContainer(int id, PlayerEntity player, ItemStack filter) {
        super(ContainerType.GENERIC_9X1, id, player);

        filterStack = filter;
        inv = FilterUpgradeItem.readInvTag(filter.getOrCreateTag());

        for (int col = 0; col < SLOTS_PER_ROW; ++col) {
            addSlot(new GhostSlot(this, inv, col, 8 + col * 18, 18));
        }

        addPlayerInventory(player);
    }

    @Override
    public boolean canInteractWith(@NotNull PlayerEntity playerIn) {
        return true;
    }

    @Override
    public void onSlotChanged(SlotEx slot) {
        if (slot instanceof GhostSlot) {
            FilterUpgradeItem.writeInvTag((filterStack.getOrCreateTag()), inv);
        }
    }
}
