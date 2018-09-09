package de.shyrik.modularitemframe.api;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class UpgradeBase {

    public abstract int getMaxCount();

    public void onInsert(World world, BlockPos pos, EnumFacing facing) {

    }

    public void onRemove() {

    }
}
