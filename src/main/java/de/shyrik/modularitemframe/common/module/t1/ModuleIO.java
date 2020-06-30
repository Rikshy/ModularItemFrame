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
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nonnull;

public class ModuleIO extends ModuleBase {

    public static final ResourceLocation LOC = new ResourceLocation(ModularItemFrame.MOD_ID, "module_t1_io");
    public static final ResourceLocation BG_LOC = new ResourceLocation(ModularItemFrame.MOD_ID, "block/module_t1_io");

    private static final String NBT_LAST = "lastclick";
    private static final String NBT_LASTSTACK = "laststack";
    private static final String NBT_DISPLAY = "display";

    private long lastClick;
    private ItemStack displayItem = ItemStack.EMPTY;
    private ItemStack lastStack = ItemStack.EMPTY;

    @Override
    public ResourceLocation getId() {
        return LOC;
    }

    @Nonnull
    @Override
    @OnlyIn(Dist.CLIENT)
    public ResourceLocation frontTexture() {
        return BG_LOC;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void specialRendering(FrameRenderer tesr, @Nonnull MatrixStack matrixStack, float partialTicks, @Nonnull IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {
        FrameItemRenderer.renderOnFrame(displayItem, tile.blockFacing(), 0F, 0.1F, TransformType.FIXED, matrixStack, buffer, combinedLight, combinedOverlay);
    }

    @Override
    public String getModuleName() {
        return I18n.format("modularitemframe.module.io");
    }

    @Override
    public void onBlockClicked(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull PlayerEntity playerIn) {
        if (!worldIn.isRemote) {
            IItemHandlerModifiable handler = (IItemHandlerModifiable)tile.getAttachedInventory();
            if (handler != null) {
                Direction blockFacing = tile.blockFacing();
                IItemHandlerModifiable player = ItemHandlerHelper.getPlayerInv(playerIn);

                int slot = ItemHandlerHelper.getFirstOccupiedSlot(handler);
                if (slot >= 0) {
                    int amount = playerIn.isSneaking() ? handler.getStackInSlot(slot).getMaxStackSize() : 1;
                    ItemStack extract = handler.extractItem(slot, amount, false);
                    extract = ItemHandlerHelper.giveStack(player, extract);
                    if (!extract.isEmpty()) ItemHelper.ejectStack(worldIn, pos, blockFacing, extract);
                    tile.getAttachedTile().markDirty();
                    tile.markDirty();
                }
            }
        }
    }

    @Override
    public ActionResultType onBlockActivated(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nonnull PlayerEntity playerIn, @Nonnull Hand hand, @Nonnull Direction facing, BlockRayTraceResult hit) {
        if (!worldIn.isRemote) {
            IItemHandlerModifiable handler = (IItemHandlerModifiable)tile.getAttachedInventory();
            if (handler != null) {
                IItemHandlerModifiable player = ItemHandlerHelper.getPlayerInv(playerIn);
                ItemStack held = playerIn.getHeldItem(hand);
                long time = worldIn.getGameTime();

                if (time - lastClick <= 8L && !playerIn.isSneaking() && !lastStack.isEmpty())
                    ItemHandlerHelper.giveAllPossibleStacks(handler, player, lastStack, held);
                else if (!held.isEmpty()) {
                    ItemStack heldCopy = held.copy();
                    heldCopy.setCount(1);
                    if (ItemHandlerHelper.giveStack(handler, heldCopy).isEmpty()){
                        held.shrink(1);

                        lastStack = heldCopy;
                        lastClick = time;
                    }
                }
                tile.markDirty();
                return ActionResultType.SUCCESS;
            }
        }
        return ActionResultType.SUCCESS;
    }

    @Override
    public void tick(@Nonnull World world, @Nonnull BlockPos pos) {
        if(!world.isRemote) {
            IItemHandler handler = tile.getAttachedInventory();
            if (handler != null) {
                int slot = ItemHandlerHelper.getFirstOccupiedSlot(handler);
                if (slot >= 0) {
                    ItemStack slotStack = handler.getStackInSlot(slot);
                    if (!ItemStack.areItemsEqual(slotStack, displayItem)) {
                        ItemStack copy = slotStack.copy();
                        copy.setCount(1);
                        displayItem = copy;
                        tile.markDirty();
                    }
                } else {
                    displayItem = ItemStack.EMPTY;
                    tile.markDirty();
                }
            }
        }
    }

    @Nonnull
    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT cmp = super.serializeNBT();
        cmp.putLong(NBT_LAST, lastClick);
        cmp.put(NBT_LASTSTACK, lastStack.serializeNBT());
        cmp.put(NBT_DISPLAY, displayItem.serializeNBT());
        return cmp;
    }

    @Override
    public void deserializeNBT(@Nonnull CompoundNBT cmp) {
        if (cmp.contains(NBT_LAST)) lastClick = cmp.getLong(NBT_LAST);
        if (cmp.contains(NBT_LASTSTACK)) lastStack = ItemStack.read(cmp.getCompound(NBT_LASTSTACK));
        if (cmp.contains(NBT_DISPLAY)) displayItem = ItemStack.read(cmp.getCompound(NBT_DISPLAY));
    }
}
