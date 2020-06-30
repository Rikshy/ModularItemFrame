package de.shyrik.modularitemframe.api.util;

import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import java.util.List;

public class ItemHelper {

    public static boolean areItemsEqualIgnoreDurability(ItemStack[] toCheck, ItemStack itemStack) {
        for (ItemStack checkStack : toCheck) {
            if (ItemStack.areItemsEqualIgnoreDurability(checkStack, itemStack)) return true;
        }
        return false;
    }

    public static NonNullList<Ingredient> getIngredients(IRecipe recipe) {
        return (NonNullList<Ingredient>)recipe.getIngredients();
    }

    public static boolean simpleAreItemsEqual(ItemStack stack, ItemStack stack2) {
        return stack.getItem() == stack2.getItem();
    }

    public static boolean simpleAreStacksEqual(ItemStack stack, ItemStack stack2) {
        return stack.getItem() == stack2.getItem() && stack.getDamage() == stack2.getDamage();
    }

    public static void ejectStack(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull Direction facing, ItemStack stack) {
        Vec3d position = new Vec3d(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);
        Vec3d velocity = Vec3d.ZERO;

        switch (facing) {
            case UP:
                position = position.add(0.0D, -0.25D, 0.0D);
                velocity = velocity.add(0.0D, 0.2D, 0.0D);
                break;
            case DOWN:
                position = position.add(0.0D, -0.25D, 0.0D);
                break;
            case NORTH:
                position = position.add(0.0D, -0.25D, 0.25D);
                velocity = velocity.add(0.0D, 0.0D, -0.2D);
                break;
            case EAST:
                position = position.add(-0.25D, -0.25D, 0.0D);
                velocity = velocity.add(0.2D, 0.0D, 0.0D);
                break;
            case WEST:
                position = position.add(0.25D, -0.25D, 0.0D);
                velocity = velocity.add(-0.2D, 0.0D, 0.0D);
                break;
            case SOUTH:
                position = position.add(0.0D, -0.25D, -0.25D);
                velocity = velocity.add(0.0D, 0.0D, 0.2D);
                break;
        }

        ItemEntity item = new ItemEntity(world, position.x, position.y, position.z, stack);
        item.setVelocity(velocity.x, velocity.y, velocity.z);
        world.addEntity(item);
    }

    public static boolean increaseStackInList(List<ItemStack> list, ItemStack stack) {
        int idx = listContainsItemStackEqual(list, stack);
        if (idx >= 0) {
            ItemStack listStack = list.get(idx);
            listStack.grow(stack.getCount());
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

    public static IRecipe getRecipe(IItemHandler itemHandler, World world) {
        CraftingInventory craft = new CraftingInventory(new Container(ContainerType.CRAFTING, 1) {
            @Override
            public boolean canInteractWith(@Nonnull PlayerEntity playerIn) {
                return false;
            }
        }, 3, 3);

        for (int i = 0; i < 9; i++) {
            ItemStack stack = itemHandler.getStackInSlot(i);

            if (stack.isEmpty())
                continue;

            craft.setInventorySlotContents(i, stack.copy());
        }

        return world.getRecipeManager().getRecipe(IRecipeType.CRAFTING, craft, world).orElse(null);
    }
}
