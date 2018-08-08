package de.shyrik.justcraftingframes.common.module;

import com.teamwizardry.librarianlib.features.saving.Save;
import de.shyrik.justcraftingframes.ConfigValues;
import de.shyrik.justcraftingframes.JustCraftingFrames;
import de.shyrik.justcraftingframes.api.utils.Utils;
import de.shyrik.justcraftingframes.client.gui.GuiHandler;
import de.shyrik.justcraftingframes.common.container.ContainerCraftingFrame;
import de.shyrik.justcraftingframes.common.container.FrameCrafting;
import de.shyrik.justcraftingframes.common.container.IContainerCallbacks;
import de.shyrik.justcraftingframes.common.tile.TileModularFrame;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;

import javax.annotation.Nonnull;

public class ModuleCrafting extends ModuleItem implements IContainerCallbacks {

	private IRecipe recipe;

	@Save
	public ItemStackHandler ghostInventory = new ItemStackHandler(9);

	public ModuleCrafting(TileModularFrame te) {
		super(te);
		scale = 0.7F;
		offset = -0.05F;
	}

	@Nonnull
	@Override
	public ResourceLocation getModelLocation() {
		return new ResourceLocation(JustCraftingFrames.MOD_ID, "blocks/crafting_frame_bg");
	}

	@Override
	public void onBlockActivated(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull EntityPlayer playerIn, @Nonnull EnumHand hand, @Nonnull EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (!worldIn.isRemote) {
			if (!hasValidRecipe(playerIn))
				playerIn.openGui(JustCraftingFrames.instance, GuiHandler.CRAFTING_FRAME, worldIn, pos.getX(), pos.getY(), pos.getZ());
			else {
				if (playerIn.isSneaking())
					craft(playerIn, true);
				else
					craft(playerIn, false);
			}
			tile.markDirty();
		}
	}

	@Override
	public ContainerCraftingFrame createContainer(final EntityPlayer player) {
		final IItemHandlerModifiable playerInventory = (IItemHandlerModifiable)player.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.UP);

		return new ContainerCraftingFrame(playerInventory, ghostInventory, player, this);
	}

	private void craft(EntityPlayer player, boolean fullStack) {
		if (player instanceof FakePlayer && !ConfigValues.AllowFakePlayers)
			return;

		final IItemHandlerModifiable playerInventory = (IItemHandlerModifiable) player.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.UP);
		final IItemHandlerModifiable workingInv = getWorkingInventories(playerInventory);

		if (recipe == null)
			reloadRecipe(player);

		if (workingInv == null || recipe == null || recipe.getRecipeOutput().isEmpty() || !Utils.canCraft(workingInv, recipe.getIngredients()))
			return;

		int craftAmount = fullStack ? Math.min(Utils.countPossibleCrafts(workingInv, recipe.getIngredients()), 64) : 1;
		do {
			ItemStack remain = Utils.giveStack(playerInventory, recipe.getRecipeOutput()); //use playerinventory here!
			if (!remain.isEmpty()) Utils.ejectStack(world, pos, tile.blockFacing(), remain);

			for (Ingredient ingredient : recipe.getIngredients()) {
				if (ingredient.getMatchingStacks().length > 0) {
					Utils.removeFromInventory(workingInv, ingredient.getMatchingStacks());
				}
			}
		} while (--craftAmount > 0);
		world.playSound(null, pos, SoundEvents.BLOCK_LADDER_STEP, SoundCategory.BLOCKS, 0.4F, 0.7F);
	}

	private IItemHandlerModifiable getWorkingInventories(IItemHandlerModifiable playerInventory) {
		EnumFacing facing = tile.blockFacing();
		TileEntity neighbor = world.getTileEntity(pos.offset(facing));
		IItemHandlerModifiable neighborInventory = null;
		if (neighbor != null) {
			neighborInventory = (IItemHandlerModifiable) neighbor.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing.getOpposite());
		}

		if(neighborInventory != null) {
			if (!ConfigValues.StillUsePlayerInv) return neighborInventory;
			else return new CombinedInvWrapper(neighborInventory, playerInventory);
		}
		return playerInventory;
	}

	public boolean hasValidRecipe(@Nonnull EntityPlayer player) {
		if ( recipe == null) reloadRecipe(player);
		return recipe != null && !recipe.getRecipeOutput().isEmpty();
	}

	private void reloadRecipe(EntityPlayer player) {
		FrameCrafting fc = new FrameCrafting(new ContainerCraftingFrame(null, ghostInventory, player, this), ghostInventory, 3, 3);
		fc.onCraftMatrixChanged();
	}

	/*if(!worldIn.isRemote && placer instanceof EntityPlayer) {
		((EntityPlayer) placer).openGui(JustCraftingFrames.instance, GuiHandler.CRAFTING_FRAME, worldIn, pos.getX(), pos.getY(), pos.getZ());
		getTE(worldIn, pos).markDirty();
	}*/

	@Override
	public boolean isUsableByPlayer(EntityPlayer player) {
		return world.getTileEntity(pos) == tile && player.getDistanceSq(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) <= 64;
	}

	@Override
	public void onContainerCraftingResultChanged(InventoryCraftResult result) {
		displayItem = result.getStackInSlot(0);
		recipe = result.getRecipeUsed();
	}
}
