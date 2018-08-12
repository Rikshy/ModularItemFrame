package de.shyrik.modularitemframe.common.module;

import de.shyrik.modularitemframe.ModularItemFrame;
import de.shyrik.modularitemframe.api.ConfigValues;
import de.shyrik.modularitemframe.api.ModuleFrameBase;
import de.shyrik.modularitemframe.api.utils.RenderUtils;
import de.shyrik.modularitemframe.client.render.FrameRenderer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;

import javax.annotation.Nonnull;

public class ModuleItem extends ModuleFrameBase {

	private static final String NBT_DISPLAY = "display";
	private static final String NBT_ROTATION = "rotation";

	public int rotation = 0;
	public ItemStack displayItem = ItemStack.EMPTY;

	@Nonnull
	public ResourceLocation frontTexture() {
		return new ResourceLocation(ModularItemFrame.MOD_ID, "blocks/item_frame_bg");
	}

	@Override
	public String getModuleName() {
		return I18n.format("modularitemframe.module.item");
	}

	protected float scale = 0.9f;
	protected float offset = 0.05F;

	public void rotate(EntityPlayer player) {
		if (player.isSneaking()) {
			rotation += 20;
		} else {
			rotation -= 20;
		}
		if (rotation >= 360 || rotation <= -360) rotation = 0;
		tile.markDirty();
	}

	@Override
	public void specialRendering(FrameRenderer tesr, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		GlStateManager.pushMatrix();
		GlStateManager.translate(x + 0.5D, y + 0.5D, z + 0.5D);
		GlStateManager.scale(scale, scale, scale);
		GlStateManager.pushMatrix();

		RenderUtils.renderItem(displayItem, tile.blockFacing(), rotation, offset);

		GlStateManager.popMatrix();
		GlStateManager.popMatrix();
	}

	public void screw(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull EntityPlayer playerIn, ItemStack driver) {
		if (!world.isRemote) {
			rotate(playerIn);
			tile.markDirty();
		}
	}

	@Override
	public boolean onBlockActivated(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull EntityPlayer playerIn, @Nonnull EnumHand hand, @Nonnull EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (playerIn instanceof FakePlayer && !ConfigValues.AllowFakePlayers) return false;

		if (!worldIn.isRemote) {
			if (!playerIn.isSneaking()) {
				ItemStack copy = playerIn.getHeldItem(hand).copy();
				copy.setCount(1);
				displayItem = copy;
				tile.markDirty();
			}
		}
		return true;
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound compound = new NBTTagCompound();
		compound.setTag(NBT_DISPLAY, displayItem.serializeNBT());
		compound.setInteger("rotation", rotation);
		return compound;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		if (nbt.hasKey(NBT_DISPLAY)) displayItem = new ItemStack(nbt.getCompoundTag(NBT_DISPLAY));
		if (nbt.hasKey(NBT_ROTATION)) rotation = nbt.getInteger(NBT_ROTATION);
	}
}
