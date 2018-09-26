package de.shyrik.modularitemframe.common.block;

import de.shyrik.modularitemframe.ModularItemFrame;
import de.shyrik.modularitemframe.api.ConfigValues;
import de.shyrik.modularitemframe.common.compat.CompatHelper;
import de.shyrik.modularitemframe.common.item.ItemModule;
import de.shyrik.modularitemframe.common.item.ItemUpgrade;
import de.shyrik.modularitemframe.common.item.ItemScrewdriver;
import de.shyrik.modularitemframe.common.tile.TileModularFrame;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.IProbeInfoAccessor;
import mcjty.theoneprobe.api.ProbeMode;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRedstoneDiode;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber
@Optional.Interface(iface = "mcjty.theoneprobe.api.IProbeInfoAccessor", modid = "theoneprobe")
public class BlockModularFrame extends Block implements IProbeInfoAccessor {

    public static final PropertyDirection FACING = PropertyDirection.create("facing");

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
        super(Material.WOOD);
        setHardness(2.0F);
        setResistance(4.0F);
        setSoundType(SoundType.WOOD);

        setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
    }

    //region <tileentity>
    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(@Nonnull World world, @Nonnull IBlockState state) {
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
    public boolean onBlockActivated(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull EntityPlayer playerIn, @Nonnull EnumHand hand, @Nonnull EnumFacing facing, float hitX, float hitY, float hitZ) {
        boolean moveHand;
        if (!(playerIn instanceof FakePlayer) || ConfigValues.AllowFakePlayers) {
            TileModularFrame tile = getTE(worldIn, pos);
            ItemStack handItem = playerIn.getHeldItem(hand);
            if (handItem.getItem() instanceof ItemScrewdriver) {
                if (!worldIn.isRemote) {
                    if (facing.getOpposite() == state.getValue(FACING)) {
                        if (hitModule(facing.getOpposite(), hitX, hitY, hitZ)) {
                            if (ItemScrewdriver.getMode(handItem) == ItemScrewdriver.EnumMode.INTERACT) {
                                tile.module.screw(worldIn, pos, playerIn, handItem);
                            } else tile.dropModule(facing, playerIn);
                        } else tile.dropUpgrades(playerIn, facing);
                        tile.markDirty();
                    }
                }
                moveHand = true;
            } else if (handItem.getItem() instanceof ItemModule && tile.acceptsModule()) {
                if (!worldIn.isRemote) {
                    tile.setModule(((ItemModule) handItem.getItem()).moduleId);
                    if (!playerIn.isCreative()) playerIn.getHeldItem(hand).shrink(1);
                    tile.markDirty();
                }
                moveHand = true;
            } else if (handItem.getItem() instanceof ItemUpgrade && tile.acceptsUpgrade()) {
                if (!worldIn.isRemote) {
                    if (tile.tryAddUpgrade(((ItemUpgrade) handItem.getItem()).upgradeId)) {
                        if (!playerIn.isCreative()) playerIn.getHeldItem(hand).shrink(1);
                        tile.markDirty();
                    }
                }
                moveHand = true;
            } else moveHand = tile.module.onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ);
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

    @Override
    @Optional.Method(modid = "theoneprobe")
    public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world, IBlockState blockState, IProbeHitData data) {
        getTE(world, data.getPos()).module.addProbeInfo(mode, probeInfo, player, world, blockState, data);
    }

    @Nonnull
    @Override
    @SuppressWarnings("deprecation")
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        switch (state.getValue(FACING)) {
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
        return FULL_BLOCK_AABB;
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
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        if (!worldIn.isRemote) {
            if (!canPlaceBlockOnSide(worldIn, pos, state.getValue(FACING).getOpposite())) {
                dropBlockAsItem(worldIn, pos, state, 0);
                worldIn.setBlockToAir(pos);
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
    @SuppressWarnings("deprecation")
    public boolean shouldSideBeRendered(IBlockState blockState, @Nonnull IBlockAccess blockAccess, @Nonnull BlockPos pos, EnumFacing side) {
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
    @SuppressWarnings("deprecation")
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isPassable(IBlockAccess worldIn, BlockPos pos) {
        return true;
    }

    @Override
    public boolean canCreatureSpawn(@Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, EntityLiving.SpawnPlacementType type) {
        return false;
    }

    @Override
    public boolean canPlaceTorchOnTop(IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos) {
        return false;
    }
    //endregion
}
