package de.shyrik.modularitemframe.common.block;

import de.shyrik.modularitemframe.ModularItemFrame;
import de.shyrik.modularitemframe.init.ConfigValues;
import de.shyrik.modularitemframe.api.ItemModule;
import de.shyrik.modularitemframe.api.ItemUpgrade;
import de.shyrik.modularitemframe.api.UpgradeBase;
import de.shyrik.modularitemframe.common.item.ItemScrewdriver;
import de.shyrik.modularitemframe.common.module.ModuleEmpty;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.*;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber
public class BlockModularFrame extends Block {

    public static final DirectionProperty FACING = DirectionProperty.create("facing", EnumFacing.values());
    private static final Properties PROPERTIES = Properties.create(Material.WOOD)
            .hardnessAndResistance(4F)
            .sound(SoundType.WOOD);

    private static final VoxelShape UP_SHAPE = Block.makeCuboidShape(0.125D, 1.0D, 0.125D, 0.875D, 0.895D, 0.875D);
    private static final VoxelShape DOWN_SHAPE = Block.makeCuboidShape(0.125D, 0.0D, 0.125D, 0.875D, 0.11D, 0.875D);
    private static final VoxelShape NORTH_SHAPE = Block.makeCuboidShape(0.125D, 0.125D, 0.0D, 0.875D, 0.875D, 0.11D);
    private static final VoxelShape SOUTH_SHAPE = Block.makeCuboidShape(0.125D, 0.125D, 1.0D, 0.875D, 0.875D, 0.895D);
    private static final VoxelShape EAST_SHAPE = Block.makeCuboidShape(0.895D, 0.125D, 0.125D, 1.0D, 0.875D, 0.875D);
    private static final VoxelShape WEST_SHAPE = Block.makeCuboidShape(0.0D, 0.125D, 0.125D, 0.11D, 0.875D, 0.875D);

    public static final ResourceLocation LOC = new ResourceLocation(ModularItemFrame.MOD_ID, "modular_frame");
    public static final ResourceLocation INNER_DEF_LOC = new ResourceLocation(ModularItemFrame.MOD_ID, "block/hard_inner");
    public static final ResourceLocation INNER_HARD_LOC = new ResourceLocation(ModularItemFrame.MOD_ID, "block/hard_inner");
    public static final ResourceLocation INNER_HARDEST_LOC = new ResourceLocation(ModularItemFrame.MOD_ID, "block/hardest_inner");

    public BlockModularFrame() {
        super(PROPERTIES);

        setDefaultState(this.stateContainer.getBaseState().with(FACING, EnumFacing.NORTH));
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> builder) {
        builder.add(FACING);
    }

    //region <tileentity>
    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(IBlockState state, IBlockReader world) {
        return new TileModularFrame();
    }

    private TileModularFrame getTE(@Nonnull World world, @Nonnull BlockPos pos) {
        return (TileModularFrame) world.getTileEntity(pos);
    }
    //endregion

