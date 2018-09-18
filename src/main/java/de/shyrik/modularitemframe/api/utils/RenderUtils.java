package de.shyrik.modularitemframe.api.utils;

import de.shyrik.modularitemframe.client.render.FrameRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;
import java.nio.FloatBuffer;
import java.util.Random;
import java.util.function.Predicate;

@SideOnly(Side.CLIENT)
public class RenderUtils {

	private static void rotateItemOnFacing(@Nonnull EnumFacing facing, float rotation, float offset) {
		switch (facing) {
			case NORTH:
				break;
			case SOUTH:
				GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
				break;
			case WEST:
				GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
				break;
			case EAST:
				GlStateManager.rotate(-90.0F, 0.0F, 1.0F, 0.0F);
				break;
			case DOWN:
				GlStateManager.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
				break;
			case UP:
				GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
		}
		GlStateManager.rotate(rotation, 0.0F, 0.0F, 1.0F);
		GlStateManager.translate(0.0F, 0.0F, -0.5125F + offset);
	}

	public static void renderItem(ItemStack stack, @Nonnull EnumFacing facing, float rotation, float offset) {
		if (!stack.isEmpty()) {
			rotateItemOnFacing(facing, rotation, offset);
			RenderItem itemRenderer = Minecraft.getMinecraft().getRenderItem();

			GlStateManager.pushMatrix();
			GlStateManager.disableLighting();

			GlStateManager.scale(0.5F, 0.5F, 0.5F);
			GlStateManager.pushAttrib();
			RenderHelper.enableStandardItemLighting();
			if (itemRenderer.shouldRenderItemIn3D(stack)) {
				GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
			}
			itemRenderer.renderItem(stack, ItemCameraTransforms.TransformType.FIXED);
			RenderHelper.disableStandardItemLighting();
			GlStateManager.popAttrib();

			GlStateManager.enableLighting();
			GlStateManager.popMatrix();
		}
	}

	public static void renderFluid(FluidStack fluid, BlockPos pos, double x, double y, double z, double x1, double y1, double z1, double x2, double y2, double z2, int color, TextureAtlasSprite top, TextureAtlasSprite side) {

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

		addTexturedQuad(buffer, top, x1, y1, z1, x2 - x1, y2 - y1, z2 - z1, EnumFacing.DOWN, color, brightness);
		addTexturedQuad(buffer, side, x1, y1, z1, x2 - x1, y2 - y1, z2 - z1, EnumFacing.NORTH, color, brightness);
		addTexturedQuad(buffer, side, x1, y1, z1, x2 - x1, y2 - y1, z2 - z1, EnumFacing.EAST, color, brightness);
		addTexturedQuad(buffer, side, x1, y1, z1, x2 - x1, y2 - y1, z2 - z1, EnumFacing.SOUTH, color, brightness);
		addTexturedQuad(buffer, side, x1, y1, z1, x2 - x1, y2 - y1, z2 - z1, EnumFacing.WEST, color, brightness);
		addTexturedQuad(buffer, top, x1, y1, z1, x2 - x1, y2 - y1, z2 - z1, EnumFacing.UP, color, brightness);
		tessellator.draw();

		GlStateManager.disableBlend();
		GlStateManager.popMatrix();
		RenderHelper.enableStandardItemLighting();
	}


