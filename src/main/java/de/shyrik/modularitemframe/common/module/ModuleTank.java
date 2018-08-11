package de.shyrik.modularitemframe.common.module;

import de.shyrik.modularitemframe.ConfigValues;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidUtil;

import javax.annotation.Nonnull;

public class ModuleTank extends ModuleFluid {

    public ModuleTank() {
        tank.setCapacity(ConfigValues.TankFrameCapacity);
    }

    @Override
    public void onBlockActivated(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull EntityPlayer playerIn, @Nonnull EnumHand hand, @Nonnull EnumFacing facing, float hitX, float hitY, float hitZ) {
        //ItemStack stack = playerIn.getHeldItem(hand);
        FluidUtil.interactWithFluidHandler(playerIn, hand, tank);
        tile.markDirty();
        //return FluidUtil.getFluidHandler(stack) != null; TODO
    }
}
