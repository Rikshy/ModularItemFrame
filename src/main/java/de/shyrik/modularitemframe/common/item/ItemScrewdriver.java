package de.shyrik.modularitemframe.common.item;

import com.teamwizardry.librarianlib.features.base.item.ItemMod;
import de.shyrik.modularitemframe.common.tile.TileModularFrame;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class ItemScrewdriver extends ItemMod {
	public ItemScrewdriver() {
		super("screwdriver");
		setMaxStackSize(1);
	}

	@Nonnull
	@Override
	public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {

		TileEntity tmp = world.getTileEntity(pos);
		if (tmp instanceof TileModularFrame) {
			if (!world.isRemote) {
				TileModularFrame tile = (TileModularFrame) tmp;
				if (tile.module.hasScrewInteraction()) {
					tile.module.screw(player, player.getHeldItem(hand));
				} else {
					tile.rotate(player);
				}
				tile.markDirty();
			}
			return EnumActionResult.SUCCESS;
		}
		return super.onItemUseFirst(player, world, pos, side, hitX, hitY, hitZ, hand);
	}
}
