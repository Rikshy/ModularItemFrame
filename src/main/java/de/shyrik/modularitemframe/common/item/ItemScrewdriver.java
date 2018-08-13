package de.shyrik.modularitemframe.common.item;

import com.teamwizardry.librarianlib.features.base.item.ItemModTool;
import de.shyrik.modularitemframe.common.module.ModuleEmpty;
import de.shyrik.modularitemframe.common.tile.TileModularFrame;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class ItemScrewdriver extends ItemModTool {
	private static final String NBT_MODE = "mode";

	public ItemScrewdriver() {
		super("screwdriver", ToolMaterial.IRON, "wrench");
		setMaxStackSize(1);
	}

	@Nonnull
	@Override
	public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
		TileEntity tmp = world.getTileEntity(pos);
		if (tmp instanceof TileModularFrame) {
			if (!world.isRemote) {
				TileModularFrame tile = (TileModularFrame) tmp;
				ItemStack driver = player.getHeldItem(hand);
				if (readModeFromNBT(driver) == EnumMode.INTERACT) {
					tile.module.screw(world, pos, player, driver);
				} else {
					tile.module.onRemove(world, pos, player);
					tile.setModule(new ModuleEmpty());
				}
				tile.markDirty();
			}
			return EnumActionResult.SUCCESS;
		}
		return super.onItemUseFirst(player, world, pos, side, hitX, hitY, hitZ, hand);
	}

	@Nonnull
	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, @Nonnull EnumHand handIn) {
		EnumActionResult result = EnumActionResult.PASS;
		if (!worldIn.isRemote && playerIn.isSneaking()) {
			ItemStack driver = playerIn.getHeldItem(handIn);
			EnumMode mode = readModeFromNBT(driver);
			mode = EnumMode.VALUES[mode.getIndex() + 1 >= EnumMode.values().length ? 0 : mode.getIndex() + 1];
			writeModeToNbt(driver, mode);
			playerIn.sendMessage(new TextComponentTranslation("modularitemframe.message.screw_mode_change", mode.getName()));

			result = EnumActionResult.SUCCESS;
		}
		return new ActionResult<>(result, playerIn.getHeldItem(handIn));
	}

	private void writeModeToNbt(ItemStack stack, EnumMode mode) {
		NBTTagCompound nbt = stack.getTagCompound();
		if (nbt == null) nbt = new NBTTagCompound();
		nbt.setInteger(NBT_MODE, mode.getIndex());
		stack.setTagCompound(nbt);
	}

	private EnumMode readModeFromNBT(ItemStack stack) {
		NBTTagCompound nbt = stack.getTagCompound();
		EnumMode mode = EnumMode.REMOVE;
		if (nbt == null) writeModeToNbt(stack, mode);
		else if (nbt.hasKey(NBT_MODE)) mode = EnumMode.VALUES[nbt.getInteger(NBT_MODE)];
		return mode;
	}

	public enum EnumMode {
		REMOVE(0, "modularitemframe.message.screw_mode_change.rem"),
		INTERACT(1, "modularitemframe.message.screw_mode_change.inter");
		//ROTATE(2, "modularitemframe.message.screw_mode_change.rot");

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
