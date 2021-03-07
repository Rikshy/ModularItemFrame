package modularitemframe.api;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import modularitemframe.api.accessors.IFrameRenderer;
import modularitemframe.api.accessors.IFrameTile;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.TextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.INBTSerializable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class ModuleBase implements INBTSerializable<CompoundNBT> {
	protected IFrameTile frame;
	ModuleItem item;

	public void setFrame(IFrameTile te) {
		frame = te;
	}

	public ModuleItem getItem() { return item; }

	/**
	 * @return unique ID the module gets registered with.
	 */
	@NotNull
	public abstract ResourceLocation getId();

	/**
	 * TOP and WAILA are using this for display
	 *
	 * @return the name of the module :O
	 */
	@OnlyIn(Dist.CLIENT)
	public abstract TextComponent getName();

	/**
	 * Represents the module tier. Defines the inner texture of the frame.
	 *
	 * @return [Nonnull] {@link ModuleTier} to the Texture
	 */
	@NotNull
	public abstract ModuleTier moduleTier();

	/**
	 * Is called when the {@link IFrameRenderer} wants to render the module for the first time.
	 *
	 * @return [Nonnull] {@link ResourceLocation} to the Texture
	 */
	@NotNull
	@OnlyIn(Dist.CLIENT)
	public abstract ResourceLocation frontTexture();

    /**
     * Is called when the {@link IFrameRenderer} wants to render the module for the first time.
     *
     * @return [Nonnull] {@link ResourceLocation} to the Texture
     */
    @NotNull
	@OnlyIn(Dist.CLIENT)
    public ResourceLocation backTexture() {
        return new ResourceLocation("minecraft", "block/stripped_birch_log_top");
    }

	/**
	 * Append tooltip information for waila
	 */
	@OnlyIn(Dist.CLIENT)
	public void appendTooltips(List<TextComponent> tooltips) {
	}

	/**
	 * Override this if you want to register multiple backgrounds for the module.
	 *
	 * @return list of resource locations as module backgrounds
	 */
	public List<ResourceLocation> getVariantFronts() {
		return ImmutableList.of(frontTexture());
	}

	/**
	 * Called by the {@link IFrameRenderer} after rendering the frame.
	 * Extra rendering can be don here
	 * like the {@link ModuleItem ModuleItem} does the item thing)
	 *
	 * @param renderer instance of the current {@link IFrameRenderer}
	 */
	@OnlyIn(Dist.CLIENT)
	public void specialRendering(@NotNull IFrameRenderer renderer, float partialTicks, @NotNull MatrixStack matrixStack, @NotNull IRenderTypeBuffer buffer, int light, int overlay) {
	}

	/**
	 * Called when the frame got left clicked
	 */
	public void onBlockClicked(@NotNull World worldIn, @NotNull BlockPos pos, @NotNull PlayerEntity playerIn) {
	}

	/**
	 * Called when a Screwdriver in interaction mode clicks a frame
	 * Implement behavior for Screwdriver interaction here
	 *
	 * @param driver the driver who was used
	 */
	public void screw(@NotNull World world, @NotNull BlockPos pos, @NotNull PlayerEntity playerIn, ItemStack driver) {

	}

	/**
	 * Called when the frames upgrades change.
	 */
	public void onFrameUpgradesChanged(World world, BlockPos pos, Direction facing) {

	}

	/**
	 * Called when a frame is simply right clicked
	 */
	public ActionResultType onUse(@NotNull World world, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull PlayerEntity player, @NotNull Hand hand, @NotNull Direction facing, BlockRayTraceResult hit) {
		return ActionResultType.FAIL;
	}

	/**
	 * called when the tile entity ticks
	 */
	public void tick(@NotNull World world, @NotNull BlockPos pos) {
	}

	/**
	 * Called when module is added to a frame.
	 */
	public void onInsert(@NotNull World world, @NotNull BlockPos pos, @NotNull Direction facing, @NotNull PlayerEntity player, @NotNull ItemStack moduleStack) {
	}

	/**
	 * Called when module is removed with the Screwdriver
	 * or destroyed.
	 */
	public void onRemove(@NotNull World world, @NotNull BlockPos pos, @NotNull Direction facing, @Nullable PlayerEntity player, @NotNull ItemStack moduleStack) {
	}

	/**
	 * NBT serialization in case there are some data to be saved!
	 * this gets synced automatically
	 */
	@NotNull
	@Override
	public CompoundNBT serializeNBT() {
		return new CompoundNBT();
	}

	/**
	 * NBT deserialization in case there are some data to be saved!
	 */
	@Override
	public void deserializeNBT(CompoundNBT nbtTagCompound) {
	}

	//region <helper>
	/**
	 * Helper method which safe checks ticks.
	 */
	public boolean canTick(World world, int base, int mod) {
		return world.getGameTime() % Math.max(base - mod * frame.getSpeedUpCount(), 10) == 0;
	}

	/**
	 * Forwarded from blockEntity. initiates data sync.
	 */
	public void markDirty() {
		frame.markDirty();
	}

	/**
	 * helper method for default world interaction range.
	 */
	protected AxisAlignedBB getScanBox() {
		BlockPos pos = frame.getPos();
		int range = frame.getRangeUpCount() + frame.getConfig().getBaseScanRadius();
		switch (frame.getFacing()) {
			case DOWN:
				return new AxisAlignedBB(pos.add(-range + 1, 1, -range + 1), pos.add(range, -range + 1, range));
			case UP:
				return new AxisAlignedBB(pos.add(-range + 1, 0, -range + 1), pos.add(range, range, range));
			case NORTH:
				return new AxisAlignedBB(pos.add(-range + 1, -range + 1, 1), pos.add(range, range, -range + 1));
			case SOUTH:
				return new AxisAlignedBB(pos.add(-range + 1, -range + 1, 0), pos.add(range, range, range));
			case WEST:
				return new AxisAlignedBB(pos.add(1, -range + 1, -range + 1), pos.add(-range + 1, range, range));
			case EAST:
				return new AxisAlignedBB(pos.add(0, -range + 1, -range + 1), pos.add(range, range, range));
		}
		return new AxisAlignedBB(pos, pos.add(1, 1, 1));
	}
	//endregion
}
