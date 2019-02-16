package de.shyrik.modularitemframe.api;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public abstract class UpgradeBase {

    public abstract int getMaxCount();

    public void onInsert(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull EnumFacing facing) {
    }

    public void onRemove(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull EnumFacing facing) {
    }
}
