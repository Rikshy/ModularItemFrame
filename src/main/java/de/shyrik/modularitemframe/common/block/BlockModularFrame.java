package de.shyrik.modularitemframe.common.block;

import de.shyrik.modularitemframe.ModularItemFrame;
import de.shyrik.modularitemframe.api.ItemModule;
import de.shyrik.modularitemframe.api.UpgradeBase;
import de.shyrik.modularitemframe.common.item.ItemScrewdriver;
import de.shyrik.modularitemframe.common.module.ModuleEmpty;
import de.shyrik.modularitemframe.common.tile.TileModularFrame;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.IProbeInfoAccessor;
import mcjty.theoneprobe.api.ProbeMode;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Optional.Interface(iface = "mcjty.theoneprobe.api.IProbeInfoAccessor", modid = "theoneprobe")
public class BlockModularFrame extends BlockContainer implements IProbeInfoAccessor {

    public static final PropertyDirection FACING = PropertyDirection.create("facing");

	private static final AxisAlignedBB UP_AABB = new AxisAlignedBB(0.125D, 1.0D, 0.125D, 0.875D, 0.895D, 0.875D);
	private static final AxisAlignedBB DOWN_AABB = new AxisAlignedBB(0.125D, 0.0D, 0.125D, 0.875D, 0.11D, 0.875D);
	private static final AxisAlignedBB NORTH_AABB = new AxisAlignedBB(0.125D, 0.125D, 0.0D, 0.875D, 0.875D, 0.11D);
	private static final AxisAlignedBB SOUTH_AABB = new AxisAlignedBB(0.125D, 0.125D, 1.0D, 0.875D, 0.875D, 0.895D);
	private static final AxisAlignedBB EAST_AABB = new AxisAlignedBB(0.895D, 0.125D, 0.125D, 1.0D, 0.875D, 0.875D);
	private static final AxisAlignedBB WEST_AABB = new AxisAlignedBB(0.0D, 0.125D, 0.125D, 0.11D, 0.875D, 0.875D);

	public static final ResourceLocation ID = new ResourceLocation(ModularItemFrame.MOD_ID,"modular_frame");

	public BlockModularFrame() {
		super(Material.WOOD);
		setTranslationKey(ID.toString());
		setRegistryName(ID);
		setHardness(2.0F);
		setResistance(4.0F);
		setSoundType(SoundType.WOOD);
		setCreativeTab(ModularItemFrame.TAB);

		setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
	}

    @Nullable
	@Override
	public TileEntity createNewTileEntity(@Nonnull World worldIn, int meta) {
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
        boolean moveHand;
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
        }
        else if (handItem.getItem() instanceof ItemModule) {
            if (!worldIn.isRemote && tile.acceptsModule()) {
                tile.setModule((ItemModule) handItem.getItem());
                if (!playerIn.isCreative()) playerIn.getHeldItem(hand).shrink(1);
                tile.markDirty();
            }
            moveHand = true;
        }
        else
            moveHand = tile.module.onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ);
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

	@Override
	public void breakBlock(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
        getTE(worldIn, pos).module.onRemove(worldIn, pos, state.getValue(FACING), null);
        getTE(worldIn, pos).dropUpgrades(null, state.getValue(FACING));
		super.breakBlock(worldIn, pos, state);
	}

	@Override
	@Optional.Method(modid = "theoneprobe")
	public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world, IBlockState blockState, IProbeHitData data) {
		getTE(world, data.getPos()).module.addProbeInfo(mode, probeInfo, player, world, blockState, data);
	}

	@Override
	@SuppressWarnings("deprecation")
	public boolean isFullBlock(IBlockState state) {
		return false;
	}

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
	public boolean canRenderInLayer(IBlockState state, BlockRenderLayer layer) {
		return layer == BlockRenderLayer.CUTOUT;
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
	public @Nonnull
	IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
		return this.getStateFromMeta(meta).withProperty(FACING, facing.getOpposite());
	}

	@Override
	@Nonnull
	@SuppressWarnings("deprecation")
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(FACING, EnumFacing.byIndex(meta & 7));
	}

    @Override
    public float getExplosionResistance(World world, BlockPos pos, @Nullable Entity exploder, Explosion explosion) {
        return getTE(world, pos).countUpgradeOfType(UpgradeBase.UpgradeBlastResist.class) >= 1 ? 200F : 4F;
    }

    @Override
	public boolean canPlaceBlockOnSide(@Nonnull World worldIn, @Nonnull BlockPos pos, EnumFacing side) {
		return !worldIn.isAirBlock(pos.offset(side.getOpposite()));
	}

	@Override
    public boolean canCreatureSpawn(@Nonnull IBlockState state, @Nonnull  IBlockAccess world, @Nonnull BlockPos pos, EntityLiving.SpawnPlacementType type) {
        return false;
    }

    @Override
    public boolean canPlaceTorchOnTop(IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos) {
        return false;
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
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, FACING);
    }

	@Nonnull
    @Override
    @SuppressWarnings("deprecation")
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }
}
