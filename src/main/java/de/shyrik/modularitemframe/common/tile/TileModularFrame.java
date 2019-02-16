package de.shyrik.modularitemframe.common.tile;

import de.shyrik.modularitemframe.api.*;
import de.shyrik.modularitemframe.api.utils.ItemUtils;
import de.shyrik.modularitemframe.common.block.BlockModularFrame;
import de.shyrik.modularitemframe.common.item.ItemModule;
import de.shyrik.modularitemframe.common.item.ItemUpgrade;
import de.shyrik.modularitemframe.common.module.ModuleEmpty;
import de.shyrik.modularitemframe.common.network.packet.FrameTileUpdatePacket;
import de.shyrik.modularitemframe.common.upgrade.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class TileModularFrame extends TileEntity implements ITickable {

    private static final String NBTMODULE = "framemodule";
    private static final String NBTMODULEDATA = "framemoduledata";
    private static final String NBTUPGRADES = "upgrades";

    public ModuleBase module;
    private List<UpgradeBase> upgrades = new ArrayList<>();

    public TileModularFrame() {
        setModule(new ModuleEmpty());
    }

    //region <upgrade>
    public boolean tryAddUpgrade(ResourceLocation upgradeLoc) {
        return tryAddUpgrade(upgradeLoc, true);
    }

    public boolean tryAddUpgrade(ResourceLocation upgradeLoc, boolean fireInsert) {
        UpgradeBase up = UpgradeRegistry.createUpgradeInstance(upgradeLoc);
        if (up != null && countUpgradeOfType(up.getClass()) < up.getMaxCount()) {
            upgrades.add(up);
            if (fireInsert) {
                up.onInsert(world, pos, blockFacing());
                module.onFrameUpgradesChanged();
            }
            return true;
        }
        return false;
    }

    public boolean acceptsUpgrade() {
        return upgrades.size() <= ConfigValues.MaxFrameUpgrades;
    }

    public void dropUpgrades(@Nullable EntityPlayer playerIn, @Nonnull EnumFacing facing) {
        for (UpgradeBase up : upgrades) {
            ResourceLocation upId = UpgradeRegistry.getUpgradeId(up.getClass());
            if(upId == null) continue;

            Item item = Item.getByNameOrId(upId.toString());
            if (item instanceof ItemUpgrade) {
                up.onRemove(world, pos, facing);

                ItemStack remain = new ItemStack(item);
                if (playerIn != null) remain = ItemUtils.giveStack(ItemUtils.getPlayerInv(playerIn), remain);
                if (!remain.isEmpty()) ItemUtils.ejectStack(world, pos, facing, remain);
                markDirty();
            }
        }
        module.onFrameUpgradesChanged();
    }

    public int getSpeedUpCount() {
        return countUpgradeOfType(UpgradeSpeed.class);
    }

    public int getRangeUpCount() {
        return countUpgradeOfType(UpgradeRange.class);
    }

    public int getCapacityUpCount() {
        return countUpgradeOfType(UpgradeCapacity.class);
    }

    public boolean isBlastResist() {
        return countUpgradeOfType(UpgradeBlastResist.class) >= 1;
    }

    public int countUpgradeOfType(Class<? extends UpgradeBase> clsUp) {
        int count = 0;
        for (UpgradeBase up : upgrades) {
            if (clsUp.isInstance(up)) count++;
        }
        return count;
    }
    //endregion

    //region <block>
    public EnumFacing blockFacing() {
        return world.getBlockState(pos).getValue(BlockModularFrame.FACING);
    }

    public boolean hasAttachedTile() {
        return getAttachedTile() != null;
    }

    @Nullable
    public TileEntity getAttachedTile() {
        return world.getTileEntity(pos.offset(blockFacing()));
    }

    @Nullable
    public IItemHandler getAttachedInventory() {
        TileEntity neighbor = getAttachedTile();
        if (neighbor != null)
            return neighbor.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, blockFacing().getOpposite());

        return null;
    }

    public IBlockState getAttachedBlock() {
        return world.getBlockState(getAttachedPos());
    }

    public BlockPos getAttachedPos() {
        return pos.offset(blockFacing());
    }

    public boolean isPowered() { return world.isBlockPowered(pos); }
    //rendregion

    //region <module>
    public void setModule(ResourceLocation moduleLoc) {
        setModule(ModuleRegistry.createModuleInstance(moduleLoc));
    }

    private void setModule(ModuleBase mod) {
        module = mod == null ? new ModuleEmpty() : mod;
        module.setTile(this);
    }

    public boolean acceptsModule() {
        return module instanceof ModuleEmpty;
    }

    public void dropModule(@Nonnull EnumFacing facing, @Nullable EntityPlayer playerIn) {
        ResourceLocation moduleLoc = ModuleRegistry.getModuleId(module.getClass());
        if (moduleLoc == null) return;

        Item item = Item.getByNameOrId(moduleLoc.toString());
        if (item instanceof ItemModule) {
            ItemStack remain = new ItemStack(item);

            if (playerIn != null) remain = ItemUtils.giveStack(ItemUtils.getPlayerInv(playerIn), remain);
            if (!remain.isEmpty()) ItemUtils.ejectStack(world, pos, facing, remain);

            module.onRemove(world, pos, facing, playerIn);
            setModule(new ModuleEmpty());
            markDirty();
        }
    }
    //endregion

    @Override
    public void update() {
        if (world.getTileEntity(pos) != this || isPowered()) return;
        module.tick(world, pos);
    }

    //region <syncing>
    @Override
    public void markDirty() {
        IBlockState state = world.getBlockState(pos);
        world.markBlockRangeForRenderUpdate(pos, pos);
        world.notifyBlockUpdate(pos, state, state, 3);
        world.scheduleBlockUpdate(pos, blockType, 0, 0);
        super.markDirty();
    }

    @Override
    public void handleUpdateTag(@Nonnull NBTTagCompound tag) {
        super.handleUpdateTag(tag);
        markDirty();
    }

    @Nonnull
    @Override
    public NBTTagCompound getUpdateTag() {
        return writeToNBT(new NBTTagCompound());
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        NBTTagCompound nbt = writeToNBT(new NBTTagCompound());
        NBTTagCompound customNBT = module.writeUpdateNBT(new NBTTagCompound());
        return new FrameTileUpdatePacket(getPos(), -999, nbt, customNBT);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
        super.onDataPacket(net, packet);
        module.readUpdateNBT(((FrameTileUpdatePacket) packet).getCustomTag());
        readFromNBT(packet.getNbtCompound());
    }

    @Nonnull
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound = super.writeToNBT(compound);
        ResourceLocation moduleLoc = ModuleRegistry.getModuleId(module.getClass());
        compound.setString(NBTMODULE, moduleLoc != null ? moduleLoc.toString() : "");
        compound.setTag(NBTMODULEDATA, module.serializeNBT());

        NBTTagList upgradeList = new NBTTagList();
        for ( UpgradeBase up : upgrades) {
            ResourceLocation upLoc = UpgradeRegistry.getUpgradeId(up.getClass());
            if(upLoc != null)
                upgradeList.appendTag(new NBTTagString(upLoc.toString()));
        }
        compound.setTag(NBTUPGRADES, upgradeList);
        return compound;
    }

    @Override
    public void readFromNBT(@Nonnull NBTTagCompound cmp) {
        super.readFromNBT(cmp);
        ResourceLocation moduleLoc = ModuleRegistry.getModuleId(module.getClass());
        if (moduleLoc != null && moduleLoc.toString().equals(cmp.getString(NBTMODULE))) {
            module.deserializeNBT(cmp.getCompoundTag(NBTMODULEDATA));
        } else {
            setModule(new ResourceLocation(cmp.getString(NBTMODULE)));
            module.deserializeNBT(cmp.getCompoundTag(NBTMODULEDATA));
            cmp.removeTag(NBTMODULEDATA);
        }
        upgrades = new ArrayList<>();
        for (NBTBase sub : cmp.getTagList(NBTUPGRADES, 8)) {
            tryAddUpgrade(new ResourceLocation(((NBTTagString)sub).getString()), false);
        }
    }
    //endregion
}
