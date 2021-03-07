package de.shyrik.modularitemframe.common.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import org.jetbrains.annotations.Nullable;

public abstract class BaseContainer extends Container {
    protected PlayerEntity player;

    protected BaseContainer(@Nullable ContainerType<?> type, int id, PlayerEntity player) {
        super(type, id);
        this.player = player;
    }

    public void onSlotChanged(SlotEx slot) {
        detectAndSendChanges();
    }
}