	private static void addTexturedQuad(BufferBuilder buffer, TextureAtlasSprite sprite, double x, double y, double z, double width, double height, double length, EnumFacing face, int color, int brightness) {

		if (sprite == null) {
			return;
		}

		final int light1 = brightness >> 0x10 & 0xFFFF;
		final int light2 = brightness & 0xFFFF;
		final int alpha = color >> 24 & 0xFF;
		final int red = color >> 16 & 0xFF;
		final int green = color >> 8 & 0xFF;
		final int blue = color & 0xFF;

		double minU;
		double maxU;
		double minV;
		double maxV;

		final double size = 16f;

		final double x2 = x + width;
		final double y2 = y + height;
		final double z2 = z + length;

		final double u = x % 1d;
		double u1 = u + width;

		while (u1 > 1f) {
			u1 -= 1f;
		}

		final double vy = y % 1d;
		double vy1 = vy + height;

		while (vy1 > 1f) {
			vy1 -= 1f;
		}

		final double vz = z % 1d;
		double vz1 = vz + length;

		while (vz1 > 1f) {
			vz1 -= 1f;
		}

		switch (face) {

			case DOWN:

			case UP:
				minU = sprite.getInterpolatedU(u * size);
				maxU = sprite.getInterpolatedU(u1 * size);
				minV = sprite.getInterpolatedV(vz * size);
				maxV = sprite.getInterpolatedV(vz1 * size);
				break;

			case NORTH:

			case SOUTH:
				minU = sprite.getInterpolatedU(u1 * size);
				maxU = sprite.getInterpolatedU(u * size);
				minV = sprite.getInterpolatedV(vy * size);
				maxV = sprite.getInterpolatedV(vy1 * size);
				break;

			case WEST:

			case EAST:
				minU = sprite.getInterpolatedU(vz1 * size);
				maxU = sprite.getInterpolatedU(vz * size);
				minV = sprite.getInterpolatedV(vy * size);
				maxV = sprite.getInterpolatedV(vy1 * size);
				break;

			default:
				minU = sprite.getMinU();
				maxU = sprite.getMaxU();
				minV = sprite.getMinV();
				maxV = sprite.getMaxV();
		}

		switch (face) {

			case DOWN:
				buffer.pos(x, y, z).color(red, green, blue, alpha).tex(minU, minV).lightmap(light1, light2).endVertex();
				buffer.pos(x2, y, z).color(red, green, blue, alpha).tex(maxU, minV).lightmap(light1, light2).endVertex();
				buffer.pos(x2, y, z2).color(red, green, blue, alpha).tex(maxU, maxV).lightmap(light1, light2).endVertex();
				buffer.pos(x, y, z2).color(red, green, blue, alpha).tex(minU, maxV).lightmap(light1, light2).endVertex();
				break;

			case UP:
				buffer.pos(x, y2, z).color(red, green, blue, alpha).tex(minU, minV).lightmap(light1, light2).endVertex();
				buffer.pos(x, y2, z2).color(red, green, blue, alpha).tex(minU, maxV).lightmap(light1, light2).endVertex();
				buffer.pos(x2, y2, z2).color(red, green, blue, alpha).tex(maxU, maxV).lightmap(light1, light2).endVertex();
				buffer.pos(x2, y2, z).color(red, green, blue, alpha).tex(maxU, minV).lightmap(light1, light2).endVertex();
				break;

			case NORTH:
				buffer.pos(x, y, z).color(red, green, blue, alpha).tex(minU, maxV).lightmap(light1, light2).endVertex();
				buffer.pos(x, y2, z).color(red, green, blue, alpha).tex(minU, minV).lightmap(light1, light2).endVertex();
				buffer.pos(x2, y2, z).color(red, green, blue, alpha).tex(maxU, minV).lightmap(light1, light2).endVertex();
				buffer.pos(x2, y, z).color(red, green, blue, alpha).tex(maxU, maxV).lightmap(light1, light2).endVertex();
				break;

			case SOUTH:
				buffer.pos(x, y, z2).color(red, green, blue, alpha).tex(maxU, maxV).lightmap(light1, light2).endVertex();
				buffer.pos(x2, y, z2).color(red, green, blue, alpha).tex(minU, maxV).lightmap(light1, light2).endVertex();
				buffer.pos(x2, y2, z2).color(red, green, blue, alpha).tex(minU, minV).lightmap(light1, light2).endVertex();
				buffer.pos(x, y2, z2).color(red, green, blue, alpha).tex(maxU, minV).lightmap(light1, light2).endVertex();
				break;

			case WEST:
				buffer.pos(x, y, z).color(red, green, blue, alpha).tex(maxU, maxV).lightmap(light1, light2).endVertex();
				buffer.pos(x, y, z2).color(red, green, blue, alpha).tex(minU, maxV).lightmap(light1, light2).endVertex();
				buffer.pos(x, y2, z2).color(red, green, blue, alpha).tex(minU, minV).lightmap(light1, light2).endVertex();
				buffer.pos(x, y2, z).color(red, green, blue, alpha).tex(maxU, minV).lightmap(light1, light2).endVertex();
				break;

			case EAST:
				buffer.pos(x2, y, z).color(red, green, blue, alpha).tex(minU, maxV).lightmap(light1, light2).endVertex();
				buffer.pos(x2, y2, z).color(red, green, blue, alpha).tex(minU, minV).lightmap(light1, light2).endVertex();
				buffer.pos(x2, y2, z2).color(red, green, blue, alpha).tex(maxU, minV).lightmap(light1, light2).endVertex();
				buffer.pos(x2, y, z2).color(red, green, blue, alpha).tex(maxU, maxV).lightmap(light1, light2).endVertex();
				break;
		}
	}