    //region <interaction>
    @Override
    @SuppressWarnings("deprecation")
    public void onBlockClicked(IBlockState state, World worldIn, BlockPos pos, EntityPlayer playerIn) {
        getTE(worldIn, pos).module.onBlockClicked(worldIn, pos, playerIn);
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean onBlockActivated(IBlockState state, World worldIn, BlockPos pos, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        boolean moveHand = false;
        if (!(player instanceof FakePlayer) || ConfigValues.AllowFakePlayers) {
            TileModularFrame tile = getTE(worldIn, pos);
            ItemStack handItem = player.getHeldItem(hand);
            if (handItem.getItem() instanceof ItemScrewdriver) {
                if (!worldIn.isRemote) {
                    if (side.getOpposite() == state.get(FACING)) {
                        if (hitModule(side.getOpposite(), hitX, hitY, hitZ)) {
                            if (ItemScrewdriver.getMode(handItem) == ItemScrewdriver.EnumMode.INTERACT) {
                                tile.module.screw(worldIn, pos, player, handItem);
                            } else tile.dropModule(side, player);
                        } else tile.dropUpgrades(player, side);
                        tile.markDirty();
                    }
                }
                moveHand = true;
            } else if (handItem.getItem() instanceof ItemModule && tile.acceptsModule()) {
                if (!worldIn.isRemote) {
                    tile.setModule(handItem.getItem().getRegistryName());
                    if (!player.isCreative()) player.getHeldItem(hand).shrink(1);
                    tile.markDirty();
                }
                moveHand = true;
            } else if (handItem.getItem() instanceof ItemUpgrade && tile.acceptsUpgrade()) {
                if (!worldIn.isRemote) {
                    if (tile.tryAddUpgrade(handItem.getItem().getRegistryName())) {
                        if (!player.isCreative()) player.getHeldItem(hand).shrink(1);
                        tile.markDirty();
                    }
                }
                moveHand = true;
            } else moveHand = tile.module.onBlockActivated(worldIn, pos, state, player, hand, side, hitX, hitY, hitZ);
        }
        return moveHand;
    }

    public static boolean hitModule(EnumFacing side, float x, float y, float z) {
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
        if (event.getWorld().getBlockState(event.getPos()).getBlock() instanceof BlockModularFrame) {
            event.setUseBlock(Event.Result.ALLOW);
        }
    }

    @Nonnull
    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(IBlockState state, IBlockReader worldIn, BlockPos pos) {
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

        return super.getShape(state, worldIn, pos);
    }
    //endregion

    //region <placing/breaking>
    @Nullable
    @Override
    public IBlockState getStateForPlacement(BlockItemUseContext ctx) {
        return getDefaultState().with(FACING, ctx.getNearestLookingDirection().getOpposite());
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isValidPosition(IBlockState state, IWorldReaderBase worldIn, BlockPos pos) {
        EnumFacing side = state.get(FACING);
        return canAttachTo(worldIn, pos.offset(side.getOpposite()), side);
    }

    public boolean canAttachTo(@Nonnull IBlockReader worldIn, @Nonnull BlockPos pos, EnumFacing side) {
        IBlockState state = worldIn.getBlockState(pos);
        boolean flag = isExceptBlockForAttachWithPiston(state.getBlock());
        return !flag && state.getBlockFaceShape(worldIn, pos, side) == BlockFaceShape.SOLID;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        if (!worldIn.isRemote) {
            if (!canAttachTo(worldIn, pos, state.get(FACING).getOpposite())) {
                dropBlockAsItemWithChance(state, worldIn, pos, Float.MAX_VALUE, 0);
                worldIn.removeBlock(pos);
            }
        }
    }

    @Override
    public void getDrops(IBlockState state, NonNullList<ItemStack> drops, World world, BlockPos pos, int fortune) {
        TileModularFrame tile = getTE(world, pos);
        if (!(tile.module instanceof ModuleEmpty))
            drops.add(new ItemStack(tile.module.getParent()));
        for (UpgradeBase upgrade : tile.upgrades)
            drops.add(new ItemStack(upgrade.getParent()));
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onExplosion(ExplosionEvent.Detonate event) {
        List<BlockPos> toRemove = new ArrayList<>();
        for (BlockPos pos : event.getAffectedBlocks()) {
            TileEntity tmp = event.getWorld().getTileEntity(pos);
            //for (TileEntity tmp : CompatHelper.getTiles(event.getWorld(), pos)) {
                if (tmp instanceof TileModularFrame) {
                    TileModularFrame tile = (TileModularFrame) tmp;
                    if (tile.isBlastResist()) {
                        toRemove.add(tile.getAttachedPos());
                        toRemove.add(tile.getPos());
                    }
                }
            //}
        }
        for (BlockPos pos : toRemove) {
            event.getAffectedBlocks().remove(pos);
        }
    }
    //endregion

    //region <rendering>
    @Override
    public boolean canRenderInLayer(IBlockState state, BlockRenderLayer layer) {
        return layer == BlockRenderLayer.TRANSLUCENT || layer == BlockRenderLayer.CUTOUT;
    }

    @Nonnull
    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public boolean doesSideBlockRendering(IBlockState state, IWorldReader world, BlockPos pos, EnumFacing face) {
        return true;
    }
    //endregion

    //region <other>
    @Override
    @SuppressWarnings("deprecation")
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean canCreatureSpawn(IBlockState state, IWorldReaderBase world, BlockPos pos, EntitySpawnPlacementRegistry.SpawnPlacementType type, @Nullable EntityType<? extends EntityLiving> entityType) {
        return false;
    }

    @Override
    public boolean canPlaceTorchOnTop(IBlockState state, @Nonnull IWorldReaderBase world, @Nonnull BlockPos pos) {
        return false;
    }

    @Nonnull
    @Override
    @SuppressWarnings("deprecation")
    public BlockFaceShape getBlockFaceShape(IBlockReader worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        return BlockFaceShape.UNDEFINED;
    }
    //endregion
}
