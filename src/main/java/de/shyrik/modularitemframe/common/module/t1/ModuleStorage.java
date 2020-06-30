package de.shyrik.modularitemframe.common.module.t1;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.shyrik.modularitemframe.ModularItemFrame;
import de.shyrik.modularitemframe.api.ModuleBase;
import de.shyrik.modularitemframe.api.util.FrameItemRenderer;
import de.shyrik.modularitemframe.api.util.ItemHandlerHelper;
import de.shyrik.modularitemframe.api.util.ItemHelper;
import de.shyrik.modularitemframe.client.render.FrameRenderer;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ModuleStorage extends ModuleBase {
    public static final ResourceLocation LOC = new ResourceLocation(ModularItemFrame.MOD_ID, "module_t1_storage");
    public static final ResourceLocation BG_LOC = new ResourceLocation(ModularItemFrame.MOD_ID, "block/module_t1_storage");

    private static final String NBT_LAST = "lastclick";
    private static final String NBT_LASTSTACK = "laststack";
    private static final String NBT_INVENTORY= "inventory";

    private ItemStackHandler inventory = new ItemStackHandler(1);

    private long lastClick;
    private ItemStack lastStack = ItemStack.EMPTY;

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
        return I18n.format("modularitemframe.module.storage");
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void specialRendering(FrameRenderer renderer, @Nonnull MatrixStack matrixStack, float partialTicks, @Nonnull IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {
        FrameItemRenderer.renderOnFrame(lastStack, tile.blockFacing(), 0, 0.1F, TransformType.FIXED, matrixStack, buffer, combinedLight, combinedOverlay);
    }

    @Override
    public void onBlockClicked(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull PlayerEntity playerIn) {
        if (!worldIn.isRemote) {
            IItemHandlerModifiable player = ItemHandlerHelper.getPlayerInv(playerIn);
            int slot = ItemHandlerHelper.getFirstOccupiedSlot(inventory);
            if (slot >= 0) {
                int amount = playerIn.isSneaking() ? inventory.getStackInSlot(slot).getMaxStackSize() : 1;
                ItemStack extract = inventory.extractItem(slot, amount, false);
                extract = ItemHandlerHelper.giveStack(player, extract);
                if (!extract.isEmpty()) ItemHelper.ejectStack(worldIn, pos, tile.blockFacing(), extract);
                tile.markDirty();
            }
        }
    }

    @Override
    public ActionResultType onBlockActivated(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nonnull PlayerEntity playerIn, @Nonnull Hand hand, @Nonnull Direction facing, BlockRayTraceResult hit) {
        if (!worldIn.isRemote) {
            IItemHandlerModifiable player = ItemHandlerHelper.getPlayerInv(playerIn);
            ItemStack held = playerIn.getHeldItem(hand);
            if (lastStack.isEmpty() || ItemStack.areItemsEqual(lastStack, held)) {
                long time = worldIn.getGameTime();

                if (time - lastClick <= 8L && !playerIn.isSneaking() && !lastStack.isEmpty())
                    ItemHandlerHelper.giveAllPossibleStacks(inventory, player, lastStack, held);
                else if (!held.isEmpty()) {
                    ItemStack heldCopy = held.copy();
                    heldCopy.setCount(1);
                    if (ItemHandlerHelper.giveStack(inventory, heldCopy).isEmpty()) {
                        held.shrink(1);

                        lastStack = heldCopy;
                        lastClick = time;
                    }
                }
                tile.markDirty();
            }
        }
        return ActionResultType.SUCCESS;
    }

    @Override
    public void onFrameUpgradesChanged() {
        int newCapacity = (int)Math.pow(2, tile.getCapacityUpCount());
        ItemStackHandler tmp = new ItemStackHandler(newCapacity);
        for (int slot = 0; slot < inventory.getSlots(); slot++) {
            if (slot < tmp.getSlots())
                tmp.insertItem(slot, inventory.getStackInSlot(slot), false);
            else
                ItemHelper.ejectStack(tile.getWorld(), tile.getPos(), tile.blockFacing(), inventory.getStackInSlot(slot));
        }
        tile.markDirty();
    }

    @Override
    public void onRemove(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull Direction facing, @Nullable PlayerEntity playerIn) {
        super.onRemove(worldIn, pos, facing, playerIn);
        for( int slot = 0; slot < inventory.getSlots(); slot++) {
            ItemHelper.ejectStack(worldIn, pos, tile.blockFacing(), inventory.getStackInSlot(slot));
        }
    }

    @Nonnull
    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT cmp = super.serializeNBT();
        cmp.put(NBT_INVENTORY, inventory.serializeNBT());
        cmp.putLong(NBT_LAST, lastClick);
        cmp.put(NBT_LASTSTACK, lastStack.serializeNBT());
        return cmp;
    }

    @Override
    public void deserializeNBT(CompoundNBT cmp) {
        super.deserializeNBT(cmp);
        if (cmp.contains(NBT_INVENTORY)) inventory.deserializeNBT(cmp.getCompound(NBT_INVENTORY));
        if (cmp.contains(NBT_LAST)) lastClick = cmp.getLong(NBT_LAST);
        if (cmp.contains(NBT_LASTSTACK)) lastStack = ItemStack.read(cmp.getCompound(NBT_LASTSTACK));
    }
}
