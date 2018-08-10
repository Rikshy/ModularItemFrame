package de.shyrik.modularitemframe.common.module;

import com.teamwizardry.librarianlib.features.saving.NamedDynamic;
import com.teamwizardry.librarianlib.features.saving.Save;
import de.shyrik.modularitemframe.ConfigValues;
import de.shyrik.modularitemframe.ModularItemFrame;
import de.shyrik.modularitemframe.api.utils.Utils;
import de.shyrik.modularitemframe.client.gui.GuiHandler;
import de.shyrik.modularitemframe.common.container.ContainerCraftingFrame;
import de.shyrik.modularitemframe.common.container.FrameCrafting;
import de.shyrik.modularitemframe.common.container.IContainerCallbacks;
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

@NamedDynamic(resourceLocation = "module_crafting")
public class ModuleCrafting extends ModuleItem implements IContainerCallbacks {

	private IRecipe recipe;

	@Save
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
	public void onBlockActivated(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull EntityPlayer playerIn, @Nonnull EnumHand hand, @Nonnull EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (!worldIn.isRemote) {
			if (!hasValidRecipe(playerIn))
				playerIn.openGui(ModularItemFrame.instance, GuiHandler.CRAFTING_FRAME, worldIn, pos.getX(), pos.getY(), pos.getZ());
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
		((EntityPlayer) placer).openGui(ModularItemFrame.instance, GuiHandler.CRAFTING_FRAME, worldIn, pos.getX(), pos.getY(), pos.getZ());
		getTE(worldIn, pos).markDirty();
	}*/

	@Override
	public boolean isUsableByPlayer(EntityPlayer player) {
		BlockPos pos = tile.getPos();
		return player.getDistanceSq(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) <= 64;
	}

	@Override
	public void onContainerCraftingResultChanged(InventoryCraftResult result) {
		displayItem = result.getStackInSlot(0);
		recipe = result.getRecipeUsed();
	}
}
