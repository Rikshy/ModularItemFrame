package de.shyrik.modularitemframe.common.item;

import de.shyrik.modularitemframe.ModularItemFrame;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTier;
import net.minecraft.item.ItemTool;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.List;

public class ItemScrewdriver extends ItemTool {
    private static final String NBT_MODE = "mode";
    public static final ResourceLocation LOC = new ResourceLocation(ModularItemFrame.MOD_ID, "screwdriver");

    public ItemScrewdriver(Properties props) {
        super(3F,3F, ItemTier.IRON, new HashSet<>(), props);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        tooltip.add(new TextComponentTranslation("Mode: " + readModeFromNBT(stack).getName()));
    }

    @Nonnull
    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, @Nonnull EnumHand handIn) {
        EnumActionResult result = EnumActionResult.PASS;
        if (!worldIn.isRemote && playerIn.isSneaking()) {
            ItemStack driver = playerIn.getHeldItem(handIn);
            EnumMode mode = readModeFromNBT(driver);
            mode = EnumMode.VALUES[mode.getIndex() + 1 >= EnumMode.values().length ? 0 : mode.getIndex() + 1];
            writeModeToNbt(driver, mode);
            playerIn.sendMessage(new TextComponentTranslation("modularitemframe.message.screw_mode_change", mode.getName()));

            result = EnumActionResult.SUCCESS;
        }
        return new ActionResult<>(result, playerIn.getHeldItem(handIn));
    }

    public static EnumMode getMode(ItemStack driver) {
        return readModeFromNBT(driver);
    }

    private static void writeModeToNbt(ItemStack stack, EnumMode mode) {
        NBTTagCompound nbt = stack.getTag();
        if (nbt == null) nbt = new NBTTagCompound();
        nbt.putInt(NBT_MODE, mode.getIndex());
        stack.setTag(nbt);
    }

    private static EnumMode readModeFromNBT(ItemStack stack) {
        NBTTagCompound nbt = stack.getTag();
        EnumMode mode = EnumMode.REMOVE;
        if (nbt == null) writeModeToNbt(stack, mode);
        else if (nbt.hasUniqueId(NBT_MODE)) mode = EnumMode.VALUES[nbt.getInt(NBT_MODE)];
        return mode;
    }

    public enum EnumMode {
        REMOVE(0, "modularitemframe.message.screw_mode_change.rem"),
        INTERACT(1, "modularitemframe.message.screw_mode_change.inter");
        //ROTATE(2, "modularitemframe.message.screw_mode_change.rot");

        public static final EnumMode[] VALUES = new EnumMode[3];

        private final int index;
        private final String name;

        EnumMode(int indexIn, String nameIn) {
            index = indexIn;
            name = nameIn;
        }

        public int getIndex() {
            return this.index;
        }

        public String getName() {
            return I18n.format(this.name);
        }

        static {
            for (EnumMode enummode : values())
                VALUES[enummode.index] = enummode;
        }
    }
}
