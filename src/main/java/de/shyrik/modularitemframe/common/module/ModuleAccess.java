package de.shyrik.modularitemframe.common.module;

import de.shyrik.modularitemframe.api.ModuleFrameBase;
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

public class ModuleAccess extends ModuleFrameBase {

    private static final String NBT_LAST = "lastclick";

    private long lastClick;

    @Nonnull
    @Override
    public ResourceLocation frontTexture() {
        return null;
    }

    @Override
    public String getModuleName() {
        return I18n.format("modularitemframe.module.access");
    }

    @Override
    public void onBlockClicked(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull EntityPlayer playerIn) {
        EnumFacing blockFacing = tile.blockFacing();
        TileEntity neighbor = tile.getNeighbor(blockFacing);
        if(neighbor != null) {
            IItemHandlerModifiable handler = (IItemHandlerModifiable) neighbor.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, blockFacing);
            IItemHandlerModifiable player = ItemUtils.getPlayerInv(playerIn);
            if (handler != null && player != null) {
                int slot = ItemUtils.getLastUnOccupiedSlot(handler);
                if (slot >= 0) {
                    int amount = playerIn.isSneaking() ? handler.getStackInSlot(slot).getMaxStackSize() : 1;
                    ItemStack extract = handler.extractItem(slot, amount, false);
                    extract = ItemUtils.giveStack(player, extract);
                    if (!extract.isEmpty())
                        ItemUtils.ejectStack(worldIn, pos, blockFacing, extract);
                }
            }
        }
    }

    @Override
    public boolean onBlockActivated(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull EntityPlayer playerIn, @Nonnull EnumHand hand, @Nonnull EnumFacing facing, float hitX, float hitY, float hitZ) {
        EnumFacing blockFacing = tile.blockFacing();
        TileEntity neighbor = tile.getNeighbor(blockFacing);
        if(neighbor != null) {
            IItemHandlerModifiable handler = (IItemHandlerModifiable) neighbor.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, blockFacing);
            IItemHandlerModifiable player = ItemUtils.getPlayerInv(playerIn);
            if (handler != null && player != null) {
                ItemStack held = playerIn.getHeldItem(hand);
                if (!held.isEmpty()) {
                    long time = worldIn.getTotalWorldTime();

                    if (time - lastClick <= 8L) ItemUtils.giveAllPossibleStacks(handler, player, held);
                    else ItemUtils.giveStack(handler, held);
                    lastClick = time;
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound compound = super.serializeNBT();
        compound.setLong(NBT_LAST, lastClick);
        return compound;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        super.deserializeNBT(nbt);
        if (nbt.hasKey(NBT_LAST)) lastClick = nbt.getLong(NBT_LAST);
    }
}
