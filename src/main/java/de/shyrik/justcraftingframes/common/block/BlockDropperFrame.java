package de.shyrik.justcraftingframes.common.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlockDropperFrame extends BlockFrameBase {

	public BlockDropperFrame(@NotNull String name) {
		super(name);
	}

	@Nullable
	@Override
	public TileEntity createTileEntity(@NotNull World world, @NotNull IBlockState state) {
		return null;
	}
}
