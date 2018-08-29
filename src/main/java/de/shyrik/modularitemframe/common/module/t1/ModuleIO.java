package de.shyrik.modularitemframe.common.module.t1;

import de.shyrik.modularitemframe.ModularItemFrame;
import de.shyrik.modularitemframe.api.ModuleBase;
import de.shyrik.modularitemframe.api.utils.ItemUtils;
import net.minecraft.block.state.IBlockState;
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
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nonnull;

public class ModuleIO extends ModuleBase {

    private static final String NBT_LAST = "lastclick";
    private static final String NBT_LASTSTACK = "laststack";

    private long lastClick;
    private ItemStack lastStack = ItemStack.EMPTY;

    @Nonnull
    @Override
    public ResourceLocation frontTexture() {
        return new ResourceLocation(ModularItemFrame.MOD_ID, "blocks/io");
    }

    @Override
    public String getModuleName() {
        return I18n.format("modularitemframe.module.io");
    }

    @Override
    public void onBlockClicked(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull EntityPlayer playerIn) {
        if (!worldIn.isRemote) {
            EnumFacing blockFacing = tile.blockFacing();
            TileEntity neighbor = tile.getNeighbor(blockFacing);
            if (neighbor != null) {
                IItemHandlerModifiable handler = (IItemHandlerModifiable) neighbor.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, blockFacing);
                IItemHandlerModifiable player = ItemUtils.getPlayerInv(playerIn);
                if (handler != null && player != null) {
                    int slot = ItemUtils.getFirstOccupiedSlot(handler);
                    if (slot >= 0) {
                        int amount = playerIn.isSneaking() ? handler.getStackInSlot(slot).getMaxStackSize() : 1;
                        ItemStack extract = handler.extractItem(slot, amount, false);
                        extract = ItemUtils.giveStack(player, extract);
                        if (!extract.isEmpty()) ItemUtils.ejectStack(worldIn, pos, blockFacing, extract);
                    }
                }
            }
        }
    }

    @Override
    public boolean onBlockActivated(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull EntityPlayer playerIn, @Nonnull EnumHand hand, @Nonnull EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!worldIn.isRemote) {
            EnumFacing blockFacing = tile.blockFacing();
            TileEntity neighbor = tile.getNeighbor(blockFacing);
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
    public NBTTagCompound serializeNBT() {
        NBTTagCompound compound = super.serializeNBT();
        compound.setLong(NBT_LAST, lastClick);
        compound.setTag(NBT_LASTSTACK, lastStack.serializeNBT());
        return compound;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        super.deserializeNBT(nbt);
        if (nbt.hasKey(NBT_LAST)) lastClick = nbt.getLong(NBT_LAST);
        if (nbt.hasKey(NBT_LASTSTACK)) lastStack = new ItemStack(nbt.getCompoundTag(NBT_LASTSTACK));
    }
}
