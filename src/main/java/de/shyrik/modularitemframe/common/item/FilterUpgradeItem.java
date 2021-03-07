package de.shyrik.modularitemframe.common.item;

import de.shyrik.modularitemframe.common.container.filter.FilterUpgradeContainer;
import modularitemframe.api.inventory.OpenItemStackHandler;
import modularitemframe.api.UpgradeBase;
import modularitemframe.api.UpgradeItem;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FilterUpgradeItem extends UpgradeItem {

    private static final String NBT_FILTER = "item_filter";
    private static final String NBT_MODE = "filter_mode";

    public FilterUpgradeItem(Properties prop, Class<? extends UpgradeBase> upgradeClass, ResourceLocation upgradeId) {
        super(prop, upgradeClass, upgradeId);
    }

    @Override
    public void addInformation(@NotNull ItemStack stack, @Nullable World worldIn, @NotNull List<ITextComponent> tooltip, @NotNull ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        EnumMode mode = readModeTag(stack.getOrCreateTag());
        tooltip.add(new TranslationTextComponent("modularitemframe.tooltip.mode").append(mode.getName()));
    }

    @NotNull
    @Override
    public ActionResult<ItemStack> onItemRightClick(@NotNull World world, @NotNull PlayerEntity user, @NotNull Hand hand) {
        ItemStack stack = user.getHeldItem(hand);
        if (!user.isSneaking()) return ActionResult.resultPass(stack);

        if (!world.isRemote) {
            user.openContainer(getContainerProvider(stack));
        }

        return ActionResult.resultSuccess(stack);
    }

    public INamedContainerProvider getContainerProvider(ItemStack filter) {
        return new SimpleNamedContainerProvider((id, playerInventory, player) ->
                new FilterUpgradeContainer(
                        id,
                        player,
                        filter),
                new TranslationTextComponent("gui.modularitemframe.filter.name")
        );
    }

    public static TagReadResult readTags(CompoundNBT tag) {
        TagReadResult result = new TagReadResult();
        result.inv = readInvTag(tag);
        result.mode = readModeTag(tag);
        return result;
    }

    public static OpenItemStackHandler readInvTag(CompoundNBT tag) {
        OpenItemStackHandler inv = new OpenItemStackHandler(9);
        inv.deserializeNBT(tag.getCompound(NBT_FILTER));
        return inv;
    }

    public static EnumMode readModeTag(CompoundNBT tag) {
        EnumMode mode = EnumMode.WHITELIST;
        if (tag.contains(NBT_MODE)) {
            mode = EnumMode.values()[tag.getInt(NBT_MODE)];
        }
        return mode;
    }

    public static void writeTags(CompoundNBT tag, ItemStackHandler inv, EnumMode mode) {
        writeInvTag(tag, inv);
        tag.putInt(NBT_MODE, mode.getIndex());
    }

    public static void writeInvTag(CompoundNBT tag, ItemStackHandler inv) {
        tag.put(NBT_FILTER, inv.serializeNBT());
    }

    public static class TagReadResult {
        public OpenItemStackHandler inv;
        public EnumMode mode = EnumMode.WHITELIST;
    }

    public enum EnumMode {
        WHITELIST(0, "modularitemframe.mode.whitelist"),
        BLACKLIST(1, "modularitemframe.mode.blacklist");

        private final int index;
        private final TextComponent name;

        EnumMode(int indexIn, String nameIn) {
            index = indexIn;
            name = new TranslationTextComponent(nameIn);
        }

        public int getIndex() {
            return this.index;
        }

        public TextComponent getName() {
            return this.name;
        }
    }
}
