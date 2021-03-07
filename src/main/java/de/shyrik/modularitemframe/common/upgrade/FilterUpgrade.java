package de.shyrik.modularitemframe.common.upgrade;

import de.shyrik.modularitemframe.ModularItemFrame;
import de.shyrik.modularitemframe.common.item.FilterUpgradeItem;
import modularitemframe.api.inventory.filter.AggregatedItemFilter;
import modularitemframe.api.inventory.filter.IItemFilter;
import modularitemframe.api.UpgradeBase;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

public class FilterUpgrade extends UpgradeBase {
    public static final ResourceLocation ID = new ResourceLocation(ModularItemFrame.MOD_ID, "upgrade_filter");
    private static final TextComponent NAME = new TranslationTextComponent("modularitemframe.upgrade.filter");

    private ItemStackHandler inv;
    private IItemFilter filter = null;
    public FilterUpgradeItem.EnumMode mode;

    @Override
    public int getMaxCount() {
        return 99;
    }

    @NotNull
    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @NotNull
    @Override
    public TextComponent getName() {
        return NAME;
    }

    @Override
    public void onInsert(@NotNull World world, @NotNull BlockPos pos, @NotNull Direction facing, PlayerEntity player, ItemStack upStack) {
        deserializeNBT(upStack.getOrCreateTag());
    }

    @Override
    public void onRemove(@NotNull World world, @NotNull BlockPos pos, @NotNull Direction facing, PlayerEntity player, ItemStack upStack) {
        FilterUpgradeItem.writeTags(upStack.getOrCreateTag(), inv, mode);
    }

    @NotNull
    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT tag = super.serializeNBT();
        FilterUpgradeItem.writeTags(tag, inv, mode);
        return tag;
    }

    @Override
    public void deserializeNBT(@NotNull CompoundNBT tag) {
        super.deserializeNBT(tag);
        FilterUpgradeItem.TagReadResult result = FilterUpgradeItem.readTags(tag);
        inv = result.inv;
        filter = AggregatedItemFilter.anyOf(
                result.inv
                        .stream()
                        .filter(stack -> !stack.isEmpty())
                        .toArray(ItemStack[]::new));
        mode = result.mode;

        if (mode == FilterUpgradeItem.EnumMode.BLACKLIST)
            filter = filter.negate();
    }

    public IItemFilter getFilter() {
        return filter;
    }
}
