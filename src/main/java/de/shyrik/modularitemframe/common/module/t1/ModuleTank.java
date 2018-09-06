package de.shyrik.modularitemframe.common.module.t1;

import de.shyrik.modularitemframe.ModularItemFrame;
import de.shyrik.modularitemframe.api.ConfigValues;
import de.shyrik.modularitemframe.common.module.ModuleFluid;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.ProbeMode;
import mcjty.theoneprobe.apiimpl.styles.ProgressStyle;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import net.minecraft.block.state.IBlockState;
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
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.common.Optional;

import javax.annotation.Nonnull;
import java.util.List;

public class ModuleTank extends ModuleFluid {

    private static final String NBT_MODE = "tankmode";

    public EnumMode mode = EnumMode.NONE;

    public ModuleTank() {
        tank.setCapacity(ConfigValues.TankFrameCapacity);
    }

    @Nonnull
    @Override
    public ResourceLocation frontTexture() {
        return new ResourceLocation(ModularItemFrame.MOD_ID, "blocks/tank");
    }

    @Override
    public String getModuleName() {
        return I18n.format("modularitemframe.module.tank");
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
        return nbt;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        super.deserializeNBT(nbt);
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
