package de.shyrik.modularitemframe.common.module.t2;

import de.shyrik.modularitemframe.ModularItemFrame;
import de.shyrik.modularitemframe.api.ModuleBase;
import de.shyrik.modularitemframe.api.util.ItemHandlerHelper;
import de.shyrik.modularitemframe.api.util.ItemHelper;
import de.shyrik.modularitemframe.common.block.BlockModularFrame;
import net.minecraft.block.BlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nonnull;

public class ModuleDispense extends ModuleBase {

    public static final ResourceLocation LOC = new ResourceLocation(ModularItemFrame.MOD_ID, "module_t2_dispense");
    public static final ResourceLocation BG_LOC = new ResourceLocation(ModularItemFrame.MOD_ID, "block/module_t2_dispense");
    private static final String NBT_RANGE = "range";

    private int range = 1;

    @Override
    public ResourceLocation getId() {
        return LOC;
    }

    @Nonnull
    @Override
    @OnlyIn(Dist.CLIENT)
    public ResourceLocation frontTexture() {
        return BG_LOC;
    }

    @Nonnull
    @Override
    @OnlyIn(Dist.CLIENT)
    public ResourceLocation innerTexture() {
        return BlockModularFrame.INNER_HARD_LOC;
    }

    @Override
    public String getModuleName() {
        return I18n.format("modularitemframe.module.dispense");
    }

    @Override
    public void screw(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull PlayerEntity playerIn, ItemStack driver) {
        int countRange = tile.getRangeUpCount() + 1;
        if (!world.isRemote && countRange > 1) {
            if (playerIn.isSneaking()) range--;
            else range++;
            if (range < 1) range = countRange;
            if (range > countRange) range = 1;
            playerIn.sendMessage(new TranslationTextComponent("modularitemframe.message.range_change", range + 1));
            tile.markDirty();
        }
    }

    @Override
    public ActionResultType onBlockActivated(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nonnull PlayerEntity playerIn, @Nonnull Hand hand, @Nonnull Direction facing, BlockRayTraceResult hit) {
        if (!worldIn.isRemote) {
            ItemStack held = playerIn.getHeldItem(hand);
            if (!held.isEmpty()) {
                ItemHelper.ejectStack(worldIn, pos, facing.getOpposite(), held.copy());
                held.setCount(0);
            }
        }
        return ActionResultType.SUCCESS;
    }

    @Override
    public void onFrameUpgradesChanged() {
        super.onFrameUpgradesChanged();
        range = Math.min(range, tile.getRangeUpCount() + 1);
    }

    @Override
    public void tick(@Nonnull World world, @Nonnull BlockPos pos) {
        if (!world.isRemote) {
            if (world.getGameTime() % (60 - 10 * tile.getSpeedUpCount()) != 0) return;

            Direction facing = tile.blockFacing();
            TileEntity targetTile = world.getTileEntity(pos.offset(facing.getOpposite(), range));
            if (targetTile != null) {
                IItemHandlerModifiable inv = ItemHandlerHelper.getItemHandler(targetTile, facing);
                if (inv != null) {
                    for (int slot = 0; slot < inv.getSlots(); slot++) {
                        if (!inv.getStackInSlot(slot).isEmpty()) {
                            ItemHelper.ejectStack(world, pos, facing, inv.getStackInSlot(slot));
                            inv.setStackInSlot(slot, ItemStack.EMPTY);
                            break;
                        }
                    }
                }
            }
        }
    }

    @Nonnull
    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = super.serializeNBT();
        nbt.putInt(NBT_RANGE, range);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        super.deserializeNBT(nbt);
        if (nbt.contains(NBT_RANGE)) range = nbt.getInt(NBT_RANGE);
    }
}
