package de.shyrik.modularitemframe.common.module.t1;


import com.mojang.blaze3d.matrix.MatrixStack;
import de.shyrik.modularitemframe.ModularItemFrame;
import de.shyrik.modularitemframe.api.ModuleBase;
import de.shyrik.modularitemframe.api.util.FrameItemRenderer;
import de.shyrik.modularitemframe.api.util.ItemHandlerHelper;
import de.shyrik.modularitemframe.api.util.ItemHelper;
import de.shyrik.modularitemframe.client.render.FrameRenderer;
import de.shyrik.modularitemframe.common.container.*;
import de.shyrik.modularitemframe.common.network.NetworkHandler;
import de.shyrik.modularitemframe.common.network.packet.PlaySoundPacket;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftResultInventory;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;

public class ModuleCrafting extends ModuleBase implements IContainerCallbacks {

    public static final ResourceLocation LOC = new ResourceLocation(ModularItemFrame.MOD_ID, "module_t1_crafting");
    public static final ResourceLocation BG_LOC = new ResourceLocation(ModularItemFrame.MOD_ID, "block/module_t1_crafting");
    private static final String NBT_GHOSTINVENTORY = "ghostinventory";
    private static final String NBT_DISPLAY = "display";

    protected IRecipe recipe;
    private ItemStack displayItem = ItemStack.EMPTY;
    private ItemStackHandler ghostInventory = new ItemStackHandler(9);

    @Override
    public ResourceLocation getId() {
        return LOC;
    }

    @Nonnull
    @Override
    public ResourceLocation frontTexture() {
        return BG_LOC;
    }

    @Override
    public String getModuleName() {
        return I18n.format("modularitemframe.module.crafting");
    }

    @Override
    public void specialRendering(FrameRenderer renderer, @Nonnull MatrixStack matrixStack, float partialTicks, @Nonnull IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {
        FrameItemRenderer.renderOnFrame(displayItem, tile.blockFacing(), 0, 0.1F, TransformType.FIXED, matrixStack, buffer, combinedLight, combinedOverlay);
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
        if (!hasValidRecipe())
            playerIn.openContainer(getContainer(state, worldIn, pos));
        else {
            if (!worldIn.isRemote) {
                if (playerIn.isSneaking()) craft(playerIn, true);
                else craft(playerIn, false);
            }
        }
        tile.markDirty();
        return ActionResultType.SUCCESS;
    }

    private void craft(PlayerEntity player, boolean fullStack) {
        final IItemHandlerModifiable playerInventory = ItemHandlerHelper.getPlayerInv(player);
        final IItemHandlerModifiable workingInv = getWorkingInventories(playerInventory);
        reloadRecipe();

        if (workingInv == null || recipe == null || recipe.getRecipeOutput().isEmpty() || !ItemHandlerHelper.canCraft(workingInv, recipe.getIngredients()))
            return;

        int craftAmount = fullStack ? Math.min(ItemHandlerHelper.countPossibleCrafts(workingInv, recipe), 64) : 1;
        do {
            ItemStack remain = ItemHandlerHelper.giveStack(playerInventory, recipe.getRecipeOutput()); //use playerinventory here!
            if (!remain.isEmpty()) ItemHelper.ejectStack(player.world, tile.getPos(), tile.blockFacing(), remain);

            for (Ingredient ingredient : ItemHelper.getIngredients(recipe)) {
                if (ingredient.getMatchingStacks().length > 0) {
                    ItemHandlerHelper.removeFromInventory(workingInv, ingredient.getMatchingStacks());
                }
            }
        } while (--craftAmount > 0);
        NetworkHandler.sendAround(new PlaySoundPacket(tile.getPos(), SoundEvents.BLOCK_LADDER_STEP.getRegistryName(), SoundCategory.BLOCKS.getName(), 0.4F, 0.7F), player.world, tile.getPos(), 32);
    }

    protected IItemHandlerModifiable getWorkingInventories(IItemHandlerModifiable playerInventory) {
        return playerInventory;
    }

    protected boolean hasValidRecipe() {
        if (recipe == null) reloadRecipe();
        return recipe != null && !recipe.getRecipeOutput().isEmpty();
    }

    protected void reloadRecipe() {
        recipe = ItemHelper.getRecipe(ghostInventory, tile.getWorld());
        displayItem = recipe != null ? recipe.getRecipeOutput().copy() : ItemStack.EMPTY;
        tile.markDirty();
    }

    @Override
    public boolean isUsableByPlayer(PlayerEntity player) {
        BlockPos pos = tile.getPos();
        return player.getDistanceSq(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) <= 64;
    }

    @Override
    public void onContainerCraftingResultChanged(CraftResultInventory result) {
        displayItem = result.getStackInSlot(0);
        recipe = result.getRecipeUsed();
        tile.markDirty();
    }

    @Nonnull
    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT compound = super.serializeNBT();
        compound.put(NBT_DISPLAY, displayItem.serializeNBT());
        compound.put(NBT_GHOSTINVENTORY, ghostInventory.serializeNBT());
        return compound;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        super.deserializeNBT(nbt);
        if (nbt.contains(NBT_DISPLAY)) displayItem = ItemStack.read(nbt.getCompound(NBT_DISPLAY));
        if (nbt.contains(NBT_GHOSTINVENTORY)) ghostInventory.deserializeNBT(nbt.getCompound(NBT_GHOSTINVENTORY));
    }

    @Nonnull
    @Override
    public INamedContainerProvider getContainer(BlockState state, World worldIn, BlockPos pos) {
        return new SimpleNamedContainerProvider((id, playerInventory, player) ->
                new CraftingFrameContainer(
                        id,
                        ItemHandlerHelper.getPlayerInv(player),
                        ghostInventory,
                        player,
                       this),
                new TranslationTextComponent("gui.modularitemframe.crafting.name")
        );
    }
}
