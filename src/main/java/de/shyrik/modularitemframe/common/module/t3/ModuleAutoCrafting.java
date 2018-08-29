package de.shyrik.modularitemframe.common.module.t3;

import de.shyrik.modularitemframe.ModularItemFrame;
import de.shyrik.modularitemframe.api.utils.ItemUtils;
import de.shyrik.modularitemframe.client.gui.GuiHandler;
import de.shyrik.modularitemframe.common.module.t2.ModuleCraftingPlus;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nonnull;

public class ModuleAutoCrafting extends ModuleCraftingPlus {

    @Nonnull
    @Override
    public ResourceLocation innerTexture() {
        return new ResourceLocation(ModularItemFrame.MOD_ID, "blocks/hardest_inner");
    }

    @Override
    public String getModuleName() {
        return I18n.format("modularitemframe.module.crafting_plus");
    }

    @Override
    public void screw(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull EntityPlayer playerIn, ItemStack driver) {
        if (playerIn instanceof FakePlayer) return;

        if (!world.isRemote) {
            playerIn.openGui(ModularItemFrame.instance, GuiHandler.CRAFTING_FRAME, world, pos.getX(), pos.getY(), pos.getZ());
            tile.markDirty();
        }
    }

    @Override
    public boolean onBlockActivated(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull EntityPlayer playerIn, @Nonnull EnumHand hand, @Nonnull EnumFacing facing, float hitX, float hitY, float hitZ) {
        return false;
    }

    @Override
    public void tick(@Nonnull World world, @Nonnull BlockPos pos) {
        if (world.isRemote) return;
        if (world.getTotalWorldTime() % (60 - 10 * countSpeed) != 0) return;

        EnumFacing facing = tile.blockFacing();
        TileEntity neighbor = tile.getNeighbor(facing);
        if (neighbor != null) {
            IItemHandlerModifiable handler = (IItemHandlerModifiable) neighbor.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing.getOpposite());
            if (handler != null) {
                autoCraft(handler, world, pos, false);
            }
        }
    }

    private void autoCraft(IItemHandlerModifiable inventory, World world, BlockPos pos, boolean fullStack) {
        if (recipe == null) reloadRecipe();

        if (recipe == null || recipe.getRecipeOutput().isEmpty() || !ItemUtils.canCraft(inventory, recipe.getIngredients()))
            return;

        int craftAmount = fullStack ? Math.min(ItemUtils.countPossibleCrafts(inventory, recipe.getIngredients()), 64) : 1;
        do {
            ItemUtils.ejectStack(world, pos, tile.blockFacing(), recipe.getRecipeOutput().copy());

            for (Ingredient ingredient : recipe.getIngredients()) {
                if (ingredient.getMatchingStacks().length > 0) {
                    ItemUtils.removeFromInventory(inventory, ingredient.getMatchingStacks());
                }
            }
        } while (--craftAmount > 0);
        world.playSound(null, tile.getPos(), SoundEvents.BLOCK_LADDER_STEP, SoundCategory.BLOCKS, 0.4F, 0.7F);
    }
}