	public static void translateAgainstPlayer(BlockPos pos, boolean offset) {

		final float x = (float) (pos.getX() - TileEntityRendererDispatcher.staticPlayerX);
		final float y = (float) (pos.getY() - TileEntityRendererDispatcher.staticPlayerY);
		final float z = (float) (pos.getZ() - TileEntityRendererDispatcher.staticPlayerZ);

		if (offset) {
			GlStateManager.translate(x + 0.5, y + 0.5, z + 0.5);
		} else {
			GlStateManager.translate(x, y, z);
		}
	}

	private static final ResourceLocation END_SKY_TEXTURE = new ResourceLocation("textures/environment/end_sky.png");
	private static final ResourceLocation END_PORTAL_TEXTURE = new ResourceLocation("textures/entity/end_portal.png");
	private static final Random RANDOM = new Random(31100L);
	private static final FloatBuffer MODELVIEW = GLAllocation.createDirectFloatBuffer(16);
	private static final FloatBuffer PROJECTION = GLAllocation.createDirectFloatBuffer(16);
	private static FloatBuffer buffer = GLAllocation.createDirectFloatBuffer(16);

	public static class RenderEndPosInfo {
        public RenderEndPosInfo(BufferBuilder buffer, float color1, float color2, float color3) {
            this.buffer = buffer;
            this.color1 = color1;
            this.color2 = color2;
            this.color3 = color3;
        }

        public BufferBuilder buffer;
	    public float color1, color2, color3;
    }

