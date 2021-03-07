package de.shyrik.modularitemframe.common.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

public class GhostSlot extends SlotEx {

    public GhostSlot(BaseContainer parent, IItemHandler itemHandler, int index, int xPosition, int yPosition) {
        super(parent, itemHandler, index, xPosition, yPosition);
    }

    @Override
    public boolean canTakeStack(PlayerEntity playerIn) {
        return false;
    }

    @Override
    public boolean isItemValid(@NotNull ItemStack stack) {
        return true;
    }

    @Override
    public int getSlotStackLimit() {
        return 1;
    }
}
