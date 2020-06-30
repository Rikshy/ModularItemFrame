package de.shyrik.modularitemframe.common.module.t3;

import de.shyrik.modularitemframe.ModularItemFrame;
import de.shyrik.modularitemframe.api.util.ItemHandlerHelper;
import de.shyrik.modularitemframe.api.util.ItemHelper;
import de.shyrik.modularitemframe.common.block.BlockModularFrame;
import de.shyrik.modularitemframe.common.module.t2.ModuleCraftingPlus;
import de.shyrik.modularitemframe.common.network.NetworkHandler;
import de.shyrik.modularitemframe.common.network.packet.PlaySoundPacket;
import net.minecraft.block.BlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nonnull;

public class ModuleAutoCrafting extends ModuleCraftingPlus {

    public static final ResourceLocation LOC = new ResourceLocation(ModularItemFrame.MOD_ID, "module_t3_auto_crafting");
    public static final ResourceLocation BG_LOC = new ResourceLocation(ModularItemFrame.MOD_ID, "block/module_t3_auto_crafting");

    @Nonnull
    @Override
    @OnlyIn(Dist.CLIENT)
    public ResourceLocation frontTexture() {
        return BG_LOC;
    }

    @Nonnull
    @Override
    @OnlyIn(Dist.CLIENT)
    public ResourceLocation innerTexture() {
        return BlockModularFrame.INNER_HARDEST_LOC;
    }

    @Override
    public ResourceLocation getId() {
        return LOC;
    }

    @Override
    public String getModuleName() {
        return I18n.format("modularitemframe.module.crafting_plus");
    }

    @Override
    public void screw(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull PlayerEntity playerIn, ItemStack driver) {
        if (!world.isRemote) {
            playerIn.openContainer(getContainer(tile.getBlockState(), world, pos));
            tile.markDirty();
        }
    }

    @Override
    public ActionResultType onBlockActivated(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nonnull PlayerEntity playerIn, @Nonnull Hand hand, @Nonnull Direction facing, BlockRayTraceResult hit) {
        return ActionResultType.FAIL;
    }

    @Override
    public void tick(@Nonnull World world, @Nonnull BlockPos pos) {
        if (world.isRemote) return;
        if (world.getGameTime() % (60 - 10 * tile.getSpeedUpCount()) != 0) return;

        IItemHandlerModifiable handler = (IItemHandlerModifiable) tile.getAttachedInventory();
        if (handler != null) {
            autoCraft(handler, world, pos);
        }
    }

    private void autoCraft(IItemHandlerModifiable inventory, World world, BlockPos pos) {
        if (recipe == null) reloadRecipe();

        if (recipe == null || recipe.getRecipeOutput().isEmpty() || !ItemHandlerHelper.canCraft(inventory, ItemHelper.getIngredients(recipe)))
            return;

        ItemHelper.ejectStack(world, pos, tile.blockFacing(), recipe.getRecipeOutput().copy());

        for (Ingredient ingredient : ItemHelper.getIngredients(recipe)) {
            if (ingredient.getMatchingStacks().length > 0) {
                ItemHandlerHelper.removeFromInventory(inventory, ingredient.getMatchingStacks());
            }
        }

        NetworkHandler.sendAround(new PlaySoundPacket(pos, SoundEvents.BLOCK_LADDER_STEP.getName(), SoundCategory.BLOCKS.getName(), 0.3F, 0.7F), world, tile.getPos(), 32);
    }
}
