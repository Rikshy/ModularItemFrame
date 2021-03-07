package modularitemframe.api.accessors;

import modularitemframe.api.inventory.ItemHandlerWrapper;
import modularitemframe.api.inventory.filter.IItemFilter;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

public interface IFrameTile {

    World getWorld();

    BlockPos getPos();

    IFrameConfig getConfig();

    void markDirty();

    /**
     * @return the current amount of SpeedUpgrade installed in the frame.
     */
    int getSpeedUpCount();

    /**
     * @return the current amount of RangeUpgrade installed in the frame.
     */
    int getRangeUpCount();

    /**
     * @return the current amount of CapacityUpgrade installed in the frame.
     */
    int getCapacityUpCount();

    /**
     * @return true if there is a blast resistance upgrade installed.
     */
    boolean isBlastResist();

    boolean hasInfinity();

    IItemFilter getItemFilter();
    /**
     * @return the current {@link Direction} the frame is facing.
     */
    Direction getFacing();

    /**
     * @return {@link BlockPos} the frame is attached to.
     */
    BlockPos getAttachedPos();


    /**
     * @return {@link BlockState} the frame is attached to.
     */
    BlockState getAttachedBlock();

    /**
     * @return {@link TileEntity} the frame is attached to.
     */
    @Nullable
    TileEntity getAttachedTile();


    /**
     * @return {@link IItemHandler} the frame is attached to, respecting the range upgrades applied to the frame.
     */
    @Nullable
    ItemHandlerWrapper getAttachedInventory();

    /**
     * @return {@link IItemHandler} the frame is attached to.
     *
     * @param range range the inventory should be searched.
     */
    ItemHandlerWrapper getAttachedInventory(int range);

    @Nullable
    IFluidHandler getAttachedTank();

    @Nullable
    IFluidHandler getAttachedTank(int range);

    boolean isPowered();
}
