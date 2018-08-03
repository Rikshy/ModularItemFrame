package de.shyrik.justcraftingframes.common.block;

import com.teamwizardry.librarianlib.features.base.block.tile.BlockModContainer;
import de.shyrik.justcraftingframes.client.render.FrameRenderer;
import de.shyrik.justcraftingframes.common.tile.TileFrameBase;
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
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

public abstract class BlockFrameBase extends BlockModContainer {

    public static final PropertyEnum<EnumFacing> FACING = PropertyEnum.create("facing", EnumFacing.class);

    private static final AxisAlignedBB UP_AABB = new AxisAlignedBB(0.125D, 1.0D, 0.125D, 0.875D, 0.9375D, 0.875D);
    private static final AxisAlignedBB DOWN_AABB = new AxisAlignedBB(0.125D, 0.0D, 0.125D, 0.875D, 0.0625D, 0.875D);
    private static final AxisAlignedBB NORTH_AABB = new AxisAlignedBB(0.125D, 0.125D, 0.0D, 0.875D, 0.875D, 0.0625D);
    private static final AxisAlignedBB SOUTH_AABB = new AxisAlignedBB(0.125D, 0.125D, 1.0D, 0.875D, 0.875D, 0.9375D);
    private static final AxisAlignedBB EAST_AABB = new AxisAlignedBB(0.9375D, 0.125D, 0.125D, 1.0D, 0.875D, 0.875D);
    private static final AxisAlignedBB WEST_AABB = new AxisAlignedBB(0.0D, 0.125D, 0.125D, 0.0625D, 0.875D, 0.875D);

    public BlockFrameBase(@NotNull String name) {
        super(name, Material.WOOD);
        setHardness(2.0F);
        setResistance(4.0F);
        setSoundType(SoundType.WOOD);

        setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
    }

    @SideOnly(Side.CLIENT)
    public void initModel() {
        ClientRegistry.bindTileEntitySpecialRenderer(TileFrameBase.class, new FrameRenderer());
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
        EnumFacing facing = state.getValue(FACING);

        switch (facing) {
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
        if (placer.rotationPitch > 45) return this.getStateFromMeta(meta).withProperty(FACING, EnumFacing.UP);
        if (placer.rotationPitch < -45) return this.getStateFromMeta(meta).withProperty(FACING, EnumFacing.DOWN);

        return this.getStateFromMeta(meta).withProperty(FACING, placer.getAdjustedHorizontalFacing());
    }

    @Override
    @Nonnull
    @SuppressWarnings("deprecation")
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(FACING, EnumFacing.getFront(meta & 7));
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(FACING).getIndex();
    }

    @Override
    public void onNeighborChange(IBlockAccess worldIn, BlockPos pos, BlockPos neighbor) {
        IBlockState state = worldIn.getBlockState(pos);
        BlockPos attachedPos = pos.offset(state.getValue(FACING));

        if (worldIn.isAirBlock(attachedPos)) {
            dropBlockAsItem((World) worldIn, pos, state, 0);
            ((World) worldIn).setBlockToAir(pos);
        }
    }

    @Override
    @Nonnull
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING);
    }
}
