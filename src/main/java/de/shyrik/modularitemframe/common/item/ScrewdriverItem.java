package de.shyrik.modularitemframe.common.item;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTier;
import net.minecraft.item.ToolItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.List;

public class ScrewdriverItem extends ToolItem {
    private static final String NBT_MODE = "mode";

    public ScrewdriverItem(Properties properties) {
        super(1, 1, ItemTier.IRON, new HashSet<>(), properties);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void addInformation(@NotNull ItemStack stack, @Nullable World worldIn, @NotNull List<ITextComponent> tooltip, @NotNull ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        tooltip.add(new TranslationTextComponent("modularitemframe.tooltip.mode").append(readModeFromNBT(stack).getName()));
    }

    @NotNull
    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, @NotNull PlayerEntity player, @NotNull Hand hand) {
        ActionResultType result = ActionResultType.PASS;
        if (!world.isRemote && player.isSneaking()) {
            ItemStack driver = player.getHeldItem(hand);
            EnumMode mode = readModeFromNBT(driver);
            mode = EnumMode.values()[mode.getIndex() + 1 >= EnumMode.values().length ? 0 : mode.getIndex() + 1];
            writeModeToNbt(driver, mode);
            player.sendStatusMessage(new TranslationTextComponent("modularitemframe.message.screw_mode_change", mode.getName()), false);

            result = ActionResultType.SUCCESS;
        }
        return new ActionResult<>(result, player.getHeldItem(hand));
    }

    public static EnumMode getMode(ItemStack driver) {
        return readModeFromNBT(driver);
    }

    private static void writeModeToNbt(ItemStack stack, EnumMode mode) {
        CompoundNBT nbt = stack.getTag();
        if (nbt == null) nbt = new CompoundNBT();
        nbt.putInt(NBT_MODE, mode.getIndex());
        stack.setTag(nbt);
    }

    private static EnumMode readModeFromNBT(ItemStack stack) {
        CompoundNBT nbt = stack.getTag();
        EnumMode mode = EnumMode.REMOVE_MOD;
        if (nbt == null) writeModeToNbt(stack, mode);
        else if (nbt.contains(NBT_MODE)) mode = EnumMode.values()[nbt.getInt(NBT_MODE)];
        return mode;
    }

    public enum EnumMode {
        REMOVE_MOD(0, "modularitemframe.mode.remove_module"),
        REMOVE_UP(1, "modularitemframe.mode.remove_upgrades"),
        INTERACT(2, "modularitemframe.mode.interact");
        //ROTATE(2, "modularitemframe.message.screw_mode_change.rot");

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
            return name;
        }
    }
}
