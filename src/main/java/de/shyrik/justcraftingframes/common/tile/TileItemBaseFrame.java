package de.shyrik.justcraftingframes.common.tile;

import com.teamwizardry.librarianlib.features.base.block.tile.TileMod;
import com.teamwizardry.librarianlib.features.base.block.tile.module.ModuleInventory;
import com.teamwizardry.librarianlib.features.saving.Module;
import com.teamwizardry.librarianlib.features.saving.Save;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.items.ItemStackHandler;

public class TileItemBaseFrame extends TileMod {

	@Save
	public int rotation = 0;

	public float scale = 0.9f;
	public float offset = 0.00f;

	@Module
	public ModuleInventory internal = new ModuleInventory(new ItemStackHandler(1));

	public TileItemBaseFrame() {
		internal.disallowSides(EnumFacing.VALUES);
	}

	public ItemStack getDisplayedItem() {
		return internal.getHandler().getStackInSlot(0);
	}

	public void setDisplayItem(ItemStack stack) {
		internal.getHandler().setStackInSlot(0, stack);
		markDirty();
	}

	public void rotate(EntityPlayer player) {
		if (player.isSneaking()) {
			rotation += 20;
		} else {
			rotation -= 20;
		}
		markDirty();
	}
}
