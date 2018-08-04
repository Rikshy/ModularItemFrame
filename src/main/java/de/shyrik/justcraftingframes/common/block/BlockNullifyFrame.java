package de.shyrik.justcraftingframes.common.block;

import de.shyrik.justcraftingframes.common.tile.TileNullifyFrame;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;

public class BlockNullifyFrame extends BlockFrameBase {

	public BlockNullifyFrame() {
		super("nullify_frame");
	}

	private ItemStack lastStack = ItemStack.EMPTY;

	@Override
	public boolean onBlockActivated(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull EntityPlayer playerIn, @Nonnull EnumHand hand, @Nonnull EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (!worldIn.isRemote) {
			ItemStack held = playerIn.getHeldItem(hand);
			if(!playerIn.isSneaking()) {
				lastStack = held;
				held.setCount(0);
				worldIn.playSound(null, pos, SoundEvents.BLOCK_LAVA_EXTINGUISH, SoundCategory.BLOCKS, 0.4F, 0.7F);
			} else if (held.isEmpty() && !lastStack.isEmpty()) {
				playerIn.setHeldItem(hand, lastStack);
				lastStack = ItemStack.EMPTY;
				worldIn.playSound(null, pos, SoundEvents.ENTITY_ENDERPEARL_THROW, SoundCategory.BLOCKS, 0.4F, 0.7F);
			}
		}
		return true;
	}

	@Nullable
	@Override
	public TileEntity createTileEntity(@NotNull World world, @NotNull IBlockState state) {
		return new TileNullifyFrame();
	}
}
