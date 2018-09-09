package de.shyrik.modularitemframe.common.module.t2;

import de.shyrik.modularitemframe.ModularItemFrame;
import de.shyrik.modularitemframe.api.ConfigValues;
import de.shyrik.modularitemframe.client.gui.GuiHandler;
import de.shyrik.modularitemframe.common.block.BlockModularFrame;
import de.shyrik.modularitemframe.common.module.t1.ModuleCrafting;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;

import javax.annotation.Nonnull;

public class ModuleCraftingPlus extends ModuleCrafting {

    public static final ResourceLocation LOC = new ResourceLocation(ModularItemFrame.MOD_ID, "module_t2_craft_plus");
    private static final String NBT_MODE = "cpmode";

    public EnumMode mode = EnumMode.PLAYER;

    @Override
    public String getModuleName() {
        return I18n.format("modularitemframe.module.crafting_plus");
    }

    @Nonnull
    @Override
    public ResourceLocation innerTexture() {
        return BlockModularFrame.INNER_HARD_LOC;
    }

    @Override
    public void screw(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull EntityPlayer playerIn, ItemStack driver) {
        if (playerIn instanceof FakePlayer && !ConfigValues.AllowFakePlayers) return;

        if (!world.isRemote) {
            if (playerIn.isSneaking()) {
                int modeIdx = mode.getIndex() + 1;
                if (modeIdx == EnumMode.values().length) modeIdx = 0;
                mode = EnumMode.values()[modeIdx];
                mode = EnumMode.values()[mode.getIndex() + 1 >= EnumMode.values().length ? 0 : mode.getIndex() + 1];
                playerIn.sendMessage(new TextComponentTranslation(mode.getName()));
            } else {
                playerIn.openGui(ModularItemFrame.instance, GuiHandler.CRAFTING_FRAME, world, pos.getX(), pos.getY(), pos.getZ());
                tile.markDirty();
            }
        }
    }

    @Override
    protected IItemHandlerModifiable getWorkingInventories(IItemHandlerModifiable playerInventory) {
        EnumFacing facing = tile.blockFacing();

        TileEntity neighbor = tile.getNeighbor(facing);
        IItemHandlerModifiable neighborInventory = null;
        if (neighbor != null) {
            neighborInventory = (IItemHandlerModifiable) neighbor.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing.getOpposite());
        }

        if (neighborInventory != null) {
            if (mode == EnumMode.NO_PLAYER) return neighborInventory;
            else return new CombinedInvWrapper(neighborInventory, playerInventory);
        }
        return playerInventory;
    }

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound nbt = super.serializeNBT();
        nbt.setInteger(NBT_MODE, mode.getIndex());
        return nbt;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        super.deserializeNBT(nbt);
        if (nbt.hasKey(NBT_MODE)) mode = EnumMode.values()[nbt.getInteger(NBT_MODE)];
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