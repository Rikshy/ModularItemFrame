package de.shyrik.modularitemframe.common.module.t1;

import de.shyrik.modularitemframe.ModularItemFrame;
import de.shyrik.modularitemframe.api.ModuleBase;
import de.shyrik.modularitemframe.api.utils.ItemUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ModuleStorage extends ModuleBase {
    public static final ResourceLocation LOC = new ResourceLocation(ModularItemFrame.MOD_ID, "module_t1_storage");

    private static final String NBT_LAST = "lastclick";
    private static final String NBT_LASTSTACK = "laststack";
    private static final String NBT_INVENTORY= "inventory";

    private ItemStackHandler inventory = new ItemStackHandler(1);

    private long lastClick;
    private ItemStack lastStack = ItemStack.EMPTY;

    @Nonnull
    @Override
    public ResourceLocation frontTexture() {
        return null;
    }

    @Override
    public String getModuleName() {
        return I18n.format("modularitemframe.module.storage");
    }

    @Override
    public void onBlockClicked(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull EntityPlayer playerIn) {
        if (!worldIn.isRemote) {
            IItemHandlerModifiable player = ItemUtils.getPlayerInv(playerIn);
            if (player != null) {
                int slot = ItemUtils.getFirstOccupiedSlot(inventory);
                if (slot >= 0) {
                    int amount = playerIn.isSneaking() ? inventory.getStackInSlot(slot).getMaxStackSize() : 1;
                    ItemStack extract = inventory.extractItem(slot, amount, false);
                    extract = ItemUtils.giveStack(player, extract);
                    if (!extract.isEmpty()) ItemUtils.ejectStack(worldIn, pos, tile.blockFacing(), extract);
                    tile.markDirty();
                }
            }
        }
    }

    @Override
    public boolean onBlockActivated(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull EntityPlayer playerIn, @Nonnull EnumHand hand, @Nonnull EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!worldIn.isRemote) {
            IItemHandlerModifiable player = ItemUtils.getPlayerInv(playerIn);
            if (player != null) {
                ItemStack held = playerIn.getHeldItem(hand);
                if (lastStack.isEmpty() || ItemStack.areItemsEqual(lastStack, held)) {
                    long time = worldIn.getTotalWorldTime();

                    if (time - lastClick <= 8L && !playerIn.isSneaking() && !lastStack.isEmpty())
                        ItemUtils.giveAllPossibleStacks(inventory, player, lastStack);
                    else if (!held.isEmpty()) {
                        ItemStack heldCopy = held.copy();
                        if (playerIn.isSneaking()) held.setCount(ItemUtils.giveStack(inventory, heldCopy).getCount());
                        else {
                            heldCopy.setCount(1);
                            ItemUtils.giveStack(inventory, heldCopy);
                            held.shrink(1);

                            lastStack = heldCopy;
                            lastClick = time;
                        }
                    }
                    tile.markDirty();
                }
            }
        }
        return true;
    }

    @Override
    public void onFrameUpgradesChanged() {
        int capacityUps = tile.getCapacityUpCount();
        int newCapacity = capacityUps > 0 ? 4 * tile.getCapacityUpCount() : 1;
        ItemStackHandler tmp = new ItemStackHandler(newCapacity);
        for (int slot = 0; slot < inventory.getSlots(); slot++) {
            if (slot < tmp.getSlots())
                tmp.insertItem(slot, inventory.getStackInSlot(slot), false);
            else
                ItemUtils.ejectStack(tile.getWorld(), tile.getPos(), tile.blockFacing(), inventory.getStackInSlot(slot));
        }
        tile.markDirty();
    }

    @Override
    public void onRemove(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull EnumFacing facing, @Nullable EntityPlayer playerIn) {
        super.onRemove(worldIn, pos, facing, playerIn);
        for( int slot = 0; slot < inventory.getSlots(); slot++) {
            ItemUtils.ejectStack(worldIn, pos, tile.blockFacing(), inventory.getStackInSlot(slot));
        }
    }

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound compound = super.serializeNBT();
        compound.setLong(NBT_LAST, lastClick);
        compound.setTag(NBT_LASTSTACK, lastStack.serializeNBT());
        compound.setTag(NBT_INVENTORY, inventory.serializeNBT());
        return compound;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        super.deserializeNBT(nbt);
        if (nbt.hasKey(NBT_LAST)) lastClick = nbt.getLong(NBT_LAST);
        if (nbt.hasKey(NBT_LASTSTACK)) lastStack = new ItemStack(nbt.getCompoundTag(NBT_LASTSTACK));
        if (nbt.hasKey(NBT_INVENTORY)) inventory.deserializeNBT(nbt.getCompoundTag(NBT_INVENTORY));
    }
}
