package de.shyrik.modularitemframe.common.module.t2;

import de.shyrik.modularitemframe.ModularItemFrame;
import de.shyrik.modularitemframe.common.block.BlockModularFrame;
import de.shyrik.modularitemframe.common.module.t1.ModuleCrafting;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;

import javax.annotation.Nonnull;

public class ModuleCraftingPlus extends ModuleCrafting {

    public static final ResourceLocation LOC = new ResourceLocation(ModularItemFrame.MOD_ID, "module_t2_craft_plus");
    public static final ResourceLocation BG_LOC = new ResourceLocation(ModularItemFrame.MOD_ID, "block/module_t2_craft_plus");
    private static final String NBT_MODE = "cpmode";

    public EnumMode mode = EnumMode.PLAYER;

    @Override
    public ResourceLocation getId() {
        return LOC;
    }

    @Override
    public String getModuleName() {
        return I18n.format("modularitemframe.module.crafting_plus");
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
    public void screw(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull PlayerEntity playerIn, ItemStack driver) {
        if (!world.isRemote) {
            if (playerIn.isSneaking()) {
                int modeIdx = mode.getIndex() + 1;
                if (modeIdx == EnumMode.values().length) modeIdx = 0;
                mode = EnumMode.values()[modeIdx];
                playerIn.sendMessage(new TranslationTextComponent(mode.getName()));
                tile.markDirty();
            } else {
                playerIn.openContainer(getContainer(tile.getBlockState(), world, pos));
                tile.markDirty();
            }
        }
    }

    @Override
    protected IItemHandlerModifiable getWorkingInventories(IItemHandlerModifiable playerInventory) {
        IItemHandlerModifiable neighborInventory = (IItemHandlerModifiable)tile.getAttachedInventory();

        if (neighborInventory != null) {
            if (mode == EnumMode.NO_PLAYER) return neighborInventory;
            else return new CombinedInvWrapper(neighborInventory, playerInventory);
        }
        return playerInventory;
    }

    @Nonnull
    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = super.serializeNBT();
        nbt.putInt(NBT_MODE, mode.getIndex());
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        super.deserializeNBT(nbt);
        if (nbt.contains(NBT_MODE)) mode = EnumMode.values()[nbt.getInt(NBT_MODE)];
    }

    public enum EnumMode {
        PLAYER(0, "modularitemframe.message.crafting_plus.player"),
        NO_PLAYER(1, "modularitemframe.message.crafting_plus.no_player");

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
    }
}