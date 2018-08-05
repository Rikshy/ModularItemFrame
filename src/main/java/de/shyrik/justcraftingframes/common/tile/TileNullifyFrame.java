package de.shyrik.justcraftingframes.common.tile;

import com.teamwizardry.librarianlib.features.autoregister.TileRegister;
import com.teamwizardry.librarianlib.features.base.block.tile.TileMod;
import com.teamwizardry.librarianlib.features.base.block.tile.module.ModuleInventory;
import com.teamwizardry.librarianlib.features.saving.Module;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;

@TileRegister("nullify_frame")
public class TileNullifyFrame extends TileMod {

	public static final int SLOT_LAST = 0;

	@Module
	public ModuleInventory internal = new ModuleInventory(new ItemStackHandler(1));

	public TileNullifyFrame() {
		internal.disallowSides(EnumFacing.VALUES);
	}

	public void nullify(@Nonnull EntityPlayer player, @Nonnull EnumHand hand) {
		ItemStack held = player.getHeldItem(hand);
		if(!player.isSneaking() && !held.isEmpty()) {
			internal.getHandler().setStackInSlot(0, held.copy());
			held.setCount(0);
			world.playSound(null, pos, SoundEvents.BLOCK_LAVA_EXTINGUISH, SoundCategory.BLOCKS, 0.4F, 0.7F);
		} else if (player.isSneaking() && held.isEmpty() && !internal.getHandler().getStackInSlot(SLOT_LAST).isEmpty()) {
			player.setHeldItem(hand, internal.getHandler().getStackInSlot(SLOT_LAST));
			internal.getHandler().setStackInSlot(SLOT_LAST, ItemStack.EMPTY);
			world.playSound(null, pos, SoundEvents.ENTITY_ENDERPEARL_THROW, SoundCategory.BLOCKS, 0.4F, 0.7F);
		}
	}
}
