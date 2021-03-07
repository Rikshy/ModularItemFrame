package de.shyrik.modularitemframe.common.module.t3;

import de.shyrik.modularitemframe.ModularItemFrame;
import de.shyrik.modularitemframe.common.block.ModularFrameBlock;
import modularitemframe.api.ModuleBase;
import modularitemframe.api.ModuleTier;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;

public class FluidDispenserModule extends ModuleBase {
    public static final ResourceLocation ID = new ResourceLocation(ModularItemFrame.MOD_ID, "module_t3_fluid_dispenser");
    public static final ResourceLocation BG = new ResourceLocation(ModularItemFrame.MOD_ID, "module/module_t3_fluid_dispenser");
    private static final TextComponent NAME = new TranslationTextComponent("modularitemframe.module.fluid_dispenser");

    @NotNull
    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @NotNull
    @Override
    public TextComponent getName() {
        return NAME;
    }

    @NotNull
    @Override
    public ModuleTier moduleTier() {
        return ModuleTier.T3;
    }

    @NotNull
    @Override
    public ResourceLocation frontTexture() {
        return BG;
    }

    @Override
    public void tick(@NotNull World world, @NotNull BlockPos pos) {
        if (world.isRemote || frame.isPowered() || !canTick(world,60, 10)) return;
        Direction facing = frame.getFacing();
        if (!world.isAirBlock(pos.offset(facing))) return;

        IFluidHandler neighbor = frame.getAttachedTank();
        if (neighbor == null) return;

        FluidStack attempt = neighbor.drain(FluidAttributes.BUCKET_VOLUME, IFluidHandler.FluidAction.SIMULATE);
        if (attempt.getAmount() != FluidAttributes.BUCKET_VOLUME)
            return;

        BlockPos target = pos.offset(frame.getFacing());
        BlockState state = world.getBlockState(target);
        Block block = state.getBlock();
        Fluid fluid = attempt.getRawFluid();

        if (fluid == null) return;

        if (state.isReplaceable(fluid)) {
            world.setBlockState(target, fluid.getDefaultState().getBlockState());
        } else if (block instanceof IWaterLoggable) {
            ((IWaterLoggable) block).receiveFluid(world, pos, state, fluid.getDefaultState());
        }

        neighbor.drain(FluidAttributes.BUCKET_VOLUME, IFluidHandler.FluidAction.EXECUTE);
    }
}
