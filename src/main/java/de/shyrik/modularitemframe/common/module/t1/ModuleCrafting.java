package de.shyrik.modularitemframe.common.module.t1;

import de.shyrik.modularitemframe.ModularItemFrame;
import de.shyrik.modularitemframe.api.ModuleBase;
import de.shyrik.modularitemframe.api.utils.ItemUtils;
import de.shyrik.modularitemframe.api.utils.RenderUtils;
import de.shyrik.modularitemframe.client.gui.GuiHandler;
import de.shyrik.modularitemframe.client.render.FrameRenderer;
import de.shyrik.modularitemframe.common.container.ContainerCraftingFrame;
import de.shyrik.modularitemframe.common.container.IContainerCallbacks;
import de.shyrik.modularitemframe.common.container.InteractionObject;
import de.shyrik.modularitemframe.common.network.NetworkHandler;
import de.shyrik.modularitemframe.common.network.packet.PlaySoundPacket;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.IInteractionObject;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ModuleCrafting extends ModuleBase implements IContainerCallbacks, IInteractionObject {

    public static final ResourceLocation LOC = new ResourceLocation(ModularItemFrame.MOD_ID, "module_t1_craft");
    public static final ResourceLocation BG_LOC = new ResourceLocation(ModularItemFrame.MOD_ID, "blocks/module_t1_crafting");
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
        return I18n.format("modularitemframe.module.craft");
    }

    @Override
    public void specialRendering(FrameRenderer renderer, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        GlStateManager.pushMatrix();
        GlStateManager.translated(x + 0.5D, y + 0.5D, z + 0.5D);
        GlStateManager.scaled(0.7F, 0.7F, 0.7F);
        GlStateManager.pushMatrix();

        RenderUtils.renderItem(displayItem, tile.blockFacing(), 0, -0.05F);

        GlStateManager.popMatrix();
        GlStateManager.popMatrix();
    }

    @Override
    public void screw(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull EntityPlayer playerIn, ItemStack driver) {
        if (!world.isRemote) {
            playerIn.displayGui(this);
            playerIn.openGui(ModularItemFrame.instance, GuiHandler.CRAFTING_FRAME, world, pos.getX(), pos.getY(), pos.getZ());
            tile.markDirty();
        }
    }

    @Override
    public boolean onBlockActivated(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull EntityPlayer playerIn, @Nonnull EnumHand hand, @Nonnull EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!hasValidRecipe())
            playerIn.openGui(ModularItemFrame.instance, GuiHandler.getMetaGuiId(GuiHandler.CRAFTING_FRAME, facing), worldIn, pos.getX(), pos.getY(), pos.getZ());
        else {
            if (!worldIn.isRemote) {
                if (playerIn.isSneaking()) craft(playerIn, true);
                else craft(playerIn, false);
            }
        }
        tile.markDirty();
        return true;
    }

    @Override
    public ContainerCraftingFrame createContainer(final EntityPlayer player) {
        final IItemHandlerModifiable playerInventory = ItemUtils.getPlayerInv(player);

        return new ContainerCraftingFrame(playerInventory, ghostInventory, player, this);
    }

    private void craft(EntityPlayer player, boolean fullStack) {
        final IItemHandlerModifiable playerInventory = ItemUtils.getPlayerInv(player);
        final IItemHandlerModifiable workingInv = getWorkingInventories(playerInventory);

        if (recipe == null) reloadRecipe();

        if (workingInv == null || recipe == null || recipe.getRecipeOutput().isEmpty() || !ItemUtils.canCraft(workingInv, recipe.getIngredients()))
            return;

        int craftAmount = fullStack ? Math.min(ItemUtils.countPossibleCrafts(workingInv, recipe), 64) : 1;
        do {
            ItemStack remain = ItemUtils.giveStack(playerInventory, recipe.getRecipeOutput()); //use playerinventory here!
            if (!remain.isEmpty()) ItemUtils.ejectStack(player.world, tile.getPos(), tile.blockFacing(), remain);

            for (Ingredient ingredient : recipe.getIngredients()) {
                if (ingredient.getMatchingStacks().length > 0) {
                    ItemUtils.removeFromInventory(workingInv, ingredient.getMatchingStacks());
                }
            }
        } while (--craftAmount > 0);
        NetworkHandler.sendAround(new PlaySoundPacket(tile.getPos(), SoundEvents.BLOCK_LADDER_STEP.getSoundName().toString(), SoundCategory.BLOCKS.getName(), 0.4F, 0.7F), tile.getPos(), player.dimension);
    }

    protected IItemHandlerModifiable getWorkingInventories(IItemHandlerModifiable playerInventory) {
        return playerInventory;
    }

    protected boolean hasValidRecipe() {
        if (recipe == null) reloadRecipe();
        return recipe != null && !recipe.getRecipeOutput().isEmpty();
    }

    protected void reloadRecipe() {
        recipe = ItemUtils.getRecipe(ghostInventory, tile.getWorld());
        displayItem = recipe != null ? recipe.getRecipeOutput().copy() : ItemStack.EMPTY;
        tile.markDirty();
    }

    @Override
    public boolean isUsableByPlayer(EntityPlayer player) {
        BlockPos pos = tile.getPos();
        return player.getDistanceSq(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) <= 64;
    }

    @Override
    public void onContainerCraftingResultChanged(InventoryCraftResult result) {
        displayItem = result.getStackInSlot(0);
        recipe = result.getRecipeUsed();
        tile.markDirty();
    }

    @Nonnull
    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound compound = super.serializeNBT();
        compound.put(NBT_DISPLAY, displayItem.serializeNBT());
        compound.put(NBT_GHOSTINVENTORY, ghostInventory.serializeNBT());
        return compound;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        super.deserializeNBT(nbt);
        if (nbt.hasUniqueId(NBT_DISPLAY)) displayItem = ItemStack.read(nbt.getCompound(NBT_DISPLAY));
        if (nbt.hasUniqueId(NBT_GHOSTINVENTORY)) ghostInventory.deserializeNBT(nbt.getCompound(NBT_GHOSTINVENTORY));
    }

    @Nonnull
    @Override
    public Container createContainer(@Nonnull InventoryPlayer playerInventory, @Nonnull EntityPlayer playerIn) {
        return new ContainerCraftingFrame(ItemUtils.getPlayerInv(playerIn), ghostInventory, playerIn, this);
    }

    @Nonnull
    @Override
    public String getGuiID() {
        return GuiHandler.CRAFTING_FRAME;
    }

    @Override
    public ITextComponent getName() {
        return null;
    }

    @Override
    public boolean hasCustomName() {
        return false;
    }

    @Nullable
    @Override
    public ITextComponent getCustomName() {
        return null;
    }
}
