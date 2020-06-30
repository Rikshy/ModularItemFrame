package de.shyrik.modularitemframe.common.block;

import de.shyrik.modularitemframe.api.*;
import de.shyrik.modularitemframe.api.util.ItemHandlerHelper;
import de.shyrik.modularitemframe.api.util.ItemHelper;
import de.shyrik.modularitemframe.common.module.ModuleEmpty;
import de.shyrik.modularitemframe.common.upgrade.UpgradeBlastResist;
import de.shyrik.modularitemframe.common.upgrade.UpgradeCapacity;
import de.shyrik.modularitemframe.common.upgrade.UpgradeRange;
import de.shyrik.modularitemframe.common.upgrade.UpgradeSpeed;
import de.shyrik.modularitemframe.init.ConfigValues;
import de.shyrik.modularitemframe.init.Registrar;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.*;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class TileModularFrame extends TileEntity implements ITickableTileEntity {

    private static final String NBTMODULE = "framemodule";
    private static final String NBTMODULEDATA = "framemoduledata";
    private static final String NBTUPGRADES = "upgrades";

    public ModuleBase module;
    List<UpgradeBase> upgrades = new ArrayList<>();

    public TileModularFrame() {
         super(Registrar.MODULAR_FRAME_TILE.get());
        setModule(new ModuleEmpty());
    }

    //region <upgrade>
    public boolean tryAddUpgrade(ResourceLocation upgradeLoc) {
        return tryAddUpgrade(upgradeLoc, true);
    }

    public boolean tryAddUpgrade(ResourceLocation upgradeLoc, boolean fireInsert) {
        UpgradeBase up = ItemUpgrade.createUpgrade(upgradeLoc);
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

    public void dropUpgrades(@Nullable PlayerEntity playerIn, @Nonnull Direction facing) {
        for (UpgradeBase up : upgrades) {
            up.onRemove(world, pos, facing);

            ItemStack remain = new ItemStack(up.getParent());
            if (playerIn != null) remain = ItemHandlerHelper.giveStack(ItemHandlerHelper.getPlayerInv(playerIn), remain);
            if (!remain.isEmpty()) ItemHelper.ejectStack(world, pos, facing, remain);
        }

        module.onFrameUpgradesChanged();
        markDirty();
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
    public Direction blockFacing() {
        return world.getBlockState(pos).get(BlockModularFrame.FACING);
    }

    public boolean hasAttachedTile() {
        return getAttachedTile() != null;
    }

    @Nullable
    public TileEntity getAttachedTile() {
        return world.getTileEntity(pos.offset(blockFacing().getOpposite()));
    }

    @Nullable
    public IItemHandler getAttachedInventory() {
        TileEntity neighbor = getAttachedTile();
        if (neighbor != null) {
            return neighbor.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, blockFacing().getOpposite()).orElse(null);
        }

        return null;
    }

    public BlockState getAttachedBlock() {
        return world.getBlockState(getAttachedPos());
    }

    public BlockPos getAttachedPos() {
        return pos.offset(blockFacing());
    }

    public boolean isPowered() { return world.isBlockPowered(pos); }
    //rendregion

    //region <module>
    public void setModule(ResourceLocation moduleLoc) {
        setModule(ItemModule.createModule(moduleLoc));
    }

    private void setModule(ModuleBase mod) {
        module = mod == null ? new ModuleEmpty() : mod;
        module.setTile(this);
    }

    public boolean acceptsModule() {
        return module instanceof ModuleEmpty;
    }

    public void dropModule(@Nonnull Direction facing, @Nullable PlayerEntity playerIn) {
        ItemStack remain = new ItemStack(module.getParent());

        if (playerIn != null) remain = ItemHandlerHelper.giveStack(ItemHandlerHelper.getPlayerInv(playerIn), remain);
        if (!remain.isEmpty()) ItemHelper.ejectStack(world, pos, facing, remain);

        module.onRemove(world, pos, facing, playerIn);
        setModule(new ModuleEmpty());
        markDirty();
    }
    //endregion

    @Override
    public void tick() {
        if (world.getTileEntity(pos) != this || isPowered()) return;
        module.tick(world, pos);
    }

    //region <syncing>
    @Override
    public void markDirty() {
        super.markDirty();
        BlockState state = world.getBlockState(pos);
        world.notifyBlockUpdate(pos, state, state, 1);
        world.notifyBlockUpdate(pos, state, state, 2);
    }

    @Override
    public void handleUpdateTag(@Nonnull CompoundNBT tag) {
        super.handleUpdateTag(tag);
        markDirty();
    }

    @Nonnull
    @Override
    public CompoundNBT getUpdateTag() {
        return write(new CompoundNBT());
    }

    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(getPos(), -1, getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket packet) {
        super.onDataPacket(net, packet);
        read(packet.getNbtCompound());
    }

    @Nonnull
    @Override
    public CompoundNBT write(CompoundNBT compound) {
        compound = super.write(compound);
        compound.putString(NBTMODULE, module.getId().toString());
        compound.put(NBTMODULEDATA, module.serializeNBT());


        ListNBT upgradeList = new ListNBT();
        for (UpgradeBase up : upgrades) {
            upgradeList.add(StringNBT.valueOf(up.getId().toString()));
        }
        compound.put(NBTUPGRADES, upgradeList);
        return compound;
    }

    @Override
    public void read(@Nonnull CompoundNBT cmp) {
        super.read(cmp);
        if (module.getId().toString().equals(cmp.getString(NBTMODULE))) {
            module.deserializeNBT(cmp.getCompound(NBTMODULEDATA));
        } else {
            setModule(new ResourceLocation(cmp.getString(NBTMODULE)));
            module.deserializeNBT(cmp.getCompound(NBTMODULEDATA));
            cmp.remove(NBTMODULEDATA);
        }
        upgrades = new ArrayList<>();
        for (INBT sub : cmp.getList(NBTUPGRADES, 8)) {
            tryAddUpgrade(new ResourceLocation(sub.getString()), false);
        }
    }
    //endregion
}
