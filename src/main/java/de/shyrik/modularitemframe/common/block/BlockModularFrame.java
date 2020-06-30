package de.shyrik.modularitemframe.common.block;

import de.shyrik.modularitemframe.ModularItemFrame;
import de.shyrik.modularitemframe.api.ItemModule;
import de.shyrik.modularitemframe.api.ItemUpgrade;
import de.shyrik.modularitemframe.api.UpgradeBase;
import de.shyrik.modularitemframe.common.item.ItemScrewdriver;
import de.shyrik.modularitemframe.common.module.ModuleEmpty;
import de.shyrik.modularitemframe.init.ConfigValues;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.RedstoneDiodeBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootParameters;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber
public class BlockModularFrame extends Block {

    public static final DirectionProperty FACING = DirectionProperty.create("facing", Direction.values());
    public static final Block.Properties DEFAULT_PROPERTIES = Block.Properties
            .create(Material.WOOD)
            .hardnessAndResistance(4F)
            .sound(SoundType.WOOD);

    private static final VoxelShape UP_SHAPE = Block.makeCuboidShape(2, 0, 2, 14, 1.75, 14);
    private static final VoxelShape DOWN_SHAPE = Block.makeCuboidShape(2, 14.25, 2, 14, 16, 14);
    private static final VoxelShape NORTH_SHAPE = Block.makeCuboidShape(14, 2, 16, 2, 14, 14.25);
    private static final VoxelShape SOUTH_SHAPE = Block.makeCuboidShape(2, 2, 0, 14, 14, 1.75);
    private static final VoxelShape EAST_SHAPE = Block.makeCuboidShape(0, 2, 2, 1.75, 14, 14);
    private static final VoxelShape WEST_SHAPE = Block.makeCuboidShape(16, 2, 2, 14.25, 14, 14);

    public static final ResourceLocation LOC = new ResourceLocation(ModularItemFrame.MOD_ID, "modular_frame");
    public static final ResourceLocation INNER_DEF_LOC = new ResourceLocation(ModularItemFrame.MOD_ID, "block/hard_inner");
    public static final ResourceLocation INNER_HARD_LOC = new ResourceLocation(ModularItemFrame.MOD_ID, "block/hard_inner");
    public static final ResourceLocation INNER_HARDEST_LOC = new ResourceLocation(ModularItemFrame.MOD_ID, "block/hardest_inner");

    //region <initialize>
    public BlockModularFrame(Block.Properties props) {
        super(props);

        setDefaultState(this.stateContainer.getBaseState().with(FACING, Direction.NORTH));
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Nonnull
    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
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

        return super.getShape(state, worldIn, pos, context);
    }
    //endregion

