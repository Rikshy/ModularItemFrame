package de.shyrik.modularitemframe.common.module.t3;

import de.shyrik.modularitemframe.ModularItemFrame;
import de.shyrik.modularitemframe.common.block.ModularFrameBlock;
import de.shyrik.modularitemframe.common.module.t2.CraftingModule;
import de.shyrik.modularitemframe.util.InventoryHelper;
import de.shyrik.modularitemframe.util.ItemHelper;
import modularitemframe.api.ModuleTier;
import modularitemframe.api.inventory.ItemHandlerWrapper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

public class AutoCraftingModule extends CraftingModule {

    public static final ResourceLocation ID = new ResourceLocation(ModularItemFrame.MOD_ID, "module_t3_auto_crafting");
    public static final ResourceLocation BG = new ResourceLocation(ModularItemFrame.MOD_ID, "module/module_t3_auto_crafting");
    private static final TextComponent NAME = new TranslationTextComponent("modularitemframe.module.crafting_plus");

    @NotNull
    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @NotNull
    @Override
    public TextComponent getName() {
        return NAME;
    }

    @NotNull
    @Override
    public ModuleTier moduleTier() {
        return ModuleTier.T3;
    }

    @NotNull
    @Override
    public ResourceLocation frontTexture() {
        return BG;
    }

    @Override
    public void screw(@NotNull World world, @NotNull BlockPos pos, @NotNull PlayerEntity player, ItemStack driver) {
        if (!world.isRemote) {
            player.openContainer(getScreenHandler());
            markDirty();
        }
    }

    @Override
    public void tick(@NotNull World world, @NotNull BlockPos pos) {
        if (world.isRemote || frame.isPowered() || !canTick(world,60, 10)) return;

        ItemHandlerWrapper handler = frame.getAttachedInventory();
        if (handler != null) {
            autoCraft(handler, world, pos);
        }
    }

    private void autoCraft(ItemHandlerWrapper inventory, World world, BlockPos pos) {
        if (recipe == null) reloadRecipe();

        if (recipe == null || recipe.getRecipeOutput().isEmpty() || !InventoryHelper.canCraft(inventory.getHandler(), recipe))
            return;

        ItemHelper.ejectStack(world, pos, frame.getFacing(), recipe.getRecipeOutput().copy());

        InventoryHelper.removeIngredients(inventory.getHandler(), recipe);

        world.playSound(null, frame.getPos(), SoundEvents.BLOCK_LADDER_STEP, SoundCategory.BLOCKS, 1F, 1F);
    }
}
