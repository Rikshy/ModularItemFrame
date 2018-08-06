package de.shyrik.justcraftingframes.common.tile;

import com.teamwizardry.librarianlib.features.autoregister.TileRegister;
import com.teamwizardry.librarianlib.features.saving.Save;
import de.shyrik.justcraftingframes.ConfigValues;
import de.shyrik.justcraftingframes.common.block.BlockFrameBase;
import de.shyrik.justcraftingframes.common.utils.Utils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nonnull;

@TileRegister("nullify_frame")
public class TileNullifyFrame extends TileFluidBaseFrame implements ITickable {

	@Save
	public ItemStack lastStack = ItemStack.EMPTY;

	public TileNullifyFrame() {
		super(1000);
		tank.setFluid(FluidUtil.getFluidContained(new ItemStack(Items.LAVA_BUCKET)));
	}

	public void nullify(@Nonnull EntityPlayer player, @Nonnull EnumHand hand) {
		ItemStack held = player.getHeldItem(hand);
		if(!player.isSneaking() && !held.isEmpty()) {
			if (Utils.simpleAreStacksEqual(held, lastStack)) {
				if(held.getCount() + lastStack.getCount() > lastStack.getMaxStackSize())
					lastStack.setCount(lastStack.getMaxStackSize());
				else
					lastStack.setCount(lastStack.getCount() + held.getCount());
			} else {
				lastStack = held.copy();
			}
			held.setCount(0);
			world.playSound(null, pos, SoundEvents.BLOCK_LAVA_EXTINGUISH, SoundCategory.BLOCKS, 0.4F, 0.7F);
		} else if (player.isSneaking() && held.isEmpty() && !lastStack.isEmpty()) {
			player.setHeldItem(hand, lastStack);
			lastStack = ItemStack.EMPTY;
			world.playSound(null, pos, SoundEvents.ENTITY_ENDERPEARL_THROW, SoundCategory.BLOCKS, 0.4F, 0.7F);
		}
	}

	@Override
	public void update() {
		if(ConfigValues.CanNulliFrameSuckFromInvent && !world.isRemote) {
			if (world.getTotalWorldTime() % 20 != 0)
				return;

			EnumFacing facing = world.getBlockState(pos).getValue(BlockFrameBase.FACING);
			TileEntity tile = world.getTileEntity(pos.offset(facing));
			if(tile != null) {
				IItemHandlerModifiable trash = (IItemHandlerModifiable) tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing.getOpposite());
				if (trash != null) {
					for (int slot = 0; slot < trash.getSlots(); slot++) {
						if(!trash.getStackInSlot(slot).isEmpty()) {
							trash.setStackInSlot(slot, ItemStack.EMPTY);
							world.playSound(null, pos, SoundEvents.BLOCK_LAVA_EXTINGUISH, SoundCategory.BLOCKS, 0.4F, 0.7F);
							break;
						}
					}
				}
			}
		}
	}
}
