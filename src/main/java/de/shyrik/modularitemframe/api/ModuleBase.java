package de.shyrik.modularitemframe.api;

import de.shyrik.modularitemframe.client.render.FrameRenderer;
import de.shyrik.modularitemframe.common.block.BlockModularFrame;
import de.shyrik.modularitemframe.common.module.t1.ModuleItem;
import de.shyrik.modularitemframe.common.tile.TileModularFrame;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.ProbeMode;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public abstract class ModuleBase implements INBTSerializable<NBTTagCompound> {

	protected TileModularFrame tile;

	public void setTile(TileModularFrame te) {
		tile = te;
	}

	/**
	 * Is called when the {@link FrameRenderer} wants to render the module for the first time.
	 * @see #bakeModel(IModel)
	 *
	 * @return [Nonnull] {@link ResourceLocation} to the Texture
	 */
	@Nonnull
	@SideOnly(Side.CLIENT)
	public abstract ResourceLocation frontTexture();

    /**
     * Is called when the {@link FrameRenderer} wants to render the module for the first time.
     * @see #bakeModel(IModel)
     *
     * @return [Nonnull] {@link ResourceLocation} to the Texture
     */
    @Nonnull
	@SideOnly(Side.CLIENT)
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
	@SideOnly(Side.CLIENT)
    public ResourceLocation backTexture() {
        return new ResourceLocation("minecraft", "blocks/log_birch_top");
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
	@SideOnly(Side.CLIENT)
	public IBakedModel bakeModel(IModel model) {
		if (bakedModel == null || reloadModel) {
			bakedModel = model.bake(model.getDefaultState(), DefaultVertexFormats.ITEM, location -> {
				if (location.getPath().contains("default_front"))
					return Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(frontTexture().toString());
				if (location.getPath().contains("default_back"))
					return Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(backTexture().toString());
				if (location.getPath().contains("default_inner"))
				    return Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(innerTexture().toString());
				return Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(location.toString());
			});
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
	@SideOnly(Side.CLIENT)
	public void specialRendering(FrameRenderer tesr, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
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

	/**
	 * The One Probe information handling
	 */
	@SideOnly(Side.CLIENT)
	@Optional.Method(modid = "theoneprobe")
	public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world, IBlockState blockState, IProbeHitData data) {
		probeInfo.horizontal().text(I18n.format("modularitemframe.tooltip.module", getModuleName()));
	}

	/**
	 * Waila/Hwyla information handling
	 */
	@Nonnull
	@SideOnly(Side.CLIENT)
	@Optional.Method(modid = "waila")
	public List<String> getWailaBody(ItemStack itemStack, IWailaDataAccessor accessor, IWailaConfigHandler config) {
		List<String> tips = new ArrayList<>();
		tips.add(I18n.format("modularitemframe.tooltip.module", getModuleName()));
		return tips;
	}

	@Nonnull
	public NBTTagCompound writeUpdateNBT(@Nonnull NBTTagCompound cmp) {
	    return cmp;
	}

	@SideOnly(Side.CLIENT)
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
