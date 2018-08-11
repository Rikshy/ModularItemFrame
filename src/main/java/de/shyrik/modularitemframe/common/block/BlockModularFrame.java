package de.shyrik.modularitemframe.common.block;

import com.teamwizardry.librarianlib.features.base.block.tile.BlockModContainer;
import de.shyrik.modularitemframe.api.ModuleRegistry;
import de.shyrik.modularitemframe.client.render.FrameRenderer;
import de.shyrik.modularitemframe.common.item.ItemModule;
import de.shyrik.modularitemframe.common.tile.TileModularFrame;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;

public class BlockModularFrame extends BlockModContainer {

    public static final PropertyEnum<EnumFacing> FACING = PropertyEnum.create("facing", EnumFacing.class);

    private static final AxisAlignedBB UP_AABB = new AxisAlignedBB(0.125D, 1.0D, 0.125D, 0.875D, 0.895D, 0.875D);
    private static final AxisAlignedBB DOWN_AABB = new AxisAlignedBB(0.125D, 0.0D, 0.125D, 0.875D, 0.11D, 0.875D);
    private static final AxisAlignedBB NORTH_AABB = new AxisAlignedBB(0.125D, 0.125D, 0.0D, 0.875D, 0.875D, 0.11D);
    private static final AxisAlignedBB SOUTH_AABB = new AxisAlignedBB(0.125D, 0.125D, 1.0D, 0.875D, 0.875D, 0.895D);
    private static final AxisAlignedBB EAST_AABB = new AxisAlignedBB(0.895D, 0.125D, 0.125D, 1.0D, 0.875D, 0.875D);
    private static final AxisAlignedBB WEST_AABB = new AxisAlignedBB(0.0D, 0.125D, 0.125D, 0.11D, 0.875D, 0.875D);

    public BlockModularFrame() {
        super("modular_frame", Material.WOOD);
        setHardness(2.0F);
        setResistance(4.0F);
        setSoundType(SoundType.WOOD);

        setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
    }
    @SideOnly(Side.CLIENT)
    public void initModel() {
        ClientRegistry.bindTileEntitySpecialRenderer(TileModularFrame.class, new FrameRenderer());
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(@NotNull World world, @NotNull IBlockState state) {
        return new TileModularFrame();
    }

    private TileModularFrame getTE(@Nonnull World world, @Nonnull BlockPos pos) {
        return (TileModularFrame) world.getTileEntity(pos);
    }

    @Override
    public void onBlockClicked(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull EntityPlayer playerIn) {
        getTE(worldIn, pos).module.onBlockClicked(worldIn, pos, playerIn);
    }

    @Override
    public boolean onBlockActivated(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull EntityPlayer playerIn, @Nonnull EnumHand hand, @Nonnull EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack held = playerIn.getHeldItem(hand);
        TileModularFrame tile = getTE(worldIn, pos);
        if (held.getItem() instanceof ItemModule) {
            if (!worldIn.isRemote) {
                tile.setModule(ModuleRegistry.createModuleInstance(((ItemModule) held.getItem()).moduleId));
                held.setCount(held.getCount() - 1);
                tile.markDirty();
            }
        } else {
            tile.module.onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ);
        }
        return true;
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
        return this.getStateFromMeta(meta).withProperty(FACING, facing.getOpposite());
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
