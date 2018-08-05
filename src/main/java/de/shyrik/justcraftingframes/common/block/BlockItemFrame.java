package de.shyrik.justcraftingframes.common.block;

import de.shyrik.justcraftingframes.client.render.FrameItemRenderer;
import de.shyrik.justcraftingframes.common.tile.TileItemFrame;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;

public class BlockItemFrame extends BlockFrameBase {

	public BlockItemFrame() {
		super("item_frame");
	}

	@SideOnly(Side.CLIENT)
	public void initModel() {
		ClientRegistry.bindTileEntitySpecialRenderer(TileItemFrame.class, new FrameItemRenderer());
	}

	@Override
	public void onBlockClicked(World worldIn, BlockPos pos, EntityPlayer playerIn) {
		if(!worldIn.isRemote) {
			TileItemFrame te = getTE(worldIn, pos);
			te.rotate(playerIn);
			te.markDirty();
		}
	}

	@Override
	public boolean onBlockActivated(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull EntityPlayer playerIn, @Nonnull EnumHand hand, @Nonnull EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (!worldIn.isRemote) {
			TileItemFrame te = getTE(worldIn, pos);
			if(!playerIn.isSneaking()) {
				ItemStack copy = playerIn.getHeldItem(hand).copy();
				copy.setCount(1);
				te.setDisplayItem(copy);
				te.markDirty();
			}
		}
		return true;
	}

	@Nullable
	@Override
	public TileEntity createTileEntity(@NotNull World world, @NotNull IBlockState state) {
		return new TileItemFrame();
	}

	private TileItemFrame getTE(@Nonnull World world, @Nonnull BlockPos pos) {
		return (TileItemFrame)world.getTileEntity(pos);
	}
}
