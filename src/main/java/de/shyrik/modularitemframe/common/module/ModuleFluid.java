package de.shyrik.modularitemframe.common.module;

import com.teamwizardry.librarianlib.features.base.block.tile.module.SerializableFluidTank;
import de.shyrik.modularitemframe.ModularItemFrame;
import de.shyrik.modularitemframe.api.ModuleFrameBase;
import de.shyrik.modularitemframe.api.utils.RenderUtils;
import de.shyrik.modularitemframe.client.render.FrameRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;

public abstract class ModuleFluid extends ModuleFrameBase {

	private static final String NBT_TANK = "tank";

	public SerializableFluidTank tank = new SerializableFluidTank(1000);

	@Nonnull
	@Override
	public ResourceLocation frontTexture() {
		return new ResourceLocation(ModularItemFrame.MOD_ID, "blocks/tank_frame_bg");
	}

	@Override
	public void specialRendering(FrameRenderer tesr, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		if (tank != null && tank.getFluid() != null && tank.getFluidAmount() > 0) {
			GlStateManager.pushMatrix();
			GlStateManager.enableBlend();

			FluidStack fluid = tank.getFluid();
			double amount = (double) tank.getFluidAmount() / (double) tank.getCapacity();
			int color = fluid.getFluid().getColor(fluid);
			final TextureAtlasSprite still = Minecraft.getMinecraft().getTextureMapBlocks().getTextureExtry(fluid.getFluid().getStill(fluid).toString());
			final TextureAtlasSprite flowing = Minecraft.getMinecraft().getTextureMapBlocks().getTextureExtry(fluid.getFluid().getFlowing(fluid).toString());

			RenderUtils.translateAgainstPlayer(tile.getPos(), false);

			switch (tile.blockFacing()) {
				case UP:
					RenderUtils.renderFluid(fluid, tile.getPos(), 0.16d, 0.92d, 0.16d, 0.0d, 0.0d, 0.0d, 0.67d, 0.05d, amount * 0.67d, color, still, flowing);
					break;
				case DOWN:
					RenderUtils.renderFluid(fluid, tile.getPos(), 0.16d, 0.03d, 0.16d, 0.0d, 0.0d, 0.0d, 0.67d, 0.05d, amount * 0.67d, color, still, flowing);
					break;
				case NORTH: //done
					RenderUtils.renderFluid(fluid, tile.getPos(), 0.16d, 0.16d, 0.03d, 0.0d, 0.0d, 0.0d, 0.67d, amount * 0.67d, 0.05d, color, still, flowing);
					break;
				case EAST:
					RenderUtils.renderFluid(fluid, tile.getPos(), 0.92d, 0.16d, 0.16d, 0.0d, 0.0d, 0.0d, 0.05d, amount * 0.67d, 0.67d, color, still, flowing);
					break;
				case WEST: //done
					RenderUtils.renderFluid(fluid, tile.getPos(), 0.03d, 0.16d, 0.16d, 0.0d, 0.0d, 0.0d, 0.05d, amount * 0.67d, 0.67d, color, still, flowing);
					break;
				case SOUTH: //done
					RenderUtils.renderFluid(fluid, tile.getPos(), 0.16d, 0.16d, 0.92d, 0.0d, 0.0d, 0.0d, 0.67d, amount * 0.67d, 0.05d, color, still, flowing);
					break;
			}
			GlStateManager.disableBlend();
			GlStateManager.popMatrix();
		}
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound compound = super.serializeNBT();
		compound.setTag(NBT_TANK, tank.serializeNBT());
		return compound;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		super.deserializeNBT(nbt);
		if (nbt.hasKey(NBT_TANK)) tank.deserializeNBT(nbt.getCompoundTag(NBT_TANK));
	}
}
