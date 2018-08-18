package de.shyrik.modularitemframe.common.module.t1;

import de.shyrik.modularitemframe.api.ConfigValues;
import de.shyrik.modularitemframe.ModularItemFrame;
import de.shyrik.modularitemframe.api.utils.ItemUtils;
import de.shyrik.modularitemframe.common.module.ModuleFluid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fluids.FluidUtil;

import javax.annotation.Nonnull;

public class ModuleNullify extends ModuleFluid {

    private static final String NBT_LASTSTACK = "laststack";

    public ItemStack lastStack = ItemStack.EMPTY;

    public ModuleNullify() {
        super();
        tank.setFluid(FluidUtil.getFluidContained(new ItemStack(Items.LAVA_BUCKET)));
    }

    @Override
    public String getModuleName() {
        return I18n.format("modularitemframe.module.nullify");
    }

    @Nonnull
    @Override
    public ResourceLocation frontTexture() {
        return new ResourceLocation(ModularItemFrame.MOD_ID, "blocks/nullify_frame_bg");
    }

    @Override
    public boolean onBlockActivated(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull EntityPlayer playerIn, @Nonnull EnumHand hand, @Nonnull EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (playerIn instanceof FakePlayer && !ConfigValues.AllowFakePlayers) return false;

        ItemStack held = playerIn.getHeldItem(hand);
        if (!playerIn.isSneaking() && !held.isEmpty()) {
            if (ItemUtils.simpleAreStacksEqual(held, lastStack)) {
                if (held.getCount() + lastStack.getCount() > lastStack.getMaxStackSize())
                    lastStack.setCount(lastStack.getMaxStackSize());
                else lastStack.grow(held.getCount());
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
        return true;
    }

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound compound = super.serializeNBT();
        compound.setTag(NBT_LASTSTACK, lastStack.serializeNBT());
        return compound;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        super.deserializeNBT(nbt);
        if (nbt.hasKey(NBT_LASTSTACK)) lastStack = new ItemStack(nbt.getCompoundTag(NBT_LASTSTACK));
    }
}