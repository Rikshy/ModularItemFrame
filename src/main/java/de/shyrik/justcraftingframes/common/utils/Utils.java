package de.shyrik.justcraftingframes.common.utils;

import de.shyrik.justcraftingframes.common.block.BlockFrameBase;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;

public class Utils {

	public static IItemHandler copyItemHandler(IItemHandler itemHandler) {
		ItemStackHandler copy = new ItemStackHandler(itemHandler.getSlots());

		for (int i = 0; i < itemHandler.getSlots(); i++) {
			ItemStack stack = itemHandler.getStackInSlot(i);
			if (!stack.isEmpty()) {
				stack = stack.copy();
			}
			copy.setStackInSlot(i, stack);
		}
		return copy;
	}

	public static boolean areItemsEqualIgnoreDurability(ItemStack[] tocheck, ItemStack itemStack) {
		for( ItemStack check : tocheck) {
			if (ItemStack.areItemsEqualIgnoreDurability(check, itemStack))
				return true;
		}
		return false;
	}

	public static boolean canCraft(IItemHandler inventory, NonNullList<Ingredient> ingredients) {
		IItemHandler copy = Utils.copyItemHandler(inventory);
		for (Ingredient ingredient : ingredients) {
			if (ingredient.getMatchingStacks().length > 0) {
				boolean found = false;
				for (int slot = 0; slot < copy.getSlots(); slot++) {
					ItemStack stack = copy.extractItem(slot, 1, true);
					if (!stack.isEmpty() && areItemsEqualIgnoreDurability(ingredient.getMatchingStacks(), stack)) {
						copy.extractItem(slot, 1, false);
						found = true;
						break;
					}
				}
				if (!found)
					return false;
			}
		}
		return true;
	}

	public static int countPossibleCrafts(IItemHandler inventory, NonNullList<Ingredient> ingredients) {
		IItemHandler copy = Utils.copyItemHandler(inventory);
		boolean canCraft = true;
		int count = 0;
		while(canCraft) {
			for (Ingredient ingredient : ingredients) {
				if (ingredient.getMatchingStacks().length > 0) {
					boolean found = false;
					for (int slot = 0; slot < copy.getSlots(); slot++) {
						ItemStack stack = copy.extractItem(slot, 1, true);
						if (!stack.isEmpty() && areItemsEqualIgnoreDurability(ingredient.getMatchingStacks(), stack)) {
							copy.extractItem(slot, 1, false);
							found = true;
							break;
						}
					}
					if (!found) {
						canCraft = false;
						break;
					}
				}
			}
			if(canCraft)count++;
		}
		return count;
	}

	public static void removeFromInventory(IItemHandlerModifiable inventory, ItemStack[] toRemove) {
		for (int invSlot = 0; invSlot < inventory.getSlots(); invSlot++) {
			ItemStack stack = inventory.extractItem(invSlot, 1, true);
			if (!stack.isEmpty() && areItemsEqualIgnoreDurability( toRemove, stack)) {
				inventory.extractItem(invSlot, 1, false);
				break;
			}
		}
	}

	public static ItemStack giveStack(IItemHandlerModifiable inventory, ItemStack stack) {
		int slot = getFittingSlot(inventory, stack);
		if (slot < 0 )
			return stack;
		ItemStack remain = inventory.insertItem(slot, stack, false);
		if (!remain.isEmpty()) giveStack(inventory, remain);
		return ItemStack.EMPTY;
	}

	public static int getFittingSlot(IItemHandler inventory, ItemStack stack) {
		int slot = getItemEqualSlot(inventory, stack);
		return slot < 0 ? getFirstUnOccupiedSlot(inventory) : slot;
	}

	public static int getItemEqualSlot(IItemHandler inventory, ItemStack stack) {
		for (int i = 0; i < inventory.getSlots(); ++i) if (inventory.getStackInSlot(i).getCount() < inventory.getStackInSlot(i).getMaxStackSize() && ItemStack.areItemsEqual(inventory.getStackInSlot(i), stack)) return i;
		return -1;
	}

	public static int getFirstUnOccupiedSlot(IItemHandler inventory) {
		for (int i = 0; i < inventory.getSlots(); ++i) if (inventory.getStackInSlot(i).isEmpty()) return i;
		return -1;
	}

	public static int getLastUnOccupiedSlot(IItemHandler inventory) {
		for (int i = inventory.getSlots() - 1; i >= 0; i--) if (inventory.getStackInSlot(i).isEmpty()) return i;
		return -1;
	}

	public static boolean simpleAreItemsEqual(ItemStack stack, ItemStack stack2) {
		return stack.getItem() == stack2.getItem();
	}

	public static boolean simpleAreStacksEqual(ItemStack stack, ItemStack stack2) {
		return stack.getItem() == stack2.getItem() && stack.getItemDamage() == stack2.getItemDamage();
	}

	public static void ejectStack(World world, BlockPos pos, ItemStack stack) {
		Vec3d position = new Vec3d(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);
		Vec3d velocity = Vec3d.ZERO;

		switch (world.getBlockState(pos).getValue(BlockFrameBase.FACING)) {
			case UP:
				position = position.addVector(0.0D, -0.25D, 0.0D);
				velocity = velocity.addVector(0.0D, 0.2D, 0.0D);
				break;
			case DOWN:
				position = position.addVector(0.0D, -0.25D, 0.0D);
				break;
			case NORTH:
				position = position.addVector(0.0D, -0.5D, -0.25D);
				velocity = velocity.addVector(0.0D, 0.0D, 0.2D);
				break;
			case EAST:
				position = position.addVector(0.0D, -0.5D, 0.25D);
				velocity = velocity.addVector(0.0D, 0.0D, -0.2D);
				break;
			case WEST:
				position = position.addVector(0.25D, -0.5D, 0.0D);
				velocity = velocity.addVector(-0.2D, 0.0D, 0.0D);
				break;
			case SOUTH:
				position = position.addVector(-0.25D, -0.5D, 0.0D);
				velocity = velocity.addVector(0.2D, 0.0D, 0.0D);
				break;
		}

		EntityItem item = new EntityItem(world, position.x, position.y, position.z, stack);
		item.setVelocity( velocity.x, velocity.y, velocity.z);
		world.spawnEntity(item);
	}


