package de.shyrik.justcraftingframes.common.tile;

import com.teamwizardry.librarianlib.features.autoregister.TileRegister;
import com.teamwizardry.librarianlib.features.base.block.tile.TileMod;
import com.teamwizardry.librarianlib.features.saving.Save;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;

import javax.annotation.Nonnull;

@TileRegister("nullify_frame")
public class TileNullifyFrame extends TileMod {

	@Save
	public ItemStack lastStack = ItemStack.EMPTY;

	public void nullify(@Nonnull EntityPlayer player, @Nonnull EnumHand hand) {
		ItemStack held = player.getHeldItem(hand);
		if(!player.isSneaking() && !held.isEmpty()) {
			lastStack = held.copy();
			held.setCount(0);
			world.playSound(null, pos, SoundEvents.BLOCK_LAVA_EXTINGUISH, SoundCategory.BLOCKS, 0.4F, 0.7F);
		} else if (player.isSneaking() && held.isEmpty() && !lastStack.isEmpty()) {
			player.setHeldItem(hand, lastStack);
			lastStack = ItemStack.EMPTY;
			world.playSound(null, pos, SoundEvents.ENTITY_ENDERPEARL_THROW, SoundCategory.BLOCKS, 0.4F, 0.7F);
		}
	}
}
