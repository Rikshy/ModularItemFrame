package de.shyrik.justcraftingframes.common.block;

import de.shyrik.justcraftingframes.common.tile.TileNullifyFrame;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;

public class BlockNullifyFrame extends BlockFrameBase {

	public BlockNullifyFrame() {
		super("nullify_frame");
	}

	private TileNullifyFrame getTE(@Nonnull World world, @Nonnull BlockPos pos) {
		return (TileNullifyFrame)world.getTileEntity(pos);
	}

	@Override
	public boolean onBlockActivated(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull EntityPlayer playerIn, @Nonnull EnumHand hand, @Nonnull EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (!worldIn.isRemote) {
			getTE(worldIn, pos).nullify(playerIn, hand);
		}
		return true;
	}

	@Nullable
	@Override
	public TileEntity createTileEntity(@NotNull World world, @NotNull IBlockState state) {
		return new TileNullifyFrame();
	}
}
