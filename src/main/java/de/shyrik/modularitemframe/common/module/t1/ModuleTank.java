package de.shyrik.modularitemframe.common.module.t1;

import de.shyrik.modularitemframe.ModularItemFrame;
import de.shyrik.modularitemframe.api.ConfigValues;
import de.shyrik.modularitemframe.api.ModuleBase;
import de.shyrik.modularitemframe.api.utils.RenderUtils;
import de.shyrik.modularitemframe.client.render.FrameRenderer;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.ProbeMode;
import mcjty.theoneprobe.apiimpl.styles.ProgressStyle;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
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
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.common.Optional;

import javax.annotation.Nonnull;
import java.util.List;

public class ModuleTank extends ModuleBase {

    public static final ResourceLocation LOC = new ResourceLocation(ModularItemFrame.MOD_ID,"module_t1_tank");
    private static final String NBT_MODE = "tankmode";
    private static final String NBT_TANK = "tank";

    public EnumMode mode = EnumMode.NONE;
    private FluidTank tank = new FluidTank(ConfigValues.TankFrameCapacity);

    @Nonnull
    @Override
    public ResourceLocation frontTexture() {
        return new ResourceLocation(ModularItemFrame.MOD_ID, "blocks/tank");
    }

    @Nonnull
    @Override
    public ResourceLocation backTexture() {
        return super.backTexture();//new ResourceLocation(ModularItemFrame.MOD_ID, "blocks/tank");
    }

    @Override
    public String getModuleName() {
        return I18n.format("modularitemframe.module.tank");
    }

    @Override
    public void specialRendering(FrameRenderer tesr, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (tank != null && tank.getFluid() != null && tank.getFluidAmount() > 0) {
            GlStateManager.pushMatrix();
            GlStateManager.enableBlend();

            FluidStack fluid = tank.getFluid();
            double amount = (double) tank.getFluidAmount() / (double) tank.getCapacity();
            int color = fluid.getFluid().getColor(fluid);
            final TextureAtlasSprite still = Minecraft.getMinecraft().getTextureMapBlocks().getTextureExtry(fluid.getFluid().getStill(fluid).toString());
            final TextureAtlasSprite flowing = Minecraft.getMinecraft().getTextureMapBlocks().getTextureExtry(fluid.getFluid().getFlowing(fluid).toString());

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
        if (playerIn instanceof FakePlayer && !ConfigValues.AllowFakePlayers) return;

        if (!world.isRemote) {
            if (ConfigValues.TankTransferRate > 0) {
                int modeIdx = mode.getIndex() + 1;
                if (modeIdx == EnumMode.values().length) modeIdx = 0;
                mode = EnumMode.VALUES[modeIdx];
                playerIn.sendMessage(new TextComponentTranslation("modularitemframe.message.tank_mode_change", mode.getName()));
            }
        }
    }

    @Override
    public boolean onBlockActivated(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull EntityPlayer playerIn, @Nonnull EnumHand hand, @Nonnull EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (playerIn instanceof FakePlayer && !ConfigValues.AllowFakePlayers) return false;

        ItemStack stack = playerIn.getHeldItem(hand);
        FluidUtil.interactWithFluidHandler(playerIn, hand, tank);
        tile.markDirty();
        return FluidUtil.getFluidHandler(stack) != null;
    }

    @Override
    public void tick(@Nonnull World world, @Nonnull BlockPos pos) {
        if (!world.isRemote && mode != EnumMode.NONE && ConfigValues.TankTransferRate > 0) {
            if (world.getTotalWorldTime() % (60 - 10 * countSpeed) != 0)
                return;
            EnumFacing facing = tile.blockFacing();
            TileEntity tile = world.getTileEntity(pos.offset(facing));
            if (tile != null) {
                IFluidHandler handler = tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, facing.getOpposite());
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
    @Optional.Method(modid = "theoneprobe")
    public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world, IBlockState blockState, IProbeHitData data) {
        super.addProbeInfo(mode, probeInfo, player, world, blockState, data);
        int color = 0;
        if (tank.getFluid() != null) color = tank.getFluid().getFluid().getColor();
        probeInfo.horizontal().progress(tank.getFluidAmount(), tank.getCapacity(), new ProgressStyle().suffix("mB").alternateFilledColor(color));
        probeInfo.horizontal().text(I18n.format("modularitemframe.tooltip.tankmode", this.mode.getName()));
    }

    @Nonnull
    @Override
    public List<String> getWailaBody(ItemStack itemStack, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        List<String> tooltips = super.getWailaBody(itemStack, accessor, config);
        tooltips.add(I18n.format("modularitemframe.tooltip.capacity", tank.getFluidAmount(), tank.getCapacity()));
        tooltips.add(I18n.format("modularitemframe.tooltip.tankmode", mode.getName()));
        return tooltips;
    }

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound nbt = super.serializeNBT();
        nbt.setInteger(NBT_MODE, mode.getIndex());
        nbt.setTag(NBT_TANK, tank.writeToNBT(new NBTTagCompound()));
        return nbt;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        super.deserializeNBT(nbt);
        if (nbt.hasKey(NBT_TANK)) tank.readFromNBT(nbt.getCompoundTag(NBT_TANK));
        if (nbt.hasKey(NBT_MODE))
            mode = ConfigValues.TankTransferRate > 0 ? EnumMode.VALUES[nbt.getInteger(NBT_MODE)] : EnumMode.NONE;
    }

    public enum EnumMode {
        NONE(0, "modularitemframe.message.tank_mode_change.no"), DRAIN(1, "modularitemframe.message.tank_mode_change.in"), PUSH(2, "modularitemframe.message.tank_mode_change.out");

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
