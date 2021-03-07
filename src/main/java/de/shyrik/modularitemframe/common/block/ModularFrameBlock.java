package de.shyrik.modularitemframe.common.block;

import de.shyrik.modularitemframe.ModularItemFrame;
import de.shyrik.modularitemframe.common.item.ScrewdriverItem;
import de.shyrik.modularitemframe.common.module.EmptyModule;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.IProbeInfoAccessor;
import mcjty.theoneprobe.api.ProbeMode;
import modularitemframe.api.ModuleItem;
import modularitemframe.api.UpgradeBase;
import modularitemframe.api.UpgradeItem;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameters;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber
public class ModularFrameBlock extends Block implements IProbeInfoAccessor {

    public static final DirectionProperty FACING = DirectionProperty.create("facing", Direction.values());
    public static final AbstractBlock.Properties DEFAULT_SETTINGS = AbstractBlock.Properties
            .create(Material.WOOD)
            .sound(SoundType.WOOD)
            .hardnessAndResistance(4)
            .notSolid();

    private static final VoxelShape UP_SHAPE = Block.makeCuboidShape(2, 0, 2, 14, 2, 14);
    private static final VoxelShape DOWN_SHAPE = Block.makeCuboidShape(2, 14, 2, 14, 16, 14);
    private static final VoxelShape NORTH_SHAPE = Block.makeCuboidShape(14, 2, 16, 2, 14, 14);
    private static final VoxelShape SOUTH_SHAPE = Block.makeCuboidShape(2, 2, 0, 14, 14, 2);
    private static final VoxelShape EAST_SHAPE = Block.makeCuboidShape(0, 2, 2, 2, 14, 14);
    private static final VoxelShape WEST_SHAPE = Block.makeCuboidShape(16, 2, 2, 14, 14, 14);

    public static final ResourceLocation INNER_DEF = new ResourceLocation(ModularItemFrame.MOD_ID, "block/default_inner");
    public static final ResourceLocation INNER_HARD = new ResourceLocation(ModularItemFrame.MOD_ID, "block/hard_inner");
    public static final ResourceLocation INNER_HARDEST = new ResourceLocation(ModularItemFrame.MOD_ID, "block/hardest_inner");

    public ModularFrameBlock(AbstractBlock.Properties props) {
        super(props);

        setDefaultState(getDefaultState().with(FACING, Direction.NORTH));
    }

    @Override
    protected void fillStateContainer(StateContainer.@NotNull Builder<Block, BlockState> builder) {
        super.fillStateContainer(builder);
        builder.add(FACING);
    }

