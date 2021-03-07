package de.shyrik.modularitemframe.common.module.t1;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.shyrik.modularitemframe.ModularItemFrame;
import de.shyrik.modularitemframe.api.ModuleBase;
import de.shyrik.modularitemframe.client.FrameRenderer;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.TextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TankModule extends ModuleBase {

    public static final ResourceLocation ID = new ResourceLocation(ModularItemFrame.MOD_ID, "module_t1_tank");
    public static final ResourceLocation BG = new ResourceLocation(ModularItemFrame.MOD_ID, "module/module_t1_tank");
    private static final TextComponent NAME = new TranslationTextComponent("modularitemframe.module.tank");

    private static final String NBT_MODE = "tank_mode";
    private static final String NBT_TANK = "tank";

    public EnumMode mode = EnumMode.NONE;
    private final FluidTank tank = new FluidTank(ModularItemFrame.config.tankFrameCapacity.get() * FluidAttributes.BUCKET_VOLUME);

    @NotNull
    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @NotNull
    @Override
    public ResourceLocation frontTexture() {
        return BG;
    }

    @NotNull
    @Override
    public ResourceLocation backTexture() {
        return BG;
    }

    @NotNull
    @Override
    public TextComponent getName() {
        return NAME;
    }

    @Override
    public void specialRendering(@NotNull FrameRenderer renderer, float partialTicks, @NotNull MatrixStack matrixStack, @NotNull IRenderTypeBuffer buffer, int light, int overlay) {
        if (tank.getFluidAmount() > 0) {
            double amount = (float) tank.getFluidAmount() / (float) tank.getCapacity();

            //only south cuz the matrix is already in that  direction
            FrameRenderer.FluidRenderFace face = FrameRenderer.FluidRenderFace
                    .create(0.1875d, 0.1875d, 0.08d, 0.8125d, 0.1875d + amount * 0.625d, 0.08d);

            renderer.renderFluid(tank.getFluid(), face, matrixStack, buffer);
        }
    }

    @Override
    public void screw(@NotNull World world, @NotNull BlockPos pos, @NotNull PlayerEntity player, ItemStack driver) {
        if (!world.isRemote) {
            if (ModularItemFrame.config.tankTransferRate.get() > 0) {
                int modeIdx = mode.getIndex() + 1;
                if (modeIdx == EnumMode.values().length) modeIdx = 0;
                mode = EnumMode.values()[modeIdx];
                player.sendMessage(new TranslationTextComponent("modularitemframe.message.mode_change", mode.getName()), Util.DUMMY_UUID);
            }
        }
    }

    @Override
    public ActionResultType onUse(@NotNull World worldIn, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull PlayerEntity playerIn, @NotNull Hand hand, @NotNull Direction facing, BlockRayTraceResult hit) {
        ItemStack stack = playerIn.getHeldItem(hand);
        FluidUtil.interactWithFluidHandler(playerIn, hand, tank);
        markDirty();
        return FluidUtil.getFluidHandler(stack).isPresent() ? ActionResultType.SUCCESS : ActionResultType.PASS;
    }

    @Override
    public void tick(@NotNull World world, @NotNull BlockPos pos) {
        if (world.isRemote || frame.isPowered() || !canTick(world,60, 10)) return;
        if (mode == EnumMode.NONE && ModularItemFrame.config.tankTransferRate.get() <= 0) return;

        IFluidHandler handler = frame.getAttachedTank();
        if (handler != null) {
            if (mode == EnumMode.DRAIN)
                FluidUtil.tryFluidTransfer(tank, handler, ModularItemFrame.config.tankTransferRate.get(), true);
            else FluidUtil.tryFluidTransfer(handler, tank, ModularItemFrame.config.tankTransferRate.get(), true);
            markDirty();
        }
    }

    @Override
    public void onFrameUpgradesChanged(World world, BlockPos pos, Direction facing) {
        int newCapacity = (int) Math.pow(ModularItemFrame.config.tankFrameCapacity.get() / (float) 1000, frame.getCapacityUpCount() + 1) * 1000;
        tank.setCapacity(newCapacity);
        markDirty();
    }

    @Override
    public void onRemove(@NotNull World world, @NotNull BlockPos pos, @NotNull Direction facing, @Nullable PlayerEntity player, @NotNull ItemStack modStack) {
        super.onRemove(world, pos, facing, player, modStack);
        for ( Direction face : Direction.values()) {
            if (face == facing.getOpposite()) continue;
            if (FluidUtil.tryPlaceFluid(null, world, null, pos.offset(facing.getOpposite()), tank, tank.drain(FluidAttributes.BUCKET_VOLUME, IFluidHandler.FluidAction.SIMULATE)))
                tank.drain(FluidAttributes.BUCKET_VOLUME, IFluidHandler.FluidAction.EXECUTE);
        }
    }

    @NotNull
    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = super.serializeNBT();
        nbt.putInt(NBT_MODE, mode.getIndex());
        nbt.put(NBT_TANK, tank.writeToNBT(new CompoundNBT()));
        return nbt;
    }

    @Override
    public void deserializeNBT(@NotNull CompoundNBT nbt) {
        super.deserializeNBT(nbt);
        int newCapacity = (int) Math.pow(ModularItemFrame.config.tankFrameCapacity.get(), frame.getCapacityUpCount() + 1) * FluidAttributes.BUCKET_VOLUME;
        if (newCapacity != tank.getCapacity())
            tank.setCapacity(newCapacity);

        if (nbt.contains(NBT_TANK)) tank.readFromNBT(nbt.getCompound(NBT_TANK));
        if (nbt.contains(NBT_MODE))
            mode = ModularItemFrame.config.tankTransferRate.get() > 0 ? EnumMode.values()[nbt.getInt(NBT_MODE)] : EnumMode.NONE;
    }

    public enum EnumMode {
        NONE(0, "modularitemframe.mode.no"),
        DRAIN(1, "modularitemframe.mode.in"),
        PUSH(2, "modularitemframe.mode.out");

        private final int index;
        private final TextComponent name;

        EnumMode(int indexIn, String nameIn) {
            index = indexIn;
            name = new TranslationTextComponent(nameIn);
        }

        public int getIndex() {
            return this.index;
        }

        public TextComponent getName() {
            return name;
        }
    }
}
