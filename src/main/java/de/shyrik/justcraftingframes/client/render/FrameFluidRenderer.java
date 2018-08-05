package de.shyrik.justcraftingframes.client.render;

import de.shyrik.justcraftingframes.common.block.BlockNullifyFrame;
import de.shyrik.justcraftingframes.common.tile.TileFluidBaseFrame;
import de.shyrik.justcraftingframes.common.utils.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.FluidStack;
import org.lwjgl.opengl.GL11;

public class FrameFluidRenderer extends TileEntitySpecialRenderer<TileFluidBaseFrame> {

	@Override
	public void render(TileFluidBaseFrame te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		if (te != null && te.tank != null && te.tank.getFluid() != null && te.tank.getFluidAmount() > 0) {
			GlStateManager.pushMatrix();
			GlStateManager.enableBlend();

			FluidStack fluid = te.tank.getFluid();
			double amount = (double) te.tank.getFluidAmount() / (double) te.tank.getCapacity();
			int color = fluid.getFluid().getColor(fluid);
			final TextureAtlasSprite still = Minecraft.getMinecraft().getTextureMapBlocks().getTextureExtry(fluid.getFluid().getStill(fluid).toString());
			final TextureAtlasSprite flowing = Minecraft.getMinecraft().getTextureMapBlocks().getTextureExtry(fluid.getFluid().getFlowing(fluid).toString());

			translateAgainstPlayer(te.getPos(), false);

			switch (getWorld().getBlockState(te.getPos()).getValue(BlockNullifyFrame.FACING)) {
				case UP:
					renderFluid(fluid, te.getPos(), 0.16d, 0.92d, 0.16d, 0.0d, 0.0d, 0.0d, 0.67d, 0.05d, amount * 0.67d, color, still, flowing);
					break;
				case DOWN:
					renderFluid(fluid, te.getPos(), 0.16d, 0.03d, 0.16d, 0.0d, 0.0d, 0.0d, 0.67d, 0.05d,  amount * 0.67d, color, still, flowing);
					break;
				case NORTH: //done
					renderFluid(fluid, te.getPos(), 0.16d, 0.16d, 0.03d, 0.0d, 0.0d, 0.0d, 0.67d, amount * 0.67d, 0.05d, color, still, flowing);
					break;
				case EAST:
					renderFluid(fluid, te.getPos(), 0.92d, 0.16d, 0.16d, 0.0d, 0.0d, 0.0d, 0.05d, amount * 0.67d, 0.67d, color, still, flowing);
					break;
				case WEST: //done
					renderFluid(fluid, te.getPos(), 0.03d, 0.16d, 0.16d, 0.0d, 0.0d, 0.0d, 0.05d, amount * 0.67d, 0.67d, color, still, flowing);
					break;
				case SOUTH: //done
					renderFluid(fluid, te.getPos(), 0.16d, 0.16d, 0.92d, 0.0d, 0.0d, 0.0d, 0.67d, amount * 0.67d, 0.05d, color, still, flowing);
					break;
			}
			GlStateManager.disableBlend();
			GlStateManager.popMatrix();
		}
	}

	private void renderFluid(FluidStack fluid, BlockPos pos, double x, double y, double z, double x1, double y1, double z1, double x2, double y2, double z2, int color, TextureAtlasSprite top, TextureAtlasSprite side) {

		final Minecraft mc = Minecraft.getMinecraft();
		final Tessellator tessellator = Tessellator.getInstance();
		final BufferBuilder buffer = tessellator.getBuffer();
		final int brightness = mc.world.getCombinedLight(pos, fluid.getFluid().getLuminosity());

		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
		mc.renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

		GlStateManager.pushMatrix();
		RenderHelper.disableStandardItemLighting();
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		if (Minecraft.isAmbientOcclusionEnabled()) {
			GL11.glShadeModel(GL11.GL_SMOOTH);
		} else {
			GL11.glShadeModel(GL11.GL_FLAT);
		}
		GlStateManager.translate(x, y, z);

		Utils.addTexturedQuad(buffer, top, x1, y1, z1, x2 - x1, y2 - y1, z2 - z1, EnumFacing.DOWN, color, brightness);
		Utils.addTexturedQuad(buffer, side, x1, y1, z1, x2 - x1, y2 - y1, z2 - z1, EnumFacing.NORTH, color, brightness);
		Utils.addTexturedQuad(buffer, side, x1, y1, z1, x2 - x1, y2 - y1, z2 - z1, EnumFacing.EAST, color, brightness);
		Utils.addTexturedQuad(buffer, side, x1, y1, z1, x2 - x1, y2 - y1, z2 - z1, EnumFacing.SOUTH, color, brightness);
		Utils.addTexturedQuad(buffer, side, x1, y1, z1, x2 - x1, y2 - y1, z2 - z1, EnumFacing.WEST, color, brightness);
		Utils.addTexturedQuad(buffer, top, x1, y1, z1, x2 - x1, y2 - y1, z2 - z1, EnumFacing.UP, color, brightness);
		tessellator.draw();

		GlStateManager.disableBlend();
		GlStateManager.popMatrix();
		RenderHelper.enableStandardItemLighting();
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