    @Override
    @SuppressWarnings("deprecation")
    public @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull IBlockReader world, @NotNull BlockPos pos, @NotNull ISelectionContext context) {
        switch (state.get(FACING)) {
            case UP:
                return UP_SHAPE;
            case DOWN:
                return DOWN_SHAPE;
            case NORTH:
                return NORTH_SHAPE;
            case SOUTH:
                return SOUTH_SHAPE;
            case EAST:
                return EAST_SHAPE;
            case WEST:
                return WEST_SHAPE;
        }

        return super.getShape(state, world, pos, context);
    }

    //region <tileentity>
    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(@NotNull BlockState state, IBlockReader world) {
        return new ModularFrameTile();
    }

    private ModularFrameTile getTE(@NotNull World world, @NotNull BlockPos pos) {
        return (ModularFrameTile) world.getTileEntity(pos);
    }
    //endregion

    //region <interaction>
    @Override
    @SuppressWarnings("deprecation")
    public void onBlockClicked(@NotNull BlockState state, @NotNull World world, @NotNull BlockPos pos, @NotNull PlayerEntity player) {
        getTE(world, pos).module.onBlockClicked(world, pos, player);
    }

    @Override
    @SuppressWarnings("deprecation")
    public @NotNull ActionResultType onBlockActivated(@NotNull BlockState state, @NotNull World world, @NotNull BlockPos pos, @NotNull PlayerEntity player, @NotNull Hand hand, @NotNull BlockRayTraceResult hit) {
        ActionResultType result;
        ModularFrameTile blockEntity = getTE(world, pos);
        if (!blockEntity.hasAccess(player))
            return ActionResultType.FAIL;

        ItemStack handStack = player.getHeldItem(hand);
        Item handItem = handStack.getItem();
        Direction side = hit.getFace();
        if (handItem instanceof ScrewdriverItem) {
            if (!world.isRemote) {
                if (side == state.get(FACING)) {
                    if (hitModule(side, pos, hit.getHitVec())) {
                        switch (ScrewdriverItem.getMode(handStack)) {
                            case REMOVE_MOD:
                                blockEntity.dropModule(player, side);
                                break;
                            case REMOVE_UP:
                                blockEntity.dropUpgrades(player, side);
                                break;
                            case INTERACT:
                                blockEntity.module.screw(world, pos, player, handStack);
                                break;
                        }

                        blockEntity.markDirty();
                    }
                }
            }
            result = ActionResultType.SUCCESS;
        } else if (handItem instanceof ModuleItem && blockEntity.acceptsModule()) {
            if (!world.isRemote) {
                blockEntity.setModule(((ModuleItem) handItem).createModule(), player, handStack);
                if (!player.isCreative()) player.getHeldItem(hand).shrink(1);
                blockEntity.markDirty();
            }
            result = ActionResultType.SUCCESS;
        } else if (handItem instanceof UpgradeItem && blockEntity.acceptsUpgrade()) {
            if (!world.isRemote) {
                if (blockEntity.tryAddUpgrade(((UpgradeItem) handItem).createUpgrade(), player, handStack)) {
                    if (!player.isCreative()) player.getHeldItem(hand).shrink(1);
                    blockEntity.markDirty();
                }
            }
            result = ActionResultType.SUCCESS;
        } else result = blockEntity.module.onUse(world, pos, state, player, hand, side, hit);
        return result;
    }

    public static boolean hitModule(Direction side, BlockPos pos, Vector3d hitVec) {
        double x = Math.abs(Math.abs(hitVec.x) - Math.abs(pos.getX()));
        double y = Math.abs(Math.abs(hitVec.y) - Math.abs(pos.getY()));
        double z = Math.abs(Math.abs(hitVec.z) - Math.abs(pos.getZ()));

        switch (side) {
            case DOWN:
            case UP:
                return x > 0.17F && x < 0.83F && z > 0.17F && z < 0.83F;
            case NORTH:
            case SOUTH:
                return x > 0.17F && x < 0.83F && y > 0.20F && y < 0.80F;
            case WEST:
            case EAST:
                return z > 0.17F && z < 0.83F && y > 0.20F && y < 0.80F;
        }
        return false;
    }

    @SubscribeEvent
    public static void onPlayerInteracted(PlayerInteractEvent.RightClickBlock event) {
        if (event.getWorld().getBlockState(event.getPos()).getBlock() instanceof ModularFrameBlock) {
            event.setUseBlock(Event.Result.ALLOW);
        }
    }
    //endregion

    //region <placing/breaking>
    @Nullable
    @Override
    public BlockState getStateForPlacement(@NotNull BlockItemUseContext context) {
        return super.getStateForPlacement(context).with(FACING, context.getFace());
    }

    public boolean canAttachTo(IBlockReader world, BlockPos pos, Direction side) {
        BlockState state = world.getBlockState(pos);
        return (state.isSolidSide(world, pos, side) || state.getMaterial().isSolid())
                && !RepeaterBlock.isDiode(state)
                && !(state.getBlock() instanceof ModularFrameBlock);
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isValidPosition(BlockState state, @NotNull IWorldReader world, BlockPos pos) {
        Direction side = state.get(FACING);
        return canAttachTo(world, pos.offset(side.getOpposite()), side);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void neighborChanged(@NotNull BlockState state, @NotNull World world, @NotNull BlockPos pos, @NotNull Block block, @NotNull BlockPos fromPos, boolean isMoving) {
        if (!world.isRemote) {
            Direction side = state.get(FACING);
            if (!canAttachTo(world, pos.offset(side.getOpposite()), side)) {
                world.destroyBlock(pos, true);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onExplosion(ExplosionEvent.Detonate event) {
        List<BlockPos> toRemove = new ArrayList<>();
        for (BlockPos pos : event.getAffectedBlocks()) {
            TileEntity tmp = event.getWorld().getTileEntity(pos);
            if (tmp instanceof ModularFrameTile) {
                ModularFrameTile tile = (ModularFrameTile) tmp;
                if (tile.isBlastResist()) {
                    toRemove.add(tile.getAttachedPos());
                    toRemove.add(tile.getPos());
                }
            }
        }
        for (BlockPos pos : toRemove) {
            event.getAffectedBlocks().remove(pos);
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public @NotNull List<ItemStack> getDrops(@NotNull BlockState state, @NotNull LootContext.Builder builder) {
        List<ItemStack> drops = super.getDrops(state, builder);
        drops.add(new ItemStack(asItem()));

        ModularFrameTile tile = (ModularFrameTile)builder.get(LootParameters.BLOCK_ENTITY);
        if (tile != null) {
            PlayerEntity player = builder.get(LootParameters.THIS_ENTITY) instanceof PlayerEntity ? (PlayerEntity)builder.get(LootParameters.THIS_ENTITY) : null;
            if (!(tile.module instanceof EmptyModule)) {
                ItemStack modStack = new ItemStack(tile.module.getItem());
                drops.add(modStack);
                tile.module.onRemove(builder.getWorld(), tile.getPos(), state.get(FACING), player, modStack);
            }
            for (UpgradeBase upgrade : tile.upgrades) {
                ItemStack upStack = new ItemStack(upgrade.getItem());
                drops.add(upStack);
                upgrade.onRemove(builder.getWorld(), tile.getPos(), state.get(FACING), player, upStack);
            }
        }
        return drops;
    }

    @Override
    public void addProbeInfo(ProbeMode probeMode, IProbeInfo iProbeInfo, PlayerEntity playerEntity, World world, BlockState blockState, IProbeHitData iProbeHitData) {
    }
    //endregion
}
