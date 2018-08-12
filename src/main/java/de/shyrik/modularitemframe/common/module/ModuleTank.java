package de.shyrik.modularitemframe.common.module;

import de.shyrik.modularitemframe.ConfigValues;
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
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fml.common.Optional;

import javax.annotation.Nonnull;
import java.util.List;

public class ModuleTank extends ModuleFluid {

	public ModuleTank() {
		tank.setCapacity(ConfigValues.TankFrameCapacity);
	}

	@Override
	public String getModuleName() {
		return I18n.format("modularitemframe.module.tank");
	}

	@Override
	public void onBlockActivated(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull EntityPlayer playerIn, @Nonnull EnumHand hand, @Nonnull EnumFacing facing, float hitX, float hitY, float hitZ) {
		//ItemStack stack = playerIn.getHeldItem(hand);
		FluidUtil.interactWithFluidHandler(playerIn, hand, tank);
		tile.markDirty();
		//return FluidUtil.getFluidHandler(stack) != null; TODO
	}

	@Override
	@Optional.Method(modid = "theoneprobe")
	public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world, IBlockState blockState, IProbeHitData data) {
		super.addProbeInfo(mode, probeInfo, player, world, blockState, data);
		int color = 0;
		if (tank.getFluid() != null) color = tank.getFluid().getFluid().getColor();
		probeInfo.horizontal().progress(tank.getFluidAmount(), tank.getCapacity(), new ProgressStyle().suffix("mB").alternateFilledColor(color));
	}

	@Nonnull
	@Override
	public List<String> getWailaBody(ItemStack itemStack, IWailaDataAccessor accessor, IWailaConfigHandler config) {
		List<String> tooltips = super.getWailaBody(itemStack, accessor, config);
		tooltips.add(I18n.format("modularitemframe.tooltip.capacity", tank.getFluidAmount(), tank.getCapacity()));
		return tooltips;
	}
}
