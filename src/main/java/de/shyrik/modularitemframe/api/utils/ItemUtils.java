package de.shyrik.modularitemframe.api.utils;

import com.teamwizardry.librarianlib.core.common.RecipeGeneratorHandler;
import net.minecraft.client.util.RecipeItemHelper;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import java.util.List;

public class ItemUtils {

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
		for (ItemStack check : tocheck) {
			if (ItemStack.areItemsEqualIgnoreDurability(check, itemStack)) return true;
		}
		return false;
	}

	public static boolean canCraft(IItemHandler inventory, NonNullList<Ingredient> ingredients) {
		IItemHandler copy = ItemUtils.copyItemHandler(inventory);
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
				if (!found) return false;
			}
		}
		return true;
	}

	public static int countPossibleCrafts(IItemHandler inventory, NonNullList<Ingredient> ingredients) {
		IItemHandler copy = ItemUtils.copyItemHandler(inventory);
		boolean canCraft = true;
		int count = 0;
		while (canCraft) {
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
			if (canCraft) count++;
		}
		return count;
	}

	public static void removeFromInventory(IItemHandlerModifiable inventory, ItemStack[] toRemove) {
		for (int invSlot = 0; invSlot < inventory.getSlots(); invSlot++) {
			ItemStack stack = inventory.extractItem(invSlot, 1, true);
			if (!stack.isEmpty() && areItemsEqualIgnoreDurability(toRemove, stack)) {
				inventory.extractItem(invSlot, 1, false);
				break;
			}
		}
	}

	public static ItemStack giveStack(IItemHandlerModifiable inventory, ItemStack stack) {
		int slot = getFittingSlot(inventory, stack);
		if (slot < 0) return stack;
		ItemStack remain = inventory.insertItem(slot, stack, false);
		if (!remain.isEmpty()) giveStack(inventory, remain);
		return ItemStack.EMPTY;
	}

	public static void giveAllPossibleStacks(IItemHandlerModifiable target, IItemHandlerModifiable source, ItemStack stack) {
        for (int i = 0; i < source.getSlots(); i++) {
            ItemStack sourceStack = source.getStackInSlot(i);
            if(simpleAreStacksEqual(stack, sourceStack)) {
                ItemStack remain = giveStack(target, sourceStack.copy());
                sourceStack.setCount(remain.getCount());
                if (!remain.isEmpty()) break;
            }
        }
    }

	public static int getFittingSlot(IItemHandler inventory, ItemStack stack) {
		int slot = getItemEqualSlot(inventory, stack);
		return slot < 0 ? getFirstUnOccupiedSlot(inventory) : slot;
	}

	public static int getItemEqualSlot(IItemHandler inventory, ItemStack stack) {
		for (int i = 0; i < inventory.getSlots(); ++i)
			if (inventory.getStackInSlot(i).getCount() < inventory.getStackInSlot(i).getMaxStackSize() && ItemStack.areItemsEqual(inventory.getStackInSlot(i), stack))
				return i;
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

	public static int getFirstOccupiedSlot(IItemHandler inventory) {
		for (int i = 0; i < inventory.getSlots(); ++i) if (!inventory.getStackInSlot(i).isEmpty()) return i;
		return -1;
	}

	public static boolean simpleAreItemsEqual(ItemStack stack, ItemStack stack2) {
		return stack.getItem() == stack2.getItem();
	}

	public static boolean simpleAreStacksEqual(ItemStack stack, ItemStack stack2) {
		return stack.getItem() == stack2.getItem() && stack.getItemDamage() == stack2.getItemDamage();
	}

	public static void ejectStack(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull EnumFacing facing, ItemStack stack) {
		Vec3d position = new Vec3d(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);
		Vec3d velocity = Vec3d.ZERO;

		switch (facing) {
			case UP:
				position = position.addVector(0.0D, -0.25D, 0.0D);
				break;
			case DOWN:
				position = position.addVector(0.0D, -0.25D, 0.0D);
				velocity = velocity.addVector(0.0D, 0.2D, 0.0D);
				break;
			case NORTH:
				position = position.addVector(0.0D, -0.5D, -0.25D);
				velocity = velocity.addVector(0.0D, 0.0D, 0.2D);
				break;
			case EAST:
				position = position.addVector(0.25D, -0.5D, 0.0D);
				velocity = velocity.addVector(-0.2D, 0.0D, 0.0D);
				break;
			case WEST:
				position = position.addVector(-0.25D, -0.5D, 0.0D);
				velocity = velocity.addVector(0.2D, 0.0D, 0.0D);
				break;
			case SOUTH:
				position = position.addVector(0.0D, -0.5D, 0.25D);
				velocity = velocity.addVector(0.0D, 0.0D, -0.2D);
				break;
		}

		EntityItem item = new EntityItem(world, position.x, position.y, position.z, stack);
		item.setVelocity(velocity.x, velocity.y, velocity.z);
		world.spawnEntity(item);
	}

	public static boolean increaseStackinList(List<ItemStack> list, ItemStack stack) {
		int idx = listContainsItemStackEqual(list, stack);
		if (idx >= 0) {
			ItemStack listStack = list.get(idx);
			listStack.setCount(listStack.getCount() + stack.getCount());
			return true;
		}
		return false;
	}

	public static int listContainsItemStackEqual(List<ItemStack> list, ItemStack stack) {
		for (int i = 0; i < list.size(); ++i) {
			if (simpleAreItemsEqual(stack, list.get(i))) return i;
		}
		return -1;
	}

	public static IItemHandlerModifiable getPlayerInv(@Nonnull EntityPlayer player) {
		return (IItemHandlerModifiable) player.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.UP);
	}

	public static IRecipe getRecipe(IItemHandler itemHandler, World world) {
		InventoryCrafting craft = new InventoryCrafting(new Container() {
			@Override
			public boolean canInteractWith(@Nonnull EntityPlayer playerIn) {
				return false;
			}
		}, 3 , 3);

        for(int i = 0; i < 9; i++) {
            ItemStack stack = itemHandler.getStackInSlot(i);

            if(stack.isEmpty())
                continue;

            craft.setInventorySlotContents(i, stack.copy());
        }

        for(IRecipe recipe : ForgeRegistries.RECIPES)
            if (recipe.matches(craft, world)) {
                return recipe;
            }
        return null;
    }
}
