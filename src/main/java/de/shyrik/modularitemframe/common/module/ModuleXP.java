package de.shyrik.modularitemframe.common.module;

import de.shyrik.modularitemframe.api.utils.XpUtils;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.ProbeMode;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketSpawnExperienceOrb;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.common.Optional;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

//thx openblocks and enderio
public class ModuleXP extends ModuleItem {

	private static final String NBT_XP = "xp";
	private static final String NBT_LEVEL = "level";

	public int experience;
	public int levels;
	public EnumMode mode = EnumMode.IN;

	public ModuleXP() {
		displayItem = new ItemStack(Items.EXPERIENCE_BOTTLE);
	}

	@Override
	public String getModuleName() {
		return I18n.format("modularitemframe.module.xp");
	}

	public void onBlockClicked(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull EntityPlayer playerIn) {
	}

	@Override
	public void screw(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull EntityPlayer playerIn, ItemStack driver) {
		if (!world.isRemote) {
			int modeIdx = mode.getIndex() + 1;
			if (modeIdx == EnumMode.values().length) modeIdx = 0;
			mode = EnumMode.VALUES[modeIdx];
			playerIn.sendMessage(new TextComponentTranslation("modularitemframe.message.xp_mode_change", mode.getName()));
		}
	}

	@Override
	public boolean onBlockActivated(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull EntityPlayer playerIn, @Nonnull EnumHand hand, @Nonnull EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (playerIn instanceof FakePlayer) return false;

		if (!worldIn.isRemote) {
			if (mode == EnumMode.IN) {
				if (playerIn.isSneaking()) drainPlayerXpToReachPlayerLevel(playerIn, 0);
				else drainPlayerXpToReachPlayerLevel(playerIn, playerIn.experienceLevel - 1);
			} else {
				if (playerIn.isSneaking()) drainContainerXpToReachPlayerLevel(playerIn, 0);
				else drainContainerXpToReachPlayerLevel(playerIn, playerIn.experienceLevel + 1);
			}
			tile.markDirty();
		}
		return true;
	}

	private void drainPlayerXpToReachPlayerLevel(@Nonnull EntityPlayer player, int level) {
		int targetXP = XpUtils.getExperienceForLevel(level);
		int drainXP = XpUtils.getPlayerXP(player) - targetXP;
		if (drainXP <= 0) {
			return;
		}
		drainXP = addExperience(drainXP);
		if (drainXP > 0) {
			XpUtils.addPlayerXP(player, -drainXP);
		}
	}

	public int addExperience(int xpToAdd) {
		int j = 21862 - experience;
		if (xpToAdd > j) {
			xpToAdd = j;
		}

		experience += xpToAdd;
		levels = XpUtils.getLevelForExperience(experience);
		//experience = (experience - XpUtils.getExperienceForLevel(levels)) / XpUtils.getXpBarCapacity(levels);
		return xpToAdd;
	}

	public void drainContainerXpToReachPlayerLevel(@Nonnull EntityPlayer player, int level) {
		int requiredXP = level == 0 ? experience : XpUtils.getExperienceForLevel(level) - XpUtils.getPlayerXP(player);

		requiredXP = Math.min(experience, requiredXP);

		addExperience(-requiredXP);
		XpUtils.addPlayerXP(player, requiredXP);
	}

	@Override
	public void onRemove(@NotNull World worldIn, @NotNull BlockPos pos, @Nullable EntityPlayer playerIn) {
		super.onRemove(worldIn, pos, playerIn);
		if(playerIn == null || playerIn instanceof FakePlayer)
			worldIn.spawnEntity(new EntityXPOrb(worldIn, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, experience));
		else
			playerIn.addExperience(experience);
	}

	@Override
	@Optional.Method(modid = "theoneprobe")
	public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world, IBlockState blockState, IProbeHitData data) {
		super.addProbeInfo(mode, probeInfo, player, world, blockState, data);
		probeInfo.horizontal().text(I18n.format("modularitemframe.tooltip.xp_level", levels));
	}

	@Nonnull
	@Override
	public List<String> getWailaBody(ItemStack itemStack, IWailaDataAccessor accessor, IWailaConfigHandler config) {
		List<String> tooltips = super.getWailaBody(itemStack, accessor, config);
		tooltips.add(I18n.format("modularitemframe.tooltip.xp_level", levels));
		return tooltips;
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound nbt = super.serializeNBT();
		nbt.setInteger(NBT_XP, experience);
		nbt.setInteger(NBT_LEVEL, levels);
		return nbt;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		super.deserializeNBT(nbt);
		if (nbt.hasKey(NBT_XP)) experience = nbt.getInteger(NBT_XP);
		if (nbt.hasKey(NBT_LEVEL)) levels = nbt.getInteger(NBT_LEVEL);
	}

	public enum EnumMode {
		IN(0, "input"), OUT(1, "output");

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
			return this.name;
		}

		static {
			for (EnumMode enummode : values())
				VALUES[enummode.index] = enummode;
		}
	}
}
