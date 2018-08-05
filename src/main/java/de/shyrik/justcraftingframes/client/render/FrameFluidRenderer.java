package de.shyrik.justcraftingframes.client.render;

import de.shyrik.justcraftingframes.common.Utils;
import de.shyrik.justcraftingframes.common.block.BlockNullifyFrame;
import de.shyrik.justcraftingframes.common.tile.TileNullifyFrame;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;

public class FrameFluidRenderer extends TileEntitySpecialRenderer<TileNullifyFrame> {

	@Override
	public void render(TileNullifyFrame te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {

		if (te != null) {
			GlStateManager.pushMatrix();
			GlStateManager.enableBlend();

			FluidStack fluid = FluidUtil.getFluidContained(new ItemStack(Items.LAVA_BUCKET));
			int color = fluid.getFluid().getColor(fluid);
			final TextureAtlasSprite still = Minecraft.getMinecraft().getTextureMapBlocks().getTextureExtry(fluid.getFluid().getStill(fluid).toString());
			final TextureAtlasSprite flowing = Minecraft.getMinecraft().getTextureMapBlocks().getTextureExtry(fluid.getFluid().getFlowing(fluid).toString());

			translateAgainstPlayer(te.getPos(), false);

			switch (getWorld().getBlockState(te.getPos()).getValue(BlockNullifyFrame.FACING)) {
				case UP:
					RenderUtils.renderFluid(fluid, te.getPos(), 0.16d, 0.92d, 0.16d, 0.0d, 0.0d, 0.0d, 0.67d, 0.05d, 0.67d, color, still, flowing);
					break;
				case DOWN:
					RenderUtils.renderFluid(fluid, te.getPos(), 0.16d, 0.03d, 0.16d, 0.0d, 0.0d, 0.0d, 0.67d, 0.05d, 0.67d, color, still, flowing);
					break;
				case NORTH: //done
					RenderUtils.renderFluid(fluid, te.getPos(), 0.16d, 0.16d, 0.03d, 0.0d, 0.0d, 0.0d, 0.67d, 0.67d, 0.05d, color, still, flowing);
					break;
				case EAST:
					RenderUtils.renderFluid(fluid, te.getPos(), 0.92d, 0.16d, 0.16d, 0.0d, 0.0d, 0.0d, 0.05d, 0.67d, 0.67d, color, still, flowing);
					break;
				case WEST: //done
					RenderUtils.renderFluid(fluid, te.getPos(), 0.03d, 0.16d, 0.16d, 0.0d, 0.0d, 0.0d, 0.05d, 0.67d, 0.67d, color, still, flowing);
					break;
				case SOUTH: //done
					RenderUtils.renderFluid(fluid, te.getPos(), 0.16d, 0.16d, 0.92d, 0.0d, 0.0d, 0.0d, 0.67d, 0.67d, 0.05d, color, still, flowing);
					break;
			}
			GlStateManager.disableBlend();
			GlStateManager.popMatrix();
		}
	}

	private void translateAgainstPlayer(BlockPos pos, boolean offset) {

		final float x = (float) (pos.getX() - TileEntityRendererDispatcher.staticPlayerX);
		final float y = (float) (pos.getY() - TileEntityRendererDispatcher.staticPlayerY);
		final float z = (float) (pos.getZ() - TileEntityRendererDispatcher.staticPlayerZ);

		if (offset) {
			GlStateManager.translate(x + 0.5, y + 0.5, z + 0.5);
		} else {
			GlStateManager.translate(x, y, z);
		}
	}
}
