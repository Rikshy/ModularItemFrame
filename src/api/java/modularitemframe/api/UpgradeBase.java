package modularitemframe.api;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.INBTSerializable;
import org.jetbrains.annotations.NotNull;

public abstract class UpgradeBase implements INBTSerializable<CompoundNBT> {
    UpgradeItem item;

    public UpgradeItem getItem() {
        return item;
    }

    /**
     * @return unique ID the upgrade gets registered with.
     */
    @NotNull
    public abstract ResourceLocation getId();

    /**
     * @return the name of the upgrade :O
     */
    @NotNull
    @OnlyIn(Dist.CLIENT)
    public abstract TextComponent getName();

    /**
     * @return max times this upgrade can be present in a frame.
     */
    public abstract int getMaxCount();

    public void onInsert(@NotNull World world, @NotNull BlockPos pos, @NotNull Direction facing, PlayerEntity player, ItemStack upStack) {
    }

    public void onRemove(@NotNull World world, @NotNull BlockPos pos, @NotNull Direction facing, PlayerEntity player, ItemStack upStack) {
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
}
