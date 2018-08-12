package de.shyrik.modularitemframe.common.module;

import de.shyrik.modularitemframe.api.ConfigValues;
import de.shyrik.modularitemframe.ModularItemFrame;
import de.shyrik.modularitemframe.api.ModuleFrameBase;
import de.shyrik.modularitemframe.api.utils.RenderUtils;
import de.shyrik.modularitemframe.client.render.FrameRenderer;
import de.shyrik.modularitemframe.common.tile.TileModularFrame;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.ProbeMode;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class ModuleTeleport extends ModuleFrameBase {

	private static final String NBT_LINK = "linked_pos";
	private static final String NBT_LINKX = "linked_posX";
	private static final String NBT_LINKY = "linked_posY";
	private static final String NBT_LINKZ = "linked_posZ";

	public BlockPos linkedLoc = null;

	@Nonnull
	@Override
	public ResourceLocation frontTexture() {
		return new ResourceLocation(ModularItemFrame.MOD_ID, "blocks/item_frame_bg");
	}

	@Override
	public String getModuleName() {
		return I18n.format("modularitemframe.module.tele");
	}

	@Override
	public void specialRendering(FrameRenderer tesr, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		RenderUtils.renderEnd(tesr, x, y, z, tile.blockFacing());
	}

	@Override
	public boolean onBlockActivated(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull EntityPlayer playerIn, @Nonnull EnumHand hand, @Nonnull EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (!worldIn.isRemote) {
			if (hasValidConnection(worldIn, playerIn)) {
				BlockPos target;
				if (tile.blockFacing().getAxis().isHorizontal() || tile.blockFacing() == EnumFacing.UP)
					target = linkedLoc.offset(EnumFacing.DOWN);
				else target = linkedLoc;

				for (int i = 0; i < 64; i++)
					worldIn.spawnParticle(EnumParticleTypes.PORTAL, playerIn.posX, playerIn.posY + worldIn.rand.nextDouble() * 2.0D, playerIn.posZ, worldIn.rand.nextGaussian(), 0.0D, worldIn.rand.nextGaussian());
				Minecraft.getMinecraft().getSoundHandler().playSound(new PositionedSoundRecord(SoundEvents.BLOCK_PORTAL_TRAVEL, SoundCategory.AMBIENT, 0.4F, 1F, pos));
				playerIn.setPositionAndUpdate(target.getX() + 0.5F, target.getY() + 0.5F, target.getZ() + 0.5F);
				for (int i = 0; i < 64; i++)
					worldIn.spawnParticle(EnumParticleTypes.PORTAL, target.getX(), target.getY() + worldIn.rand.nextDouble() * 2.0D, target.getZ(), worldIn.rand.nextGaussian(), 0.0D, worldIn.rand.nextGaussian());
			}
		}
		return true;
	}

	@Override
	public void screw(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull EntityPlayer playerIn, ItemStack driver) {
		NBTTagCompound nbt = driver.getTagCompound();
		if (playerIn.isSneaking()) {
			if (nbt == null) nbt = new NBTTagCompound();
			nbt.setLong(NBT_LINK, tile.getPos().toLong());
			driver.setTagCompound(nbt);
			playerIn.sendMessage(new TextComponentTranslation("modularitemframe.message.loc_saved"));
		} else {
			if (nbt != null && nbt.hasKey(NBT_LINK)) {
				BlockPos tmp = BlockPos.fromLong(nbt.getLong(NBT_LINK));
				TileEntity targetTile = tile.getWorld().getTileEntity(tmp);
				if (!(targetTile instanceof TileModularFrame) || !((((TileModularFrame) targetTile).module instanceof ModuleTeleport)))
					playerIn.sendMessage(new TextComponentTranslation("modularitemframe.message.invalid_target"));
				else if (tile.getPos().getDistance(tmp.getX(), tmp.getY(), tmp.getZ()) > ConfigValues.MaxTeleportRange) {
					playerIn.sendMessage(new TextComponentTranslation("modularitemframe.message.too_far", ConfigValues.MaxTeleportRange));
				} else {
					linkedLoc = tmp;
					((ModuleTeleport) ((TileModularFrame) targetTile).module).linkedLoc = tile.getPos();
					playerIn.sendMessage(new TextComponentTranslation("modularitemframe.message.link_established"));
					nbt.removeTag(NBT_LINK);
					driver.setTagCompound(nbt);
				}
			}
		}
	}

	private boolean isTargetLocationValid(@Nonnull World worldIn) {

		if (tile.blockFacing().getAxis().isHorizontal() || tile.blockFacing() == EnumFacing.UP)
			return worldIn.isAirBlock(linkedLoc.offset(EnumFacing.DOWN));
		else return worldIn.isAirBlock(linkedLoc.offset(EnumFacing.UP));
	}

	public boolean hasValidConnection(@Nonnull World world, @Nullable EntityPlayer player) {

		if (linkedLoc == null) {
			if (player != null) player.sendMessage(new TextComponentTranslation("modularitemframe.message.no_target"));
			return false;
		}
		if (!(world.getTileEntity(linkedLoc) instanceof TileModularFrame) || !(((TileModularFrame) world.getTileEntity(linkedLoc)).module instanceof ModuleTeleport)) {
			if (player != null)
				player.sendMessage(new TextComponentTranslation("modularitemframe.message.invalid_target"));
			return false;
		}
		if (!isTargetLocationValid(world)) {
			if (player != null)
				player.sendMessage(new TextComponentTranslation("modularitemframe.message.location_blocked"));
			return false;
		}
		return true;
	}

	@Override
	@Optional.Method(modid = "theoneprobe")
	public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world, IBlockState blockState, IProbeHitData data) {
		super.addProbeInfo(mode, probeInfo, player, world, blockState, data);
		probeInfo.horizontal().text(I18n.format("modularitemframe.tooltip.tele_valid", hasValidConnection(world, null)));
	}

	@Nonnull
	@Override
	public List<String> getWailaBody(ItemStack itemStack, IWailaDataAccessor accessor, IWailaConfigHandler config) {
		List<String> tooltips = super.getWailaBody(itemStack, accessor, config);
		tooltips.add(I18n.format("modularitemframe.tooltip.tele_valid", hasValidConnection(accessor.getWorld(), null)));
		return tooltips;
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound compound = new NBTTagCompound();
		if (linkedLoc != null) {
			compound.setInteger(NBT_LINKX, linkedLoc.getX());
			compound.setInteger(NBT_LINKY, linkedLoc.getY());
			compound.setInteger(NBT_LINKZ, linkedLoc.getZ());
		}
		return compound;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		if (nbt.hasKey(NBT_LINKX))
			linkedLoc = new BlockPos(nbt.getInteger(NBT_LINKX), nbt.getInteger(NBT_LINKY), nbt.getInteger(NBT_LINKZ));
	}
}
