package de.shyrik.modularitemframe.common.module;

import de.shyrik.modularitemframe.ConfigValues;
import de.shyrik.modularitemframe.ModularItemFrame;
import de.shyrik.modularitemframe.api.ModuleFrameBase;
import de.shyrik.modularitemframe.api.utils.ItemUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nonnull;

public class ModuleDrop extends ModuleFrameBase {

	public static final String NBT_RANGE = "range";

	public int range = 0;

	@Nonnull
	@Override
	public ResourceLocation frontTexture() {
		return new ResourceLocation(ModularItemFrame.MOD_ID, "blocks/drop_frame_bg");
	}

	@Override
	public String getModuleName() {
		return I18n.format("modularitemframe.module.drop");
	}

	@Override
	public void screw(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull EntityPlayer playerIn, ItemStack driver) {
		if(!world.isRemote && ConfigValues.AddDropperRange > 0) {
			if (playerIn.isSneaking()) range--;
			else range++;
			if (range < 0) range = ConfigValues.AddDropperRange;
			if (range > ConfigValues.AddDropperRange) range = 0;
			playerIn.sendMessage(new TextComponentTranslation("modularitemframe.message.range_change", range + 1));
			tile.markDirty();
		}
	}

	@Override
	public boolean onBlockActivated(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull EntityPlayer playerIn, @Nonnull EnumHand hand, @Nonnull EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (!worldIn.isRemote) {
			ItemStack held = playerIn.getHeldItem(hand);
			if (!playerIn.isSneaking() && !held.isEmpty()) {
				ItemUtils.ejectStack(worldIn, pos, facing.getOpposite(), held.copy());
				held.setCount(0);
			}
		}
		return true;
	}

	@Override
	public void tick(@Nonnull World world, @Nonnull BlockPos pos) {
		if (!world.isRemote) {
			if (world.getTotalWorldTime() % 20 != 0) return;

			EnumFacing facing = tile.blockFacing();
			TileEntity tile = world.getTileEntity(pos.offset(facing, Math.min(range, ConfigValues.AddDropperRange) + 1));
			if (tile != null) {
				IItemHandlerModifiable inv = (IItemHandlerModifiable) tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing.getOpposite());
				if (inv != null) {
					for (int slot = 0; slot < inv.getSlots(); slot++) {
						if (!inv.getStackInSlot(slot).isEmpty()) {
							ItemUtils.ejectStack(world, pos, facing, inv.getStackInSlot(slot));
							inv.setStackInSlot(slot, ItemStack.EMPTY);
							break;
						}
					}
				}
			}
		}
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setInteger(NBT_RANGE, range);
		return nbt;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		if (nbt.hasKey(NBT_RANGE)) range = nbt.getInteger(NBT_RANGE);
	}
}
