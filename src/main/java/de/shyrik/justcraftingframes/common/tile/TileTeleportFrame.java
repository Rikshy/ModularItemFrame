package de.shyrik.justcraftingframes.common.tile;

import com.teamwizardry.librarianlib.features.autoregister.TileRegister;
import com.teamwizardry.librarianlib.features.base.block.tile.TileMod;
import com.teamwizardry.librarianlib.features.saving.Save;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;

import javax.annotation.Nonnull;

@TileRegister("tele_frame")
public class TileTeleportFrame extends TileMod {

	@Save
	public BlockPos linkedLoc = null;

	public void teleport(@Nonnull EntityPlayer player) {
		if (linkedLoc == null) {
			player.sendMessage(new TextComponentTranslation("justcraftingframes.message.no_target"));
			return;
		}
		if (!(world.getTileEntity(linkedLoc) instanceof TileTeleportFrame)) {
			player.sendMessage(new TextComponentTranslation("justcraftingframes.message.invalid_target"));
			return;
		}
		if (!isTargetLocationValid()) {
			player.sendMessage(new TextComponentTranslation("justcraftingframes.message.location_blocked"));
			return;
		}
		BlockPos target = linkedLoc.offset(EnumFacing.DOWN);
		for (int i = 0; i < 64; i++)
			world.spawnParticle(EnumParticleTypes.PORTAL, player.posX, player.posY + world.rand.nextDouble() * 2.0D, player.posZ, world.rand.nextGaussian(), 0.0D, world.rand.nextGaussian());
		Minecraft.getMinecraft().getSoundHandler().playSound(new PositionedSoundRecord(SoundEvents.BLOCK_PORTAL_TRAVEL, SoundCategory.AMBIENT, 0.4F, 1F, pos));
		player.setPositionAndUpdate(target.getX() + 0.5F, target.getY() + 0.5F, target.getZ() + 0.5F);
		for (int i = 0; i < 64; i++)
			world.spawnParticle(EnumParticleTypes.PORTAL, target.getX(), target.getY() + world.rand.nextDouble() * 2.0D, target.getZ(), world.rand.nextGaussian(), 0.0D, world.rand.nextGaussian());
	}

	private boolean isTargetLocationValid() {
		return world.isAirBlock(linkedLoc.offset(EnumFacing.DOWN));
	}
}
