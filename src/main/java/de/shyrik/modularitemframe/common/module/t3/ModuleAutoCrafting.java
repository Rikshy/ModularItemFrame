package de.shyrik.modularitemframe.common.module.t3;

import de.shyrik.modularitemframe.ModularItemFrame;
import de.shyrik.modularitemframe.api.utils.ItemUtils;
import de.shyrik.modularitemframe.client.gui.GuiHandler;
import de.shyrik.modularitemframe.common.block.BlockModularFrame;
import de.shyrik.modularitemframe.common.module.t2.ModuleCraftingPlus;
import de.shyrik.modularitemframe.common.network.NetworkHandler;
import de.shyrik.modularitemframe.common.network.packet.PlaySoundPacket;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nonnull;

public class ModuleAutoCrafting extends ModuleCraftingPlus {

    public static final ResourceLocation LOC = new ResourceLocation(ModularItemFrame.MOD_ID, "module_t3_auto_crafting");
    public static final ResourceLocation BG_LOC = new ResourceLocation(ModularItemFrame.MOD_ID, "blocks/module_t3_auto_crafting");

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
    public String getModuleName() {
        return I18n.format("modularitemframe.module.crafting_plus");
    }

    @Override
    public void screw(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull EntityPlayer playerIn, ItemStack driver) {
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
        if (world.getGameTime() % (60 - 10 * tile.getSpeedUpCount()) != 0) return;

        IItemHandlerModifiable handler = (IItemHandlerModifiable) tile.getAttachedInventory();
        if (handler != null) {
            autoCraft(handler, world, pos);
        }
    }

    private void autoCraft(IItemHandlerModifiable inventory, World world, BlockPos pos) {
        if (recipe == null) reloadRecipe();

        if (recipe == null || recipe.getRecipeOutput().isEmpty() || !ItemUtils.canCraft(inventory, recipe.getIngredients()))
            return;

        ItemUtils.ejectStack(world, pos, tile.blockFacing(), recipe.getRecipeOutput().copy());

        for (Ingredient ingredient : recipe.getIngredients()) {
            if (ingredient.getMatchingStacks().length > 0) {
                ItemUtils.removeFromInventory(inventory, ingredient.getMatchingStacks());
            }
        }

        NetworkHandler.sendAround(new PlaySoundPacket(pos, SoundEvents.BLOCK_LADDER_STEP.getName().toString(), SoundCategory.BLOCKS.getName(), 0.3F, 0.7F), tile.getPos(), world.provider.getDimension());
    }
}
