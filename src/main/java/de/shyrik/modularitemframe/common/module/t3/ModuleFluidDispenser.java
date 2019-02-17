package de.shyrik.modularitemframe.common.module.t3;

import de.shyrik.modularitemframe.ModularItemFrame;
import de.shyrik.modularitemframe.api.ModuleBase;
import de.shyrik.modularitemframe.common.block.BlockModularFrame;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nonnull;

public class ModuleFluidDispenser extends ModuleBase {
    public static final ResourceLocation LOC = new ResourceLocation(ModularItemFrame.MOD_ID, "module_t3_fluid_dispenser");
    public static final ResourceLocation BG_LOC = new ResourceLocation(ModularItemFrame.MOD_ID, "blocks/module_t3_fluid_dispenser");

    @Nonnull
    @Override
    @OnlyIn(Dist.CLIENT)
    public ResourceLocation frontTexture() {
        return BG_LOC;
    }

    @Nonnull
    @Override
    @OnlyIn(Dist.CLIENT)
    public ResourceLocation innerTexture() {
        return BlockModularFrame.INNER_HARDEST_LOC;
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
        if (world.getGameTime() % (60 - 10 * tile.getSpeedUpCount()) == 0) return;
        EnumFacing facing = tile.blockFacing();
        if (!world.isAirBlock(pos.offset(facing.getOpposite()))) return;

        TileEntity neighbor = tile.getAttachedTile();
        if (neighbor == null) return;

        IFluidHandler handler = neighbor.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, facing.getOpposite()).orElse(null);
        if (handler == null) return;

        if (FluidUtil.tryPlaceFluid(null, world, pos.offset(facing.getOpposite()), handler, handler.drain(Fluid.BUCKET_VOLUME, false)))
            handler.drain(Fluid.BUCKET_VOLUME, true);
    }
}
