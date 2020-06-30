package de.shyrik.modularitemframe.api;

import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public abstract class UpgradeBase {
    ItemUpgrade parent;

    public ItemUpgrade getParent() {
        return parent;
    }

    public abstract int getMaxCount();
    public abstract ResourceLocation getId();

    public void onInsert(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull Direction facing) {
    }

    public void onRemove(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull Direction facing) {
    }
}
