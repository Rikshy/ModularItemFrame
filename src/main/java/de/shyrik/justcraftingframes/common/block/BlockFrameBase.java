package de.shyrik.justcraftingframes.common.block;

import com.teamwizardry.librarianlib.features.base.block.tile.BlockModContainer;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

public abstract class BlockFrameBase extends BlockModContainer {

    public static final PropertyEnum<EnumFacing> FACING = PropertyEnum.create("facing", EnumFacing.class);

    private static final AxisAlignedBB UP_AABB = new AxisAlignedBB(0.125D, 1.0D, 0.125D, 0.875D, 0.9375D, 0.875D);
    private static final AxisAlignedBB DOWN_AABB = new AxisAlignedBB(0.125D, 0.0D, 0.125D, 0.875D, 0.0625D, 0.875D);
    private static final AxisAlignedBB NORTH_AABB = new AxisAlignedBB(0.125D, 0.125D, 0.0D, 0.875D, 0.875D, 0.0625D);
    private static final AxisAlignedBB SOUTH_AABB = new AxisAlignedBB(0.125D, 0.125D, 1.0D, 0.875D, 0.875D, 2.9375D);
    private static final AxisAlignedBB EAST_AABB = new AxisAlignedBB(0.9375D, 0.125D, 0.125D, 1.0D, 0.875D, 0.875D);
    private static final AxisAlignedBB WEST_AABB = new AxisAlignedBB(0.0D, 0.125D, 0.125D, 0.0625D, 0.875D, 0.875D);

    public BlockFrameBase(@NotNull String name) {
        super(name, Material.WOOD);
        setHardness(2.0F);
        setResistance(4.0F);
        setSoundType(SoundType.WOOD);

        setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isFullBlock(IBlockState state) {
        return false;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isFullCube(IBlockState state){
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
    @Nonnull
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

    @Override
    @SuppressWarnings("deprecation")
    public AxisAlignedBB getCollisionBoundingBox(IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos) {
        return getBoundingBox(state, world, pos);
    }

    @Override
    @SuppressWarnings("deprecation")
    public @Nonnull IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        if (placer.rotationPitch > 45) return this.getStateFromMeta(meta).withProperty(FACING, EnumFacing.DOWN);
        if (placer.rotationPitch < -45) return this.getStateFromMeta(meta).withProperty(FACING, EnumFacing.UP);

        return this.getStateFromMeta(meta).withProperty(FACING, placer.getAdjustedHorizontalFacing());
    }

    @Override
    @Nonnull
    @SuppressWarnings("deprecation")
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(FACING, EnumFacing.getFront(meta & 7));
    }

    @Override
    public boolean canPlaceBlockOnSide(@Nonnull World worldIn, @Nonnull BlockPos pos, EnumFacing side) {
        return !worldIn.isAirBlock(pos.offset(side.getOpposite()));
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(FACING).getIndex();
    }

    @Override
    @SuppressWarnings("deprecation")
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        BlockPos attachedPos = pos.offset(state.getValue(FACING));

        if (worldIn.isAirBlock(attachedPos)) {
            dropBlockAsItem(worldIn, pos, state, 0);
            worldIn.setBlockToAir(pos);
        }
    }

    @Override
    @Nonnull
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING);
    }
}