	public static void addTexturedQuad(BufferBuilder buffer, TextureAtlasSprite sprite, double x, double y, double z, double width, double height, double length, EnumFacing face, int color, int brightness) {

		if (sprite == null) {
			return;
		}

		final int light1 = brightness >> 0x10 & 0xFFFF;
		final int light2 = brightness & 0xFFFF;
		final int alpha = color >> 24 & 0xFF;
		final int red = color >> 16 & 0xFF;
		final int green = color >> 8 & 0xFF;
		final int blue = color & 0xFF;

		double minU;
		double maxU;
		double minV;
		double maxV;

		final double size = 16f;

		final double x2 = x + width;
		final double y2 = y + height;
		final double z2 = z + length;

		final double u = x % 1d;
		double u1 = u + width;

		while (u1 > 1f) {
			u1 -= 1f;
		}

		final double vy = y % 1d;
		double vy1 = vy + height;

		while (vy1 > 1f) {
			vy1 -= 1f;
		}

		final double vz = z % 1d;
		double vz1 = vz + length;

		while (vz1 > 1f) {
			vz1 -= 1f;
		}

		switch (face) {

			case DOWN:

			case UP:
				minU = sprite.getInterpolatedU(u * size);
				maxU = sprite.getInterpolatedU(u1 * size);
				minV = sprite.getInterpolatedV(vz * size);
				maxV = sprite.getInterpolatedV(vz1 * size);
				break;

			case NORTH:

			case SOUTH:
				minU = sprite.getInterpolatedU(u1 * size);
				maxU = sprite.getInterpolatedU(u * size);
				minV = sprite.getInterpolatedV(vy * size);
				maxV = sprite.getInterpolatedV(vy1 * size);
				break;

			case WEST:

			case EAST:
				minU = sprite.getInterpolatedU(vz1 * size);
				maxU = sprite.getInterpolatedU(vz * size);
				minV = sprite.getInterpolatedV(vy * size);
				maxV = sprite.getInterpolatedV(vy1 * size);
				break;

			default:
				minU = sprite.getMinU();
				maxU = sprite.getMaxU();
				minV = sprite.getMinV();
				maxV = sprite.getMaxV();
		}

		switch (face) {

			case DOWN:
				buffer.pos(x, y, z).color(red, green, blue, alpha).tex(minU, minV).lightmap(light1, light2).endVertex();
				buffer.pos(x2, y, z).color(red, green, blue, alpha).tex(maxU, minV).lightmap(light1, light2).endVertex();
				buffer.pos(x2, y, z2).color(red, green, blue, alpha).tex(maxU, maxV).lightmap(light1, light2).endVertex();
				buffer.pos(x, y, z2).color(red, green, blue, alpha).tex(minU, maxV).lightmap(light1, light2).endVertex();
				break;

			case UP:
				buffer.pos(x, y2, z).color(red, green, blue, alpha).tex(minU, minV).lightmap(light1, light2).endVertex();
				buffer.pos(x, y2, z2).color(red, green, blue, alpha).tex(minU, maxV).lightmap(light1, light2).endVertex();
				buffer.pos(x2, y2, z2).color(red, green, blue, alpha).tex(maxU, maxV).lightmap(light1, light2).endVertex();
				buffer.pos(x2, y2, z).color(red, green, blue, alpha).tex(maxU, minV).lightmap(light1, light2).endVertex();
				break;

			case NORTH:
				buffer.pos(x, y, z).color(red, green, blue, alpha).tex(minU, maxV).lightmap(light1, light2).endVertex();
				buffer.pos(x, y2, z).color(red, green, blue, alpha).tex(minU, minV).lightmap(light1, light2).endVertex();
				buffer.pos(x2, y2, z).color(red, green, blue, alpha).tex(maxU, minV).lightmap(light1, light2).endVertex();
				buffer.pos(x2, y, z).color(red, green, blue, alpha).tex(maxU, maxV).lightmap(light1, light2).endVertex();
				break;

			case SOUTH:
				buffer.pos(x, y, z2).color(red, green, blue, alpha).tex(maxU, maxV).lightmap(light1, light2).endVertex();
				buffer.pos(x2, y, z2).color(red, green, blue, alpha).tex(minU, maxV).lightmap(light1, light2).endVertex();
				buffer.pos(x2, y2, z2).color(red, green, blue, alpha).tex(minU, minV).lightmap(light1, light2).endVertex();
				buffer.pos(x, y2, z2).color(red, green, blue, alpha).tex(maxU, minV).lightmap(light1, light2).endVertex();
				break;

			case WEST:
				buffer.pos(x, y, z).color(red, green, blue, alpha).tex(maxU, maxV).lightmap(light1, light2).endVertex();
				buffer.pos(x, y, z2).color(red, green, blue, alpha).tex(minU, maxV).lightmap(light1, light2).endVertex();
				buffer.pos(x, y2, z2).color(red, green, blue, alpha).tex(minU, minV).lightmap(light1, light2).endVertex();
				buffer.pos(x, y2, z).color(red, green, blue, alpha).tex(maxU, minV).lightmap(light1, light2).endVertex();
				break;

			case EAST:
				buffer.pos(x2, y, z).color(red, green, blue, alpha).tex(minU, maxV).lightmap(light1, light2).endVertex();
				buffer.pos(x2, y2, z).color(red, green, blue, alpha).tex(minU, minV).lightmap(light1, light2).endVertex();
				buffer.pos(x2, y2, z2).color(red, green, blue, alpha).tex(maxU, minV).lightmap(light1, light2).endVertex();
				buffer.pos(x2, y, z2).color(red, green, blue, alpha).tex(maxU, maxV).lightmap(light1, light2).endVertex();
				break;
		}
	}

}
