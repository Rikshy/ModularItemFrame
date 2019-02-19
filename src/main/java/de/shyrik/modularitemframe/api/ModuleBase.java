package de.shyrik.modularitemframe.api;

import de.shyrik.modularitemframe.client.render.FrameRenderer;
import de.shyrik.modularitemframe.common.block.BlockModularFrame;
import de.shyrik.modularitemframe.common.module.t1.ModuleItem;
import de.shyrik.modularitemframe.common.block.TileModularFrame;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class ModuleBase implements INBTSerializable<NBTTagCompound> {

	protected TileModularFrame tile;
	ItemModule parent;

	public void setTile(TileModularFrame te) {
		tile = te;
	}

	public ItemModule getParent() { return parent; }

	public abstract ResourceLocation getId();

	/**
	 * Is called when the {@link FrameRenderer} wants to render the module for the first time.
	 * @see #bakeModel(IModel)
	 *
	 * @return [Nonnull] {@link ResourceLocation} to the Texture
	 */
	@Nonnull
	@OnlyIn(Dist.CLIENT)
	public abstract ResourceLocation frontTexture();

    /**
     * Is called when the {@link FrameRenderer} wants to render the module for the first time.
     * @see #bakeModel(IModel)
     *
     * @return [Nonnull] {@link ResourceLocation} to the Texture
     */
    @Nonnull
	@OnlyIn(Dist.CLIENT)
    public ResourceLocation innerTexture() {
        return BlockModularFrame.INNER_DEF_LOC;
    }

    /**
     * Is called when the {@link FrameRenderer} wants to render the module for the first time.
     * @see #bakeModel(IModel)
     *
     * @return [Nonnull] {@link ResourceLocation} to the Texture
     */
    @Nonnull
	@OnlyIn(Dist.CLIENT)
    public ResourceLocation backTexture() {
        return new ResourceLocation("minecraft", "block/log_birch_top");
    }

	/**
	 * TOP and WAILA are using this for display
	 * Please use translation holders - raw strings are bad!
	 *
	 * @return the name of the module :O
	 */
	public abstract String getModuleName();

	public boolean reloadModel = false;
	private IBakedModel bakedModel = null;

	/**
	 * Called by the {@link FrameRenderer} to bake the Frame model
	 * by default {@link #frontTexture()} and {@link #backTexture()} will be asked to be replaced
	 * override this with caution.
	 *
	 * @param model Contains the model of the frame
	 * @return baked model ofc
	 */
	@OnlyIn(Dist.CLIENT)
	public IBakedModel bakeModel(IModel model) {
		if (bakedModel == null || reloadModel) {
			model.bake(resourceLocation -> model, loc -> {
				if (loc.toString().contains("default_front"))
					return Minecraft.getInstance().getTextureMap().getSprite(frontTexture());
				if (loc.toString().contains("default_back"))
					return Minecraft.getInstance().getTextureMap().getSprite(backTexture());
				if (loc.toString().contains("default_inner"))
					return Minecraft.getInstance().getTextureMap().getSprite(innerTexture());
				return Minecraft.getInstance().getTextureMap().getSprite((ResourceLocation) loc);
			}, model.getDefaultState(), false, DefaultVertexFormats.BLOCK);

			reloadModel = false;
		}
		return bakedModel;
	}

	/**
	 * Called by the {@link FrameRenderer} after rendering the frame.
	 * Extra rendering can be don here
	 * like the {@link ModuleItem ModuleItem} does the item thing)
	 *
	 * @param tesr instance of the current {@link FrameRenderer}
	 */
	@OnlyIn(Dist.CLIENT)
	public void specialRendering(FrameRenderer tesr, double x, double y, double z, float partialTicks, int destroyStage) {
	}

	/**
	 * Called when the frame got left clicked
	 */
	public void onBlockClicked(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull EntityPlayer playerIn) {
	}

	/**
	 * Called when a {@link de.shyrik.modularitemframe.common.item.ItemScrewdriver screwdriver} in interaction mode clicks a frame
	 * Implement behavior for {@link de.shyrik.modularitemframe.common.item.ItemScrewdriver screwdriver} interaction here
	 *
	 * @param driver the driver who was used
	 */
	public void screw(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull EntityPlayer playerIn, ItemStack driver) {

	}

	public void onFrameUpgradesChanged() {

	}

	/**
	 * Called when a frame is simply right clicked
	 */
	public abstract boolean onBlockActivated(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull EntityPlayer playerIn, @Nonnull EnumHand hand, @Nonnull EnumFacing facing, float hitX, float hitY, float hitZ);

	/**
	 * in case your module has a gui
	 */
	public Container createContainer(final EntityPlayer player) {
		return null;
	}

	/**
	 * called when the tile entity ticks
	 */
	public void tick(@Nonnull World world, @Nonnull BlockPos pos) {
	}

	/**
	 * Called when module is removed with the {@link de.shyrik.modularitemframe.common.item.ItemScrewdriver screwdriver}
	 * or destroyed.
	 */
	public void onRemove(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull EnumFacing facing, @Nullable EntityPlayer playerIn) {
	}

	@Nonnull
	public NBTTagCompound writeUpdateNBT(@Nonnull NBTTagCompound cmp) {
	    return cmp;
	}

	public void readUpdateNBT(@Nonnull NBTTagCompound cmp) {

    }

	/**
	 * NBT serialization in case there are some data to be saved!
	 * this gets synced automatically
	 */
	@Nonnull
	@Override
	public NBTTagCompound serializeNBT() {
		return new NBTTagCompound();
	}

	/**
	 * NBT deserialization in case there are some data to be saved!
	 */
	@Override
	public void deserializeNBT(NBTTagCompound nbtTagCompound) {
	}
}
