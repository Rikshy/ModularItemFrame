package de.shyrik.modularitemframe.common.module.t1;

import de.shyrik.modularitemframe.ModularItemFrame;
import de.shyrik.modularitemframe.api.ModuleBase;
import de.shyrik.modularitemframe.api.utils.ItemUtils;
import de.shyrik.modularitemframe.api.utils.RenderUtils;
import de.shyrik.modularitemframe.client.render.FrameRenderer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nonnull;

public class ModuleIO extends ModuleBase {

    public static final ResourceLocation LOC = new ResourceLocation(ModularItemFrame.MOD_ID, "module_t1_io");
    public static final ResourceLocation BG_LOC = new ResourceLocation(ModularItemFrame.MOD_ID, "blocks/io");

    private static final String NBT_LAST = "lastclick";
    private static final String NBT_LASTSTACK = "laststack";
    private static final String NBT_DISPLAY = "display";

    private long lastClick;
    private ItemStack displayItem = ItemStack.EMPTY;
    private ItemStack lastStack = ItemStack.EMPTY;

    @Nonnull
    @Override
    @SideOnly(Side.CLIENT)
    public ResourceLocation frontTexture() {
        return BG_LOC;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void specialRendering(FrameRenderer tesr, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5D, y + 0.5D, z + 0.5D);
        GlStateManager.pushMatrix();

        RenderUtils.renderItem(displayItem, tile.blockFacing(), 0F, 0.05F);

        GlStateManager.popMatrix();
        GlStateManager.popMatrix();
    }

    @Override
    public String getModuleName() {
        return I18n.format("modularitemframe.module.io");
    }

    @Override
    public void onBlockClicked(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull EntityPlayer playerIn) {
        if (!worldIn.isRemote) {
            TileEntity neighbor = tile.getAttachedTile();
            if (neighbor != null) {
                EnumFacing blockFacing = tile.blockFacing();
                IItemHandlerModifiable handler = (IItemHandlerModifiable) neighbor.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, blockFacing);
                IItemHandlerModifiable player = ItemUtils.getPlayerInv(playerIn);
                if (handler != null && player != null) {
                    int slot = ItemUtils.getFirstOccupiedSlot(handler);
                    if (slot >= 0) {
                        int amount = playerIn.isSneaking() ? handler.getStackInSlot(slot).getMaxStackSize() : 1;
                        ItemStack extract = handler.extractItem(slot, amount, false);
                        extract = ItemUtils.giveStack(player, extract);
                        if (!extract.isEmpty()) ItemUtils.ejectStack(worldIn, pos, blockFacing, extract);
                        neighbor.markDirty();
                        tile.markDirty();
                    }
                }
            }
        }
    }

    @Override
    public boolean onBlockActivated(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull EntityPlayer playerIn, @Nonnull EnumHand hand, @Nonnull EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!worldIn.isRemote) {
            EnumFacing blockFacing = tile.blockFacing();
            TileEntity neighbor = tile.getAttachedTile();
            if (neighbor != null) {
                IItemHandlerModifiable handler = (IItemHandlerModifiable) neighbor.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, blockFacing);
                IItemHandlerModifiable player = ItemUtils.getPlayerInv(playerIn);
                if (handler != null && player != null) {
                    ItemStack held = playerIn.getHeldItem(hand);
                    long time = worldIn.getTotalWorldTime();

                    if (time - lastClick <= 8L && !playerIn.isSneaking() && !lastStack.isEmpty())
                        ItemUtils.giveAllPossibleStacks(handler, player, lastStack);
                    else if (!held.isEmpty()) {
                        ItemStack heldCopy = held.copy();
                        if (playerIn.isSneaking()) held.setCount(ItemUtils.giveStack(handler, heldCopy).getCount());
                        else {
                            heldCopy.setCount(1);
                            ItemUtils.giveStack(handler, heldCopy);
                            held.shrink(1);

                            lastStack = heldCopy;
                            lastClick = time;
                        }
                    }
                    tile.markDirty();
                    return true;
                }
            }
        }
        return true;
    }

    @Override
    public void tick(@Nonnull World world, @Nonnull BlockPos pos) {
        if(!world.isRemote) {
            TileEntity neighbor = tile.getAttachedTile();
            if (neighbor != null) {
                EnumFacing blockFacing = tile.blockFacing();
                IItemHandler handler = neighbor.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, blockFacing);
                if (handler != null) {
                    int slot = ItemUtils.getFirstOccupiedSlot(handler);
                    if (slot >= 0) {
                        ItemStack slotStack = handler.getStackInSlot(slot);
                        if (!ItemStack.areItemsEqual(slotStack, displayItem)) {
                            ItemStack copy = slotStack.copy();
                            copy.setCount(1);
                            displayItem = copy;
                            tile.markDirty();
                        }
                    }
                }
            }
        }
    }

    @Nonnull
    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound compound = super.serializeNBT();
        compound.setLong(NBT_LAST, lastClick);
        compound.setTag(NBT_LASTSTACK, lastStack.serializeNBT());
        compound.setTag(NBT_DISPLAY, displayItem.serializeNBT());
        return compound;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        super.deserializeNBT(nbt);
        if (nbt.hasKey(NBT_LAST)) lastClick = nbt.getLong(NBT_LAST);
        if (nbt.hasKey(NBT_LASTSTACK)) lastStack = new ItemStack(nbt.getCompoundTag(NBT_LASTSTACK));
        if (nbt.hasKey(NBT_DISPLAY)) displayItem = new ItemStack(nbt.getCompoundTag(NBT_DISPLAY));
    }
}
