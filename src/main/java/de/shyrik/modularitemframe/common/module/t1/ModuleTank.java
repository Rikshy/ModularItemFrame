package de.shyrik.modularitemframe.common.module.t1;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.shyrik.modularitemframe.ModularItemFrame;
import de.shyrik.modularitemframe.api.ModuleBase;
import de.shyrik.modularitemframe.api.util.FrameFluidRenderer;
import de.shyrik.modularitemframe.client.render.FrameRenderer;
import de.shyrik.modularitemframe.init.ConfigValues;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ModuleTank extends ModuleBase {

    public static final ResourceLocation LOC = new ResourceLocation(ModularItemFrame.MOD_ID, "module_t1_tank");
    public static final ResourceLocation BG_LOC = new ResourceLocation(ModularItemFrame.MOD_ID, "block/module_t1_tank");
    private static final String NBT_MODE = "tankmode";
    private static final String NBT_TANK = "tank";

    private int BUCKET_VOLUME = 1000;

    public EnumMode mode = EnumMode.NONE;
    private FluidTank tank = new FluidTank(ConfigValues.TankFrameCapacity);

    @Override
    public ResourceLocation getId() {
        return LOC;
    }

    @Nonnull
    @Override
    public ResourceLocation frontTexture() {
        return BG_LOC;
    }

    @Nonnull
    @Override
    public ResourceLocation backTexture() {
        return BG_LOC;
    }

    @Override
    public String getModuleName() {
        return I18n.format("modularitemframe.module.tank");
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void specialRendering(FrameRenderer renderer, @Nonnull MatrixStack matrixStack, float partialTicks, @Nonnull IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {
        if (tank != null && tank.getFluidAmount() > 0) {
            FluidStack fluid = tank.getFluid();
            float amount = (float) tank.getFluidAmount() / (float) tank.getCapacity();

            switch (tile.blockFacing()) {
                case UP:
                    FrameFluidRenderer.renderFluidCuboid(fluid, matrixStack, buffer, combinedLight, 0.2f, 0.08f, 0.2f, 0.8f, 0.08f, 0.2f + amount * 0.6f);
                    break;
                case DOWN:
                    FrameFluidRenderer.renderFluidCuboid(fluid, matrixStack, buffer, combinedLight, 0.2f, 0.92f, 0.2f, 0.8f, 0.92f, 0.2f + amount * 0.6f);
                    break;
                case NORTH:
                    FrameFluidRenderer.renderFluidCuboid(fluid, matrixStack, buffer, combinedLight, 0.2f, 0.2f, 0.92f, 0.8f, 0.2f + amount * 0.6f, 0.92f);
                    break;
                case EAST:
                    FrameFluidRenderer.renderFluidCuboid(fluid, matrixStack, buffer, combinedLight, 0.08f, 0.2f, 0.2f, 0.08f, 0.2f + amount * 0.6f, 0.8f);
                    break;
                case WEST:
                    FrameFluidRenderer.renderFluidCuboid(fluid, matrixStack, buffer, combinedLight, 0.92f, 0.2f, 0.2f, 0.92f, 0.2f + amount * 0.6f, 0.8f);
                    break;
                case SOUTH:
                    FrameFluidRenderer.renderFluidCuboid(fluid, matrixStack, buffer, combinedLight, 0.2f, 0.2f, 0.08f, 0.8f, 0.2f + amount * 0.6f, 0.08f);
                    break;
            }
        }
    }

    @Override
    public void screw(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull PlayerEntity playerIn, ItemStack driver) {
        if (!world.isRemote) {
            if (ConfigValues.TankTransferRate > 0) {
                int modeIdx = mode.getIndex() + 1;
                if (modeIdx == EnumMode.values().length) modeIdx = 0;
                mode = EnumMode.VALUES[modeIdx];
                playerIn.sendMessage(new TranslationTextComponent("modularitemframe.message.mode_change", mode.getName()));
            }
        }
    }

    @Override
    public ActionResultType onBlockActivated(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nonnull PlayerEntity playerIn, @Nonnull Hand hand, @Nonnull Direction facing, BlockRayTraceResult hit) {
        ItemStack stack = playerIn.getHeldItem(hand);
        FluidUtil.interactWithFluidHandler(playerIn, hand, tank);
        tile.markDirty();
        return FluidUtil.getFluidHandler(stack) != null ? ActionResultType.SUCCESS : ActionResultType.FAIL;
    }

    @Override
    public void tick(@Nonnull World world, @Nonnull BlockPos pos) {
        if (!world.isRemote && mode != EnumMode.NONE && ConfigValues.TankTransferRate > 0) {
            if (world.getGameTime() % (60 - 10 * tile.getSpeedUpCount()) != 0) return;

            TileEntity neighbor = tile.getAttachedTile();
            if (neighbor != null) {
                IFluidHandler handler = tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, tile.blockFacing().getOpposite()).orElse(null);
                if (handler != null) {
                    if (mode == EnumMode.DRAIN)
                        FluidUtil.tryFluidTransfer(tank, handler, ConfigValues.TankTransferRate, true);
                    else FluidUtil.tryFluidTransfer(handler, tank, ConfigValues.TankTransferRate, true);
                    tile.markDirty();
                }
            }
        }
    }

    @Override
    public void onFrameUpgradesChanged() {
        int newCapacity = (int) Math.pow(ConfigValues.TankFrameCapacity / (float) BUCKET_VOLUME, tile.getCapacityUpCount() + 1) * BUCKET_VOLUME;
        tank.setCapacity(newCapacity);
        tile.markDirty();
    }

    @Override
    public void onRemove(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull Direction facing, @Nullable PlayerEntity playerIn) {
        super.onRemove(worldIn, pos, facing, playerIn);
        for ( Direction face : Direction.values()) {
            if (face == facing.getOpposite()) continue;
            if (FluidUtil.tryPlaceFluid(null, worldIn, Hand.MAIN_HAND, pos.offset(facing.getOpposite()), tank, tank.drain(BUCKET_VOLUME, IFluidHandler.FluidAction.SIMULATE)))
                tank.drain(BUCKET_VOLUME, IFluidHandler.FluidAction.EXECUTE);
        }
    }

    @Nonnull
    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = super.serializeNBT();
        nbt.putInt(NBT_MODE, mode.getIndex());
        nbt.put(NBT_TANK, tank.writeToNBT(new CompoundNBT()));
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        super.deserializeNBT(nbt);
        if (nbt.contains(NBT_TANK)) tank.readFromNBT(nbt.getCompound(NBT_TANK));
        if (nbt.contains(NBT_MODE))
            mode = ConfigValues.TankTransferRate > 0 ? EnumMode.VALUES[nbt.getInt(NBT_MODE)] : EnumMode.NONE;
    }

    public enum EnumMode {
        NONE(0, "modularitemframe.mode.no"),
        DRAIN(1, "modularitemframe.mode.in"),
        PUSH(2, "modularitemframe.mode.out");

        public static final EnumMode[] VALUES = new EnumMode[3];

        private final int index;
        private final String name;

        EnumMode(int indexIn, String nameIn) {
            index = indexIn;
            name = nameIn;
        }

        public int getIndex() {
            return this.index;
        }

        public String getName() {
            return I18n.format(this.name);
        }


        static {
            for (EnumMode enummode : values())
                VALUES[enummode.index] = enummode;
        }
    }
}