    //region <tile-entity>
    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new TileModularFrame();
    }

    private TileModularFrame getTE(@Nonnull World world, @Nonnull BlockPos pos) {
        return (TileModularFrame) world.getTileEntity(pos);
    }
    //endregion

    //region <interaction>
    @Override
    @SuppressWarnings("deprecation")
    public void onBlockClicked(BlockState state, World worldIn, BlockPos pos, PlayerEntity playerIn) {
        getTE(worldIn, pos).module.onBlockClicked(worldIn, pos, playerIn);
    }

    @Nonnull
    @Override
    @SuppressWarnings("deprecation")
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        ActionResultType result = ActionResultType.PASS;
        if (!(player instanceof FakePlayer) || ConfigValues.AllowFakePlayers) {
            TileModularFrame tile = getTE(worldIn, pos);
            ItemStack handItem = player.getHeldItem(hand);
            Direction side = hit.getFace();
            if (handItem.getItem() instanceof ItemScrewdriver) {
                if (!worldIn.isRemote) {
                    if (side == state.get(FACING)) {
                        if (hitModule(side, pos, hit.getHitVec())) {
                            if (ItemScrewdriver.getMode(handItem) == ItemScrewdriver.EnumMode.INTERACT) {
                                tile.module.screw(worldIn, pos, player, handItem);
                            } else tile.dropModule(side, player);
                        } else tile.dropUpgrades(player, side);
                        tile.markDirty();
                    }
                }
                result = ActionResultType.SUCCESS;
            } else if (handItem.getItem() instanceof ItemModule && tile.acceptsModule()) {
                if (!worldIn.isRemote) {
                    tile.setModule(handItem.getItem().getRegistryName());
                    if (!player.isCreative()) player.getHeldItem(hand).shrink(1);
                    tile.markDirty();
                }
                result = ActionResultType.SUCCESS;
            } else if (handItem.getItem() instanceof ItemUpgrade && tile.acceptsUpgrade()) {
                if (!worldIn.isRemote) {
                    if (tile.tryAddUpgrade(handItem.getItem().getRegistryName())) {
                        if (!player.isCreative()) player.getHeldItem(hand).shrink(1);
                        tile.markDirty();
                    }
                }
                result = ActionResultType.SUCCESS;
            } else result = tile.module.onBlockActivated(worldIn, pos, state, player, hand, side, hit);
        }
        return result;
    }

    public static boolean hitModule(Direction side, BlockPos pos, Vec3d hitVec) {
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

    @SuppressWarnings("deprecation")
    public INamedContainerProvider getContainer(BlockState state, World worldIn, BlockPos pos) {
        return getTE(worldIn, pos).module.getContainer(state, worldIn, pos);
    }

    @SubscribeEvent
    public static void onPlayerInteracted(PlayerInteractEvent.RightClickBlock event) {
        if (event.getWorld().getBlockState(event.getPos()).getBlock() instanceof BlockModularFrame) {
            event.setUseBlock(Event.Result.ALLOW);
            event.setUseItem(Event.Result.DENY);
        }
    }
    //endregion

    //region <placing/breaking>
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext ctx) {
        return getDefaultState().with(FACING, ctx.getNearestLookingDirection().getOpposite());
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) {
        Direction side = state.get(FACING);
        return canAttachTo(worldIn, pos.offset(side.getOpposite()), side);
    }

    public boolean canAttachTo(@Nonnull IBlockReader worldIn, @Nonnull BlockPos pos, Direction side) {
        BlockState state = worldIn.getBlockState(pos);
        return (state.isSolidSide(worldIn, pos, side) || state.getMaterial().isSolid()) && !RedstoneDiodeBlock.isDiode(state);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void neighborChanged(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        if (!world.isRemote) {
            Direction side = state.get(FACING);
            if (!canAttachTo(world, pos.offset(side.getOpposite()), side)) {
                world.destroyBlock(pos, true);
            }
        }
    }

    @Nonnull
    @Override
    @SuppressWarnings("deprecation")
    public List<ItemStack> getDrops(@Nonnull BlockState state, @Nonnull LootContext.Builder builder) {
        List<ItemStack> drops = super.getDrops(state, builder);
        drops.add(new ItemStack(asItem()));

        TileModularFrame tile = (TileModularFrame)builder.get(LootParameters.BLOCK_ENTITY);
        if (tile != null) {
            if (!(tile.module instanceof ModuleEmpty)) {
                drops.add(new ItemStack(tile.module.getParent()));
                tile.module.onRemove(builder.getWorld(), tile.getPos(), state.get(FACING), null);
            }
            for (UpgradeBase upgrade : tile.upgrades)
                drops.add(new ItemStack(upgrade.getParent()));
        }
        return drops;
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onExplosionDestroy(ExplosionEvent.Detonate event) {
        List<BlockPos> toRemove = new ArrayList<>();
        for (BlockPos pos : event.getAffectedBlocks()) {
            TileEntity tmp = event.getWorld().getTileEntity(pos);
            if (tmp instanceof TileModularFrame) {
                TileModularFrame tile = (TileModularFrame) tmp;
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
    //endregion

    //region <other>
    @Override
    @SuppressWarnings("deprecation")
    public boolean canEntitySpawn(BlockState state, @Nonnull IBlockReader worldIn, @Nonnull BlockPos pos, EntityType<?> type) {
        return false;
    }
    //endregion
}
