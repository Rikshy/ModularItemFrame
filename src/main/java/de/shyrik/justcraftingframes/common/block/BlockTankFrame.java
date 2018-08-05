package de.shyrik.justcraftingframes.common.block;

import de.shyrik.justcraftingframes.client.render.FrameFluidRenderer;
import de.shyrik.justcraftingframes.common.tile.TileTankFrame;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;

public class BlockTankFrame extends BlockFrameBase {

	public BlockTankFrame() {
		super("tank_frame");
	}


	@SideOnly(Side.CLIENT)
	public void initModel() {
		ClientRegistry.bindTileEntitySpecialRenderer(TileTankFrame.class, new FrameFluidRenderer());
	}

	@Nullable
	@Override
	public TileEntity createTileEntity(@NotNull World world, @NotNull IBlockState state) {
		return new TileTankFrame();
	}

	private TileTankFrame getTE(@Nonnull World world, @Nonnull BlockPos pos) {
		return (TileTankFrame) world.getTileEntity(pos);
	}

	@Override
	public boolean onBlockActivated(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull EntityPlayer playerIn, @Nonnull EnumHand hand, @Nonnull EnumFacing facing, float hitX, float hitY, float hitZ) {
		TileTankFrame te = getTE(worldIn, pos);
		ItemStack stack = playerIn.getHeldItem(hand);
		FluidUtil.interactWithFluidHandler(playerIn, hand, te.tank);
		te.markDirty();
		return FluidUtil.getFluidHandler(stack) != null;
	}
}
