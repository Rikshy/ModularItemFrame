package de.shyrik.modularitemframe.common.module.t2;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.shyrik.modularitemframe.ModularItemFrame;
import de.shyrik.modularitemframe.api.ModuleBase;
import de.shyrik.modularitemframe.client.FrameRenderer;
import de.shyrik.modularitemframe.common.container.crafting.CraftingFrameContainer;
import de.shyrik.modularitemframe.common.container.crafting.IContainerCallbacks;
import de.shyrik.modularitemframe.api.Inventory.ItemHandlerWrapper;
import de.shyrik.modularitemframe.util.InventoryHelper;
import de.shyrik.modularitemframe.util.ItemHelper;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.TextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;
import org.jetbrains.annotations.NotNull;

public class CraftingModule extends ModuleBase implements IContainerCallbacks {
    public static final ResourceLocation ID = new ResourceLocation(ModularItemFrame.MOD_ID, "module_t2_crafting");
    public static final ResourceLocation BG = new ResourceLocation(ModularItemFrame.MOD_ID, "module/module_t2_crafting");
    private static final TextComponent NAME = new TranslationTextComponent("modularitemframe.module.crafting");

    private static final String NBT_GHOST_INVENTORY = "ghost_inventory";
    private static final String NBT_DISPLAY = "display";
    private static final String NBT_MODE = "cp_mode";

    public EnumMode mode = EnumMode.PLAYER;
    protected ICraftingRecipe recipe;
    private ItemStack displayItem = ItemStack.EMPTY;
    private final ItemStackHandler ghostInventory = new ItemStackHandler(9);

    @NotNull
    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @NotNull
    @Override
    public ResourceLocation frontTexture() {
        return BG;
    }

    @NotNull
    @Override
    public TextComponent getName() {
        return NAME;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void specialRendering(@NotNull FrameRenderer renderer, float partialTicks, @NotNull MatrixStack matrixStack, @NotNull IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {
        renderer.renderInside(displayItem, matrixStack, buffer, combinedLight, combinedOverlay);
    }

    @Override
    public void screw(@NotNull World world, @NotNull BlockPos pos, @NotNull PlayerEntity player, ItemStack driver) {
        if (!world.isRemote) {
            if (player.isSneaking()) {
                int modeIdx = mode.getIndex() + 1;
                if (modeIdx == EnumMode.values().length) modeIdx = 0;
                mode = EnumMode.values()[modeIdx];
                player.sendMessage(new TranslationTextComponent("modularitemframe.message.mode_change", mode.getName()), Util.DUMMY_UUID);
            } else {
                player.openContainer(getScreenHandler());
            }

            markDirty();
        }
    }

    @Override
    public ActionResultType onUse(@NotNull World world, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull PlayerEntity player, @NotNull Hand hand, @NotNull Direction facing, BlockRayTraceResult hit) {
        if (!world.isRemote) {
            if (!hasValidRecipe()) {
                player.openContainer(getScreenHandler());
                markDirty();
            } else {
                craft(player, world, player.isSneaking());
            }
        }
        return ActionResultType.SUCCESS;
    }

    private void craft(PlayerEntity player, World world, boolean fullStack) {
        final IItemHandler workingInv = getWorkingInventories(InventoryHelper.getPlayerInv(player));
        reloadRecipe();

        if (workingInv == null || recipe == null || recipe.getRecipeOutput().isEmpty() || !InventoryHelper.canCraft(workingInv, recipe))
            return;

        int craftAmount = fullStack ? Math.min(InventoryHelper.countPossibleCrafts(workingInv, recipe), 64) : 1;
        do {
            ItemStack remain = InventoryHelper.givePlayer(player, recipe.getRecipeOutput());
            if (!remain.isEmpty()) ItemHelper.ejectStack(player.world, frame.getPos(), frame.getFacing(), remain);

            InventoryHelper.removeIngredients(workingInv, recipe);
        } while (--craftAmount > 0);
        world.playSound(null, frame.getPos(), SoundEvents.BLOCK_LADDER_STEP, SoundCategory.BLOCKS, 1F, 1F);
    }

    protected IItemHandler getWorkingInventories(IItemHandler playerInventory) {
        ItemHandlerWrapper neighborInventory = frame.getAttachedInventory();
        switch (mode) {
            case HYBRID:
                if (neighborInventory == null)
                    return playerInventory;
                return new CombinedInvWrapper((IItemHandlerModifiable) playerInventory, (IItemHandlerModifiable) neighborInventory.getHandler());
            case ATTACHED:
                return neighborInventory == null ? null : neighborInventory.getHandler();
            case PLAYER:
                return playerInventory;
        }

        return null;
    }

    protected boolean hasValidRecipe() {
        if (recipe == null) reloadRecipe();
        return recipe != null && !recipe.getRecipeOutput().isEmpty();
    }

    protected void reloadRecipe() {
        recipe = ItemHelper.getRecipe(ghostInventory, frame.getWorld());
        displayItem = recipe != null ? recipe.getRecipeOutput().copy() : ItemStack.EMPTY;
        markDirty();
    }

    @Override
    public void setRecipe(ICraftingRecipe recipe) {
        this.recipe = recipe;
        displayItem = recipe == null ? ItemStack.EMPTY : recipe.getRecipeOutput();
        markDirty();
    }

    @NotNull
    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT tag = super.serializeNBT();
        tag.putInt(NBT_MODE, mode.getIndex());
        tag.put(NBT_DISPLAY, displayItem.serializeNBT());
        tag.put(NBT_GHOST_INVENTORY, ghostInventory.serializeNBT());
        return tag;
    }

    @Override
    public void deserializeNBT(@NotNull CompoundNBT tag) {
        super.deserializeNBT(tag);
        if (tag.contains(NBT_MODE)) mode = EnumMode.values()[tag.getInt(NBT_MODE)];
        if (tag.contains(NBT_DISPLAY)) displayItem = ItemStack.read(tag.getCompound(NBT_DISPLAY));
        if (tag.contains(NBT_GHOST_INVENTORY)) ghostInventory.deserializeNBT(tag.getCompound(NBT_GHOST_INVENTORY));
    }

    public INamedContainerProvider getScreenHandler() {
        return new SimpleNamedContainerProvider((id, playerInventory, player) ->
                new CraftingFrameContainer(
                        id,
                        ghostInventory,
                        player,
                        this),
                new TranslationTextComponent("gui.modularitemframe.crafting.name")
        );
    }

    public enum EnumMode {
        HYBRID(0, "modularitemframe.mode.hybrid_inv"),
        ATTACHED(1, "modularitemframe.mode.attached_inv"),
        PLAYER(2, "modularitemframe.mode.player_inv");

        private final int index;
        private final TextComponent name;

        EnumMode(int indexIn, String nameIn) {
            index = indexIn;
            name = new TranslationTextComponent(nameIn);
        }

        public int getIndex() {
            return this.index;
        }

        public TextComponent getName() {
            return this.name;
        }
    }
}
