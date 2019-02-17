package de.shyrik.modularitemframe.common.block;

import de.shyrik.modularitemframe.ModularItemFrame;
import de.shyrik.modularitemframe.api.ConfigValues;
import de.shyrik.modularitemframe.common.item.ItemScrewdriver;
import de.shyrik.modularitemframe.common.tile.TileModularFrame;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.state.DirectionProperty;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;
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

    public static final DirectionProperty FACING = DirectionProperty.create("facing");
    private static final Properties PROPERTIES = Properties.create(Material.WOOD)
            .hardnessAndResistance(4F)
            .sound(SoundType.WOOD);


    private static final AxisAlignedBB UP_AABB = new AxisAlignedBB(0.125D, 1.0D, 0.125D, 0.875D, 0.895D, 0.875D);
    private static final AxisAlignedBB DOWN_AABB = new AxisAlignedBB(0.125D, 0.0D, 0.125D, 0.875D, 0.11D, 0.875D);
    private static final AxisAlignedBB NORTH_AABB = new AxisAlignedBB(0.125D, 0.125D, 0.0D, 0.875D, 0.875D, 0.11D);
    private static final AxisAlignedBB SOUTH_AABB = new AxisAlignedBB(0.125D, 0.125D, 1.0D, 0.875D, 0.875D, 0.895D);
    private static final AxisAlignedBB EAST_AABB = new AxisAlignedBB(0.895D, 0.125D, 0.125D, 1.0D, 0.875D, 0.875D);
    private static final AxisAlignedBB WEST_AABB = new AxisAlignedBB(0.0D, 0.125D, 0.125D, 0.11D, 0.875D, 0.875D);

    public static final ResourceLocation LOC = new ResourceLocation(ModularItemFrame.MOD_ID, "modular_frame");
    public static final ResourceLocation INNER_DEF_LOC = new ResourceLocation(ModularItemFrame.MOD_ID, "blocks/hard_inner");
    public static final ResourceLocation INNER_HARD_LOC = new ResourceLocation(ModularItemFrame.MOD_ID, "blocks/hard_inner");
    public static final ResourceLocation INNER_HARDEST_LOC = new ResourceLocation(ModularItemFrame.MOD_ID, "blocks/hardest_inner");

    public BlockModularFrame() {
        super(PROPERTIES);

        this.setDefaultState(this.stateContainer.getBaseState().with(FACING, EnumFacing.NORTH).with(FACING, EnumFacing.NORTH));
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
    public void onBlockClicked(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull EntityPlayer playerIn) {
        getTE(worldIn, pos).module.onBlockClicked(worldIn, pos, playerIn);
    }


    @Override
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
            } else if (handItem.getItem() instanceof IModule && tile.acceptsModule()) {
                if (!worldIn.isRemote) {
                    tile.setModule(((ItemModule) handItem.getItem()).getModuleId(handItem));
                    if (!player.isCreative()) player.getHeldItem(hand).shrink(1);
                    tile.markDirty();
                }
                moveHand = true;
            } else if (handItem.getItem() instanceof ItemUpgrade && tile.acceptsUpgrade()) {
                if (!worldIn.isRemote) {
                    if (tile.tryAddUpgrade(((ItemUpgrade) handItem.getItem()).getUpgradeId(handItem))) {
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
    public AxisAlignedBB getBoundingBox(IBlockState state, IWorldReaderBase source, BlockPos pos) {
        switch (state.get(FACING)) {
            case UP:
                return UP_AABB;
            case DOWN:
                return DOWN_AABB;
            case NORTH:
                return NORTH_AABB;
            case SOUTH:
                return SOUTH_AABB;
            case EAST:
                return EAST_AABB;
            case WEST:
                return WEST_AABB;
        }
        return null;
    }

    @Override
    public VoxelShape getShape(IBlockState state, IBlockReader worldIn, BlockPos pos) {
        return super.getShape(state, worldIn, pos);
    }
    //endregion

    //region <placing/breaking>



    @Override
    public boolean canPlaceBlockOnSide(@Nonnull World worldIn, @Nonnull BlockPos pos, EnumFacing side) {
        BlockPos adjacent = pos.offset(side.getOpposite());
        IBlockState state = worldIn.getBlockState(adjacent);
        return (state.isSideSolid(worldIn, adjacent, side) || state.getMaterial().isSolid()) && !BlockRedstoneDiode.isDiode(state) && CompatHelper.canPlace(worldIn, adjacent, side);
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isValidPosition(IBlockState state, IWorldReaderBase worldIn, BlockPos pos) {
        BlockPos adjacent = pos.offset(side.getOpposite());
        IBlockState state = worldIn.getBlockState(adjacent);
        return (state.isSideSolid(worldIn, adjacent, side) || state.getMaterial().isSolid()) && !BlockRedstoneDiode.isDiode(state);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        if (!worldIn.isRemote) {
            if (!canPlaceBlockOnSide(worldIn, pos, state.get(FACING).getOpposite())) {
                dropBlockAsItemWithChance(worldIn, pos, state, 0);
                worldIn.removeBlock(pos);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onExplosion(ExplosionEvent.Detonate event) {
        List<BlockPos> toRemove = new ArrayList<>();
        for (BlockPos pos : event.getAffectedBlocks()) {
            for (TileEntity tmp : CompatHelper.getTiles(event.getWorld(), pos)) {
                if (tmp instanceof TileModularFrame) {
                    TileModularFrame tile = (TileModularFrame) tmp;
                    if (tile.isBlastResist()) {
                        toRemove.add(tile.getAttachedPos());
                        toRemove.add(tile.getPos());
                    }
                }
            }
        }
        for (BlockPos pos : toRemove) {
            event.getAffectedBlocks().remove(pos);
        }
    }

    @Override
    public void breakBlock(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
        getTE(worldIn, pos).module.onRemove(worldIn, pos, state.getValue(FACING), null);
        getTE(worldIn, pos).dropUpgrades(null, state.getValue(FACING));
        super.breakBlock(worldIn, pos, state);
    }
    //endregion

    //region <state>
    @Nonnull
    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING);
    }

    @Nonnull
    @Override
    @SuppressWarnings("deprecation")
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        return this.getStateFromMeta(meta).withProperty(FACING, facing.getOpposite());
    }

    @Nonnull
    @Override
    @SuppressWarnings("deprecation")
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(FACING, EnumFacing.byIndex(meta & 7));
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(FACING).getIndex();
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
    public boolean isPassable(IWorldReaderBase worldIn, BlockPos pos) {
        return true;
    }

    @Override
    public boolean canCreatureSpawn(IBlockState state, IWorldReaderBase world, BlockPos pos, EntitySpawnPlacementRegistry.SpawnPlacementType type, @Nullable EntityType<? extends EntityLiving> entityType) {
        return false;
    }

    @Override
    public boolean canPlaceTorchOnTop(IBlockState state, @Nonnull IWorldReaderBase world, @Nonnull BlockPos pos) {
        return false;
    }
    //endregion
}
