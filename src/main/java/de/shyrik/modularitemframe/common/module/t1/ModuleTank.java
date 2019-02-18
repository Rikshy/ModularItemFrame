package de.shyrik.modularitemframe.common.module.t1;

import de.shyrik.modularitemframe.ModularItemFrame;
import de.shyrik.modularitemframe.init.ConfigValues;
import de.shyrik.modularitemframe.api.ModuleBase;
import de.shyrik.modularitemframe.api.utils.RenderUtils;
import de.shyrik.modularitemframe.client.render.FrameRenderer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ModuleTank extends ModuleBase {

    public static final ResourceLocation LOC = new ResourceLocation(ModularItemFrame.MOD_ID, "module_t1_tank");
    public static final ResourceLocation BG_LOC = new ResourceLocation(ModularItemFrame.MOD_ID, "blocks/module_t1_tank");
    private static final String NBT_MODE = "tankmode";
    private static final String NBT_TANK = "tank";

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
    public void specialRendering(FrameRenderer renderer, double x, double y, double z, float partialTicks, int destroyStage) {
        if (tank != null && tank.getFluid() != null && tank.getFluidAmount() > 0) {
            GlStateManager.pushMatrix();
            GlStateManager.enableBlend();

            FluidStack fluid = tank.getFluid();
            double amount = (double) tank.getFluidAmount() / (double) tank.getCapacity();
            int color = fluid.getFluid().getColor(fluid);
            final TextureAtlasSprite still = Minecraft.getInstance().getTextureMap().getSprite(fluid.getFluid().getStill(fluid));
            final TextureAtlasSprite flowing = Minecraft.getInstance().getTextureMap().getSprite(fluid.getFluid().getFlowing(fluid));

            RenderUtils.translateAgainstPlayer(tile.getPos(), false);

            switch (tile.blockFacing()) {
                case UP:
                    RenderUtils.renderFluid(fluid, tile.getPos(), 0.16d, 0.92d, 0.16d, 0.0d, 0.0d, 0.0d, 0.67d, 0.05d, amount * 0.67d, color, still, flowing);
                    break;
                case DOWN:
                    RenderUtils.renderFluid(fluid, tile.getPos(), 0.16d, 0.03d, 0.16d, 0.0d, 0.0d, 0.0d, 0.67d, 0.05d, amount * 0.67d, color, still, flowing);
                    RenderUtils.renderFluid(fluid, tile.getPos(), 0.16d, -0.01d, 0.16d, 0.0d, 0.0d, 0.0d, 0.67d, 0.05d, amount * 0.67d, color, still, flowing);
                    break;
                case NORTH: //done
                    RenderUtils.renderFluid(fluid, tile.getPos(), 0.16d, 0.16d, 0.035d, 0.0d, 0.0d, 0.0d, 0.67d, amount * 0.67d, 0.05d, color, still, flowing);
                    RenderUtils.renderFluid(fluid, tile.getPos(), 0.16d, 0.16d, -0.01d, 0.0d, 0.0d, 0.0d, 0.67d, amount * 0.67d, 0.05d, color, still, flowing);
                    break;
                case EAST:
                    RenderUtils.renderFluid(fluid, tile.getPos(), 0.92d, 0.16d, 0.16d, 0.0d, 0.0d, 0.0d, 0.05d, amount * 0.67d, 0.67d, color, still, flowing);
                    RenderUtils.renderFluid(fluid, tile.getPos(), 0.951d, 0.16d, 0.16d, 0.0d, 0.0d, 0.0d, 0.05d, amount * 0.67d, 0.67d, color, still, flowing);
                    break;
                case WEST: //done
                    RenderUtils.renderFluid(fluid, tile.getPos(), 0.03d, 0.16d, 0.16d, 0.0d, 0.0d, 0.0d, 0.05d, amount * 0.67d, 0.67d, color, still, flowing);
                    RenderUtils.renderFluid(fluid, tile.getPos(), -0.01d, 0.16d, 0.16d, 0.0d, 0.0d, 0.0d, 0.05d, amount * 0.67d, 0.67d, color, still, flowing);
                    break;
                case SOUTH: //done
                    RenderUtils.renderFluid(fluid, tile.getPos(), 0.16d, 0.16d, 0.92d, 0.0d, 0.0d, 0.0d, 0.67d, amount * 0.67d, 0.05d, color, still, flowing);
                    break;
            }
            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
        }
    }

    @Override
    public void screw(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull EntityPlayer playerIn, ItemStack driver) {
        if (!world.isRemote) {
            if (ConfigValues.TankTransferRate > 0) {
                int modeIdx = mode.getIndex() + 1;
                if (modeIdx == EnumMode.values().length) modeIdx = 0;
                mode = EnumMode.VALUES[modeIdx];
                playerIn.sendMessage(new TextComponentTranslation("modularitemframe.message.mode_change", mode.getName()));
            }
        }
    }

    @Override
    public boolean onBlockActivated(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull EntityPlayer playerIn, @Nonnull EnumHand hand, @Nonnull EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack stack = playerIn.getHeldItem(hand);
        FluidUtil.interactWithFluidHandler(playerIn, hand, tank);
        tile.markDirty();
        return FluidUtil.getFluidHandler(stack) != null;
    }

    @Override
    public void tick(@Nonnull World world, @Nonnull BlockPos pos) {
        if (!world.isRemote && mode != EnumMode.NONE && ConfigValues.TankTransferRate > 0) {
            if (world.getGameTime() % (60 - 10 * tile.getSpeedUpCount()) != 0) return;

            EnumFacing facing = tile.blockFacing();
            TileEntity tile = world.getTileEntity(pos.offset(facing));
            if (tile != null) {
                IFluidHandler handler = tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, facing.getOpposite()).orElse(null);
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
        int newCapacity = (int) Math.pow(ConfigValues.TankFrameCapacity / (float)Fluid.BUCKET_VOLUME, tile.getCapacityUpCount() + 1) * Fluid.BUCKET_VOLUME;
        tank.setCapacity(newCapacity);
        tile.markDirty();
    }

    @Override
    public void onRemove(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull EnumFacing facing, @Nullable EntityPlayer playerIn) {
        super.onRemove(worldIn, pos, facing, playerIn);
        for ( EnumFacing face : EnumFacing.values()) {
            if (face == facing.getOpposite()) continue;
            if (FluidUtil.tryPlaceFluid(null, worldIn, pos.offset(facing.getOpposite()), tank, tank.drain(Fluid.BUCKET_VOLUME, false)))
                tank.drain(Fluid.BUCKET_VOLUME, true);
        }
    }

    @Nonnull
    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound nbt = super.serializeNBT();
        nbt.putInt(NBT_MODE, mode.getIndex());
        nbt.put(NBT_TANK, tank.writeToNBT(new NBTTagCompound()));
        return nbt;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        super.deserializeNBT(nbt);
        if (nbt.hasUniqueId(NBT_TANK)) tank.readFromNBT(nbt.getCompound(NBT_TANK));
        if (nbt.hasUniqueId(NBT_MODE))
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
