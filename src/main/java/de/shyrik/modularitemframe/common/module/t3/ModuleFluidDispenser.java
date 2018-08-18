package de.shyrik.modularitemframe.common.module.t3;

import de.shyrik.modularitemframe.api.ModuleBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nonnull;

public class ModuleFluidDispenser extends ModuleBase {
    @Nonnull
    @Override
    public ResourceLocation frontTexture() {
        return null;
    }

    @Override
    public String getModuleName() {
        return I18n.format("modularitemframe.module.fluid_dispenser");
    }

    @Override
    public boolean onBlockActivated(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull EntityPlayer playerIn, @Nonnull EnumHand hand, @Nonnull EnumFacing facing, float hitX, float hitY, float hitZ) {
        return false;
    }

    @Override
    public void tick(@Nonnull World world, @Nonnull BlockPos pos) {
        if (world.getTotalWorldTime() % 20 == 0) return;
        EnumFacing facing = tile.blockFacing();
        if(!world.isAirBlock(pos.offset(facing.getOpposite()))) return;

        TileEntity neighbor = tile.getNeighbor(facing);
        if(neighbor != null) {
            IFluidHandler handler = neighbor.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, facing.getOpposite());
            if(handler!=null) {
                FluidUtil.tryPlaceFluid(null, world, pos.offset(facing.getOpposite()), handler, handler.drain(1000, false));
            }
        }
    }
}