	public static void renderEnd(FrameRenderer tesr, double x, double y, double z, Predicate<RenderEndPosInfo> drawPos) {
		GlStateManager.disableLighting();
		RANDOM.setSeed(31100L);
		GlStateManager.getFloat(2982, MODELVIEW);
		GlStateManager.getFloat(2983, PROJECTION);
		double d0 = x * x + y * y + z * z;
		int i = getPasses(d0);
		boolean flag = false;

		for (int j = 0; j < i; ++j) {
			GlStateManager.pushMatrix();
			float f1 = 2.0F / (float) (18 - j);

			if (j == 0) {
				tesr.bindTex(END_SKY_TEXTURE);
				f1 = 0.15F;
				GlStateManager.enableBlend();
				GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
			}

			if (j >= 1) {
				tesr.bindTex(END_PORTAL_TEXTURE);
				flag = true;
				Minecraft.getMinecraft().entityRenderer.setupFogColor(true);
			}

			if (j == 1) {
				GlStateManager.enableBlend();
				GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
			}

			GlStateManager.texGen(GlStateManager.TexGen.S, 9216);
			GlStateManager.texGen(GlStateManager.TexGen.T, 9216);
			GlStateManager.texGen(GlStateManager.TexGen.R, 9216);
			GlStateManager.texGen(GlStateManager.TexGen.S, 9474, getBuffer(1.0F, 0.0F, 0.0F, 0.0F));
			GlStateManager.texGen(GlStateManager.TexGen.T, 9474, getBuffer(0.0F, 1.0F, 0.0F, 0.0F));
			GlStateManager.texGen(GlStateManager.TexGen.R, 9474, getBuffer(0.0F, 0.0F, 1.0F, 0.0F));
			GlStateManager.enableTexGenCoord(GlStateManager.TexGen.S);
			GlStateManager.enableTexGenCoord(GlStateManager.TexGen.T);
			GlStateManager.enableTexGenCoord(GlStateManager.TexGen.R);
			GlStateManager.popMatrix();
			GlStateManager.matrixMode(5890);
			GlStateManager.pushMatrix();
			GlStateManager.loadIdentity();
			GlStateManager.translate(0.5F, 0.5F, 0.0F);
			GlStateManager.scale(0.5F, 0.5F, 1.0F);
			float f2 = (float) (j + 1);
			GlStateManager.translate(17.0F / f2, (2.0F + f2 / 1.5F) * ((float) Minecraft.getSystemTime() % 800000.0F / 800000.0F), 0.0F);
			GlStateManager.rotate((f2 * f2 * 4321.0F + f2 * 9.0F) * 2.0F, 0.0F, 0.0F, 1.0F);
			GlStateManager.scale(4.5F - f2 / 4.0F, 4.5F - f2 / 4.0F, 1.0F);
			GlStateManager.multMatrix(PROJECTION);
			GlStateManager.multMatrix(MODELVIEW);
			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder buffer = tessellator.getBuffer();
			buffer.begin(7, DefaultVertexFormats.POSITION_COLOR);
			float f3 = (RANDOM.nextFloat() * 0.5F + 0.1F) * f1;
			float f4 = (RANDOM.nextFloat() * 0.5F + 0.4F) * f1;
			float f5 = (RANDOM.nextFloat() * 0.5F + 0.5F) * f1;

			drawPos.test(new RenderEndPosInfo(buffer, f3, f4, f5));

			tessellator.draw();
			GlStateManager.popMatrix();
			GlStateManager.matrixMode(5888);
			tesr.bindTex(END_SKY_TEXTURE);
		}

		GlStateManager.disableBlend();
		GlStateManager.disableTexGenCoord(GlStateManager.TexGen.S);
		GlStateManager.disableTexGenCoord(GlStateManager.TexGen.T);
		GlStateManager.disableTexGenCoord(GlStateManager.TexGen.R);
		GlStateManager.enableLighting();

		if (flag) {
			Minecraft.getMinecraft().entityRenderer.setupFogColor(false);
		}
	}

	private static int getPasses(double p_191286_1_) {
		int i;

		if (p_191286_1_ > 36864.0D) {
			i = 1;
		} else if (p_191286_1_ > 25600.0D) {
			i = 3;
		} else if (p_191286_1_ > 16384.0D) {
			i = 5;
		} else if (p_191286_1_ > 9216.0D) {
			i = 7;
		} else if (p_191286_1_ > 4096.0D) {
			i = 9;
		} else if (p_191286_1_ > 1024.0D) {
			i = 11;
		} else if (p_191286_1_ > 576.0D) {
			i = 13;
		} else if (p_191286_1_ > 256.0D) {
			i = 14;
		} else {
			i = 15;
		}

		return i;
	}

	private static FloatBuffer getBuffer(float p_147525_1_, float p_147525_2_, float p_147525_3_, float p_147525_4_) {
		buffer.clear();
		buffer.put(p_147525_1_).put(p_147525_2_).put(p_147525_3_).put(p_147525_4_);
		buffer.flip();
		return buffer;
	}
}
