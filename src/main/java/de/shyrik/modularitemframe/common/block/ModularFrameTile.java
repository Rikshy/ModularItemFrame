package de.shyrik.modularitemframe.common.block;

import de.shyrik.modularitemframe.ModularItemFrame;
import de.shyrik.modularitemframe.api.*;
import de.shyrik.modularitemframe.common.module.EmptyModule;
import de.shyrik.modularitemframe.common.upgrade.*;
import de.shyrik.modularitemframe.init.Blocks;
import de.shyrik.modularitemframe.api.Inventory.filter.AggregatedItemFilter;
import de.shyrik.modularitemframe.api.Inventory.filter.DefaultFilter;
import de.shyrik.modularitemframe.api.Inventory.filter.IItemFilter;
import de.shyrik.modularitemframe.api.Inventory.ItemHandlerWrapper;
import de.shyrik.modularitemframe.util.InventoryHelper;
import de.shyrik.modularitemframe.util.ItemHelper;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class ModularFrameTile extends TileEntity implements ITickableTileEntity {

    private static final String NBT_MODULE = "frame_module";
    private static final String NBT_MODULE_DATA = "frame_module_data";
    private static final String NBT_UPGRADES = "upgrades";
    private static final String NBT_UPGRADE_ID = "upgrade_id";
    private static final String NBT_UPGRADE_DATA = "upgrade_data";

    ModuleBase module;
    List<UpgradeBase> upgrades = new ArrayList<>();

    public ModularFrameTile() {
        super(Blocks.MODULAR_FRAME_TILE_TYPE.get());
        setModule(new EmptyModule(), null, ItemStack.EMPTY);
    }

    //region <upgrade>
    public boolean tryAddUpgrade(@NotNull UpgradeBase up, @Nullable PlayerEntity player, ItemStack upStack) {
        if (countUpgradeOfType(up.getClass()) < up.getMaxCount()) {
            upgrades.add(up);
            if (!upStack.isEmpty() && player != null) {
                Direction facing = getFacing();
                up.onInsert(world, pos, facing, player, upStack);
                module.onFrameUpgradesChanged(world, pos, facing);
            }
            return true;
        }
        return false;
    }

    public boolean tryAddUpgrade(@NotNull UpgradeBase up) {
        return tryAddUpgrade(up, null, ItemStack.EMPTY);
    }

    public Iterable<UpgradeBase> getUpgrades() {
        return upgrades;
    }

    public Map<TextComponent, List<UpgradeBase>> getUpgradesByType() {
        return upgrades.stream().collect(Collectors.groupingBy(UpgradeBase::getName));
    }

    public int getUpgradeCount() {
        return upgrades.size();
    }

    public boolean acceptsUpgrade() {
        return upgrades.size() <= ModularItemFrame.config.maxFrameUpgrades.get();
    }

    public void dropUpgrades(@Nullable PlayerEntity player, @NotNull Direction facing) {
        for (UpgradeBase up : upgrades) {
            ItemStack remain = new ItemStack(up.getItem());

            up.onRemove(world, pos, facing, player, remain);

            if (player != null) remain = InventoryHelper.givePlayer(player, remain);
            if (!remain.isEmpty()) ItemHelper.ejectStack(world, pos, facing, remain);
        }
        upgrades.clear();

        module.onFrameUpgradesChanged(world, pos, facing);
        markDirty();
    }

    /**
     * @return the current amount of {@link SpeedUpgrade} installed in the frame.
     */
    public int getSpeedUpCount() {
        return countUpgradeOfType(SpeedUpgrade.class);
    }

    /**
     * @return the current amount of {@link RangeUpgrade} installed in the frame.
     */
    public int getRangeUpCount() {
        return countUpgradeOfType(RangeUpgrade.class);
    }

    /**
     * @return the current amount of {@link CapacityUpgrade} installed in the frame.
     */
    public int getCapacityUpCount() {
        return countUpgradeOfType(CapacityUpgrade.class);
    }

    /**
     * @return true if there is a blast resistance upgrade installed.
     */
    public boolean isBlastResist() {
        return countUpgradeOfType(BlastResistUpgrade.class) >= 1;
    }

    /**
     * @return true if there is an infinity upgrade installed.
     */
    public boolean hasInfinity() {
        return countUpgradeOfType(InfinityUpgrade.class) >= 1;
    }

    /**
     * @return true if there is the has access to the frame.
     */
    public boolean hasAccess(@NotNull PlayerEntity player) {
        if (player.isCreative() || countUpgradeOfType(SecurityUpgrade.class) < 1)
            return true;

        return upgrades.stream().anyMatch(up -> up instanceof SecurityUpgrade && ((SecurityUpgrade) up).hasAccess(player));
    }
    public IItemFilter getItemFilter() {
        IItemFilter[] filters = upgrades
                .stream()
                .filter(up -> up instanceof FilterUpgrade)
                .map(up -> ((FilterUpgrade)up).getFilter())
                .toArray(IItemFilter[]::new);

        return filters.length == 0 ? DefaultFilter.ANYTHING : AggregatedItemFilter.anyOf(filters);
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
    /**
     * @return the current {@link Direction} the frame is facing.
     */
    public Direction getFacing() {
        assert world != null;
        return world.getBlockState(pos).get(ModularFrameBlock.FACING);
    }

    /**
     * @return {@link BlockPos} the frame is attached to.
     */
    public BlockPos getAttachedPos() {
        return pos.offset(getFacing().getOpposite());
    }


    /**
     * @return {@link BlockState} the frame is attached to.
     */
    public BlockState getAttachedBlock() {
        assert world != null;
        return world.getBlockState(getAttachedPos());
    }

    /**
     * @return {@link TileEntity} the frame is attached to.
     */
    @Nullable
    public TileEntity getAttachedTile() {
        assert world != null;
        return world.getTileEntity(getAttachedPos());
    }


    /**
     * @return {@link IItemHandler} the frame is attached to, respecting the range upgrades applied to the frame.
     */
    @Nullable
    public ItemHandlerWrapper getAttachedInventory() {
        return getAttachedInventory(getRangeUpCount());
    }

    /**
     * @return {@link IItemHandler} the frame is attached to.
     *
     * @param range range the inventory should be searched.
     */
    public ItemHandlerWrapper getAttachedInventory(int range) {
        assert world != null;
        Direction facing = getFacing().getOpposite();
        for (int i = 0; i <= range; i++) {
            TileEntity neighbor = world.getTileEntity(getAttachedPos().offset(facing, i));
            if (neighbor != null) {
                Optional<IItemHandler> maybeInv = neighbor.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing).resolve();
                if (maybeInv.isPresent())
                    return new ItemHandlerWrapper(maybeInv.get(), neighbor);
            }
        }
        return null;
    }

    @Nullable
    public IFluidHandler getAttachedTank() {
        return getAttachedTank(getRangeUpCount());
    }

    @Nullable
    public IFluidHandler getAttachedTank(int range) {
        assert world != null;
        Direction facing = getFacing().getOpposite();
        for (int i = 0; i <= range; i++) {
            TileEntity neighbor = world.getTileEntity(pos.offset(facing, i));
            if (neighbor != null) {
                Optional<IFluidHandler> maybeTank = neighbor.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, facing).resolve();
                if (maybeTank.isPresent())
                    return maybeTank.get();
            }
        }
        return null;
    }

    public boolean isPowered() {
        assert world != null;
        return world.isBlockPowered(pos); }
    //endregion

    //region <module>
    public void setModule(ModuleBase mod, PlayerEntity player, ItemStack moduleStack) {
        module = mod == null ? new EmptyModule() : mod;
        module.setFrame(this);
        if (!moduleStack.isEmpty() && player != null)
            module.onInsert(world, pos, getFacing(), player, moduleStack);
    }

    public ModuleBase getModule() {
        return module;
    }

    public boolean acceptsModule() {
        return module instanceof EmptyModule;
    }

    public void dropModule(@Nullable PlayerEntity player, @NotNull Direction facing) {
        ItemStack remain = new ItemStack(module.getItem());

        if (player != null) remain = InventoryHelper.givePlayer(player, remain);
        if (!remain.isEmpty()) ItemHelper.ejectStack(world, pos, facing, remain);

        module.onRemove(world, pos, facing, player, remain);
        setModule(new EmptyModule(), null, ItemStack.EMPTY);
        markDirty();
    }
    //endregion

    @Override
    public void tick() {
        assert world != null;
        if (world.getTileEntity(pos) != this || isPowered()) return;
        module.tick(world, pos);
    }

    //region <syncing>
    @Override
    public void markDirty() {
        super.markDirty();
        assert world != null;
        BlockState state = world.getBlockState(pos);
        world.markBlockRangeForRenderUpdate(pos, state, state);
        world.notifyBlockUpdate(pos, state, state, 3);
    }

    @Override
    public void handleUpdateTag(BlockState state, CompoundNBT tag) {
        super.handleUpdateTag(state, tag);
        markDirty();
    }

    @NotNull
    @Override
    public CompoundNBT getUpdateTag() {
        return write(new CompoundNBT());
    }

    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        CompoundNBT nbt = write(new CompoundNBT());
        return new SUpdateTileEntityPacket(getPos(), -999, nbt);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket packet) {
        super.onDataPacket(net, packet);
        read(null, packet.getNbtCompound());
    }

    @Override
    public @NotNull CompoundNBT write(@NotNull CompoundNBT compound) {
        super.write(compound);
        compound.putString(NBT_MODULE, module.getId().toString());
        compound.put(NBT_MODULE_DATA, module.serializeNBT());

        ListNBT upgradeList = new ListNBT();
        for (UpgradeBase up : upgrades) {
            CompoundNBT upTag = new CompoundNBT();
            upTag.putString(NBT_UPGRADE_ID, up.getId().toString());
            upTag.put(NBT_UPGRADE_DATA, up.serializeNBT());
            upgradeList.add(upTag);
        }
        compound.put(NBT_UPGRADES, upgradeList);
        return compound;
    }

    @Override
    public void read(@NotNull BlockState state, @NotNull CompoundNBT nbt) {
        super.read(state, nbt);
        upgrades = new ArrayList<>();
        for (INBT sub : nbt.getList(NBT_UPGRADES, 10)) {
            UpgradeBase up = UpgradeItem.createUpgrade(new ResourceLocation(((CompoundNBT)sub).getString(NBT_UPGRADE_ID)));
            assert up != null;
            up.deserializeNBT(((CompoundNBT)sub).getCompound(NBT_UPGRADE_DATA));
            tryAddUpgrade(up);
        }
        if (module.getId().toString().equals(nbt.getString(NBT_MODULE))) {
            module.deserializeNBT(nbt.getCompound(NBT_MODULE_DATA));
        } else {
            setModule(ModuleItem.createModule(new ResourceLocation(nbt.getString(NBT_MODULE))), null, ItemStack.EMPTY);
            module.deserializeNBT(nbt.getCompound(NBT_MODULE_DATA));
            nbt.remove(NBT_MODULE_DATA);
        }
    }
    //endregion
}
