package de.shyrik.justcraftingframes.common.tile;

import com.teamwizardry.librarianlib.features.base.block.tile.TileMod;
import com.teamwizardry.librarianlib.features.saving.Save;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class TileItemBaseFrame extends TileMod {

	@Save
	public int rotation = 0;

	@Save
	public ItemStack displayItem = ItemStack.EMPTY;

	public float scale = 0.9f;
	public float offset =  0.05F;

	public void rotate(EntityPlayer player) {
		if (player.isSneaking()) {
			rotation += 20;
		} else {
			rotation -= 20;
		}
		markDirty();
	}
}
