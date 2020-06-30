package de.shyrik.modularitemframe.common.module.t3;

import de.shyrik.modularitemframe.ModularItemFrame;
import de.shyrik.modularitemframe.api.ModuleBase;
import de.shyrik.modularitemframe.common.block.BlockModularFrame;
import net.minecraft.block.BlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nonnull;

public class ModuleFluidDispenser extends ModuleBase {
    public static final ResourceLocation LOC = new ResourceLocation(ModularItemFrame.MOD_ID, "module_t3_fluid_dispenser");
    public static final ResourceLocation BG_LOC = new ResourceLocation(ModularItemFrame.MOD_ID, "block/module_t3_fluid_dispenser");

    private static final int BUCKET_VOLUME = 1000;

    @Override
    public ResourceLocation getId() {
        return LOC;
    }

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
    public ActionResultType onBlockActivated(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nonnull PlayerEntity playerIn, @Nonnull Hand hand, @Nonnull Direction facing, BlockRayTraceResult hit) {
        return ActionResultType.FAIL;
    }

    @Override
    public void tick(@Nonnull World world, @Nonnull BlockPos pos) {
        if (world.getGameTime() % (60 - 10 * tile.getSpeedUpCount()) == 0) return;
        Direction facing = tile.blockFacing();
        if (!world.isAirBlock(pos.offset(facing))) return;

        TileEntity neighbor = tile.getAttachedTile();
        if (neighbor == null) return;

        IFluidHandler handler = neighbor.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, facing.getOpposite()).orElse(null);
        if (handler == null) return;

        if (FluidUtil.tryPlaceFluid(null, world, Hand.MAIN_HAND, pos.offset(facing.getOpposite()), handler, handler.drain(BUCKET_VOLUME, IFluidHandler.FluidAction.SIMULATE)))
            handler.drain(BUCKET_VOLUME, IFluidHandler.FluidAction.SIMULATE);
    }
}
