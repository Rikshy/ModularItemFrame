package de.shyrik.modularitemframe.common.module;

import de.shyrik.modularitemframe.ConfigValues;
import de.shyrik.modularitemframe.ModularItemFrame;
import de.shyrik.modularitemframe.api.utils.Utils;
import de.shyrik.modularitemframe.client.gui.GuiHandler;
import de.shyrik.modularitemframe.common.container.ContainerCraftingFrame;
import de.shyrik.modularitemframe.common.container.FrameCrafting;
import de.shyrik.modularitemframe.common.container.IContainerCallbacks;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.ProbeMode;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class ModuleCrafting extends ModuleItem implements IContainerCallbacks {

	private static final String NBT_GHOSTINVENTORY = "ghostinventory";

	private IRecipe recipe;
	public ItemStackHandler ghostInventory = new ItemStackHandler(9);

	public ModuleCrafting() {
		super();
		scale = 0.7F;
		offset = -0.05F;
	}

	@Nonnull
	@Override
	public ResourceLocation getModelLocation() {
		return new ResourceLocation(ModularItemFrame.MOD_ID, "blocks/crafting_frame_bg");
	}

	@Override
	public String getModuleName() {
		return I18n.format("modularitemframe.module.craft");
	}

	@Override
	public boolean onBlockActivated(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull EntityPlayer playerIn, @Nonnull EnumHand hand, @Nonnull EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (!worldIn.isRemote) {
			if (!hasValidRecipe(playerIn))
				playerIn.openGui(ModularItemFrame.instance, GuiHandler.CRAFTING_FRAME, worldIn, pos.getX(), pos.getY(), pos.getZ());
			else {
				if (playerIn.isSneaking()) craft(playerIn, true);
				else craft(playerIn, false);
			}
			tile.markDirty();
		}
		return true;
	}

	@Override
	public ContainerCraftingFrame createContainer(final EntityPlayer player) {
		final IItemHandlerModifiable playerInventory = (IItemHandlerModifiable) player.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.UP);

		return new ContainerCraftingFrame(playerInventory, ghostInventory, player, this);
	}

	private void craft(EntityPlayer player, boolean fullStack) {
		if (player instanceof FakePlayer && !ConfigValues.AllowFakePlayers) return;

		final IItemHandlerModifiable playerInventory = (IItemHandlerModifiable) player.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.UP);
		final IItemHandlerModifiable workingInv = getWorkingInventories(playerInventory);

		if (recipe == null) reloadRecipe(player);

		if (workingInv == null || recipe == null || recipe.getRecipeOutput().isEmpty() || !Utils.canCraft(workingInv, recipe.getIngredients()))
			return;

		int craftAmount = fullStack ? Math.min(Utils.countPossibleCrafts(workingInv, recipe.getIngredients()), 64) : 1;
		do {
			ItemStack remain = Utils.giveStack(playerInventory, recipe.getRecipeOutput()); //use playerinventory here!
			if (!remain.isEmpty()) Utils.ejectStack(player.world, tile.getPos(), tile.blockFacing(), remain);

			for (Ingredient ingredient : recipe.getIngredients()) {
				if (ingredient.getMatchingStacks().length > 0) {
					Utils.removeFromInventory(workingInv, ingredient.getMatchingStacks());
				}
			}
		} while (--craftAmount > 0);
		player.world.playSound(null, tile.getPos(), SoundEvents.BLOCK_LADDER_STEP, SoundCategory.BLOCKS, 0.4F, 0.7F);
	}

	private IItemHandlerModifiable getWorkingInventories(IItemHandlerModifiable playerInventory) {
		EnumFacing facing = tile.blockFacing();
		TileEntity neighbor = tile.getNeighbor(facing);
		IItemHandlerModifiable neighborInventory = null;
		if (neighbor != null) {
			neighborInventory = (IItemHandlerModifiable) neighbor.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing.getOpposite());
		}

		if (neighborInventory != null) {
			if (!ConfigValues.StillUsePlayerInv) return neighborInventory;
			else return new CombinedInvWrapper(neighborInventory, playerInventory);
		}
		return playerInventory;
	}

	public boolean hasValidRecipe(@Nonnull EntityPlayer player) {
		if (recipe == null) reloadRecipe(player);
		return recipe != null && !recipe.getRecipeOutput().isEmpty();
	}

	private void reloadRecipe(EntityPlayer player) {
		FrameCrafting fc = new FrameCrafting(new ContainerCraftingFrame(null, ghostInventory, player, this), ghostInventory, 3, 3);
		fc.onCraftMatrixChanged();
	}

	/*if(!worldIn.isRemote && placer instanceof EntityPlayer) {
		((EntityPlayer) placer).openGui(ModularItemFrame.instance, GuiHandler.CRAFTING_FRAME, worldIn, pos.getX(), pos.getY(), pos.getZ());
		getTE(worldIn, pos).markDirty();
	}*/

	@Override
	public boolean isUsableByPlayer(EntityPlayer player) {
		BlockPos pos = tile.getPos();
		return player.getDistanceSq(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) <= 64;
	}

	@Override
	@Optional.Method(modid = "theoneprobe")
	public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world, IBlockState blockState, IProbeHitData data) {
		super.addProbeInfo(mode, probeInfo, player, world, blockState, data);
		if (recipe != null && !recipe.getRecipeOutput().isEmpty()) {
			IProbeInfo input = probeInfo.horizontal().text("Input:");
			List<ItemStack> stacks = new ArrayList<>();
			for (int slot = 0; slot < ghostInventory.getSlots(); ++slot) {
				ItemStack stack = ghostInventory.getStackInSlot(slot);
				if (!stack.isEmpty()) {
					if (!Utils.increaseStackinList(stacks, stack))
						stacks.add(stack.copy());
				}
			}
			for (ItemStack stack : stacks) {
				input.item(stack);
			}
			probeInfo.horizontal().text("output:").item(recipe.getRecipeOutput());
		}
	}

	@Nonnull
	@Override
	public List<String> getWailaBody(ItemStack itemStack, IWailaDataAccessor accessor, IWailaConfigHandler config) {
		return super.getWailaBody(itemStack, accessor, config);
	}

	@Override
	public void onContainerCraftingResultChanged(InventoryCraftResult result) {
		displayItem = result.getStackInSlot(0);
		recipe = result.getRecipeUsed();
		tile.markDirty();
	}


	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound compound = super.serializeNBT();
		compound.setTag(NBT_GHOSTINVENTORY, ghostInventory.serializeNBT());
		return compound;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		super.deserializeNBT(nbt);
		if (nbt.hasKey(NBT_GHOSTINVENTORY)) ghostInventory.deserializeNBT(nbt.getCompoundTag(NBT_GHOSTINVENTORY));
	}
}
