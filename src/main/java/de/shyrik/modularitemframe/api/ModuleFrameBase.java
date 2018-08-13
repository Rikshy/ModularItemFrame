package de.shyrik.modularitemframe.api;

import de.shyrik.modularitemframe.client.render.FrameRenderer;
import de.shyrik.modularitemframe.common.container.ContainerCraftingFrame;
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
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public abstract class ModuleFrameBase implements INBTSerializable<NBTTagCompound> {

	protected TileModularFrame tile;

	public void setTile(TileModularFrame te) {
		tile = te;
	}

	@Nonnull
	public abstract ResourceLocation frontTexture();

	@Nonnull
	public ResourceLocation backTexture() {
		return new ResourceLocation("minecraft", "blocks/log_birch_top");
	}

	public abstract String getModuleName();

	public boolean reloadModel = false;
	private IBakedModel bakedModel = null;

	public IBakedModel bakeModel(IModel model) {
		if (bakedModel == null || reloadModel) {
			bakedModel = model.bake(model.getDefaultState(), DefaultVertexFormats.ITEM, location -> {
				if (location.getResourcePath().contains("default_front"))
					return Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(frontTexture().toString());
				if (location.getResourcePath().contains("default_back"))
					return Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(backTexture().toString());
				return Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(location.toString());
			});
			reloadModel = false;
		}
		return bakedModel;
	}

	public void specialRendering(FrameRenderer tesr, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
	}

	public void onBlockClicked(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull EntityPlayer playerIn) {
	}

	public boolean hasScrewInteraction() {
		return true;
	}

	public void screw(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull EntityPlayer playerIn, ItemStack driver) {

	}

	public abstract boolean onBlockActivated(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull EntityPlayer playerIn, @Nonnull EnumHand hand, @Nonnull EnumFacing facing, float hitX, float hitY, float hitZ);

	public ContainerCraftingFrame createContainer(final EntityPlayer player) {
		return null;
	}

	public void tick(@Nonnull World world, @Nonnull BlockPos pos) {
	}

	public void onRemove(@NotNull World worldIn, @NotNull BlockPos pos, @Nullable EntityPlayer playerIn) {

	}

	@Optional.Method(modid = "theoneprobe")
	public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world, IBlockState blockState, IProbeHitData data) {
		probeInfo.horizontal().text(I18n.format("modularitemframe.tooltip.module", getModuleName()));
	}

	@Nonnull
	@Optional.Method(modid = "waila")
	public List<String> getWailaBody(ItemStack itemStack, IWailaDataAccessor accessor, IWailaConfigHandler config) {
		List<String> tips = new ArrayList<>();
		tips.add(I18n.format("modularitemframe.tooltip.module", getModuleName()));
		return tips;
	}

	@Override
	public NBTTagCompound serializeNBT() {
		return new NBTTagCompound();
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbtTagCompound) {

	}
}
