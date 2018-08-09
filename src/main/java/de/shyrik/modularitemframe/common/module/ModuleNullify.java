package de.shyrik.modularitemframe.common.module;

import com.teamwizardry.librarianlib.features.saving.NamedDynamic;
import com.teamwizardry.librarianlib.features.saving.Save;
import de.shyrik.modularitemframe.ConfigValues;
import de.shyrik.modularitemframe.ModularItemFrame;
import de.shyrik.modularitemframe.api.utils.Utils;
import de.shyrik.modularitemframe.common.tile.TileModularFrame;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nonnull;

@NamedDynamic(resourceLocation = "module_nullify")
public class ModuleNullify extends ModuleFluid {

    @Save
    public ItemStack lastStack = ItemStack.EMPTY;

    public ModuleNullify() {
        super();
        tank.setFluid(FluidUtil.getFluidContained(new ItemStack(Items.LAVA_BUCKET)));
    }

    @Nonnull
    @Override
    public ResourceLocation getModelLocation() {
        return new ResourceLocation(ModularItemFrame.MOD_ID, "blocks/nullify_frame_bg");
    }

    @Override
    public void onBlockActivated(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull EntityPlayer playerIn, @Nonnull EnumHand hand, @Nonnull EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack held = playerIn.getHeldItem(hand);
        if(!playerIn.isSneaking() && !held.isEmpty()) {
            if (Utils.simpleAreStacksEqual(held, lastStack)) {
                if(held.getCount() + lastStack.getCount() > lastStack.getMaxStackSize())
                    lastStack.setCount(lastStack.getMaxStackSize());
                else
                    lastStack.setCount(lastStack.getCount() + held.getCount());
            } else {
                lastStack = held.copy();
            }
            held.setCount(0);
            worldIn.playSound(null, pos, SoundEvents.BLOCK_LAVA_EXTINGUISH, SoundCategory.BLOCKS, 0.4F, 0.7F);
        } else if (playerIn.isSneaking() && held.isEmpty() && !lastStack.isEmpty()) {
            playerIn.setHeldItem(hand, lastStack);
            lastStack = ItemStack.EMPTY;
            worldIn.playSound(null, pos, SoundEvents.ENTITY_ENDERPEARL_THROW, SoundCategory.BLOCKS, 0.4F, 0.7F);
        }
    }

    @Override
    public void tick(@Nonnull World world, @Nonnull BlockPos pos) {
        if(ConfigValues.CanNulliFrameSuckFromInvent && !world.isRemote) {
            if (world.getTotalWorldTime() % 20 != 0)
                return;

            EnumFacing facing = tile.blockFacing();
            TileEntity tile = world.getTileEntity(pos.offset(facing));
            if(tile != null) {
                IItemHandlerModifiable trash = (IItemHandlerModifiable) tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing.getOpposite());
                if (trash != null) {
                    for (int slot = 0; slot < trash.getSlots(); slot++) {
                        if(!trash.getStackInSlot(slot).isEmpty()) {
                            trash.setStackInSlot(slot, ItemStack.EMPTY);
                            world.playSound(null, pos, SoundEvents.BLOCK_LAVA_EXTINGUISH, SoundCategory.BLOCKS, 0.4F, 0.7F);
                            break;
                        }
                    }
                }
            }
        }
    }
}
