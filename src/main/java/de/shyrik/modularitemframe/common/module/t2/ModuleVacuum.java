package de.shyrik.modularitemframe.common.module.t2;

import de.shyrik.modularitemframe.ModularItemFrame;
import de.shyrik.modularitemframe.api.ConfigValues;
import de.shyrik.modularitemframe.api.ModuleBase;
import de.shyrik.modularitemframe.api.utils.ItemUtils;
import de.shyrik.modularitemframe.common.block.BlockModularFrame;
import de.shyrik.modularitemframe.common.network.NetworkHandler;
import de.shyrik.modularitemframe.common.network.packet.SpawnParticlesPacket;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nonnull;
import java.util.List;

public class ModuleVacuum extends ModuleBase {

    public static final ResourceLocation LOC = new ResourceLocation(ModularItemFrame.MOD_ID, "module_t2_vacuum");
    public static final ResourceLocation BG_LOC = new ResourceLocation(ModularItemFrame.MOD_ID, "blocks/module_t2_vacuum");
    private static final String NBT_MODE = "rangemode";
    private static final String NBT_RANGEX = "rangex";
    private static final String NBT_RANGEY = "rangey";
    private static final String NBT_RANGEZ = "rangez";

    private EnumMode mode = EnumMode.X;
    private int rangeX = ConfigValues.BaseVacuumRange;
    private int rangeY = ConfigValues.BaseVacuumRange;
    private int rangeZ = ConfigValues.BaseVacuumRange;

    @Nonnull
    @Override
    @SideOnly(Side.CLIENT)
    public ResourceLocation frontTexture() {
        return BG_LOC;
    }

    @Nonnull
    @Override
    @SideOnly(Side.CLIENT)
    public ResourceLocation innerTexture() {
        return BlockModularFrame.INNER_HARD_LOC;
    }

    @Override
    public String getModuleName() {
        return I18n.format("modularitemframe.module.vacuum");
    }

    @Override
    public void screw(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull EntityPlayer playerIn, ItemStack driver) {
        if (world.isRemote) return;

        if (playerIn.isSneaking()) {
            int modeIdx = mode.getIndex() + 1;
            if (modeIdx == EnumMode.values().length) modeIdx = 0;
            mode = EnumMode.VALUES[modeIdx];
            playerIn.sendMessage(new TextComponentTranslation("modularitemframe.message.vacuum_mode_change", mode.getName()));
        } else {
            adjustRange(playerIn);
        }
        tile.markDirty();
    }

    @Override
    public boolean onBlockActivated(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull EntityPlayer playerIn, @Nonnull EnumHand hand, @Nonnull EnumFacing facing, float hitX, float hitY, float hitZ) {
        return false;
    }

    @Override
    public void tick(@Nonnull World world, @Nonnull BlockPos pos) {
        if (world.getTotalWorldTime() % (60 - 10 * tile.getSpeedUpCount()) != 0) return;

        IItemHandlerModifiable handler = (IItemHandlerModifiable) tile.getAttachedInventory();
        if (handler != null) {
            List<EntityItem> entities = world.getEntitiesWithinAABB(EntityItem.class, getVacuumBB(pos));
            for (EntityItem entity : entities) {
                ItemStack entityStack = entity.getItem();
                if (entity.isDead || entityStack.isEmpty() || ItemUtils.getFittingSlot(handler, entityStack) < 0)
                    continue;

                ItemStack remain = ItemUtils.giveStack(handler, entityStack);
                if (remain.isEmpty()) entity.setDead();
                else entity.setItem(remain);
                NetworkHandler.sendAround(new SpawnParticlesPacket(EnumParticleTypes.EXPLOSION_NORMAL.getParticleID(), entity.getPosition(), 1), entity.getPosition(), entity.dimension);
                break;
            }
        }
    }

    @Nonnull
    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound nbt = super.serializeNBT();
        nbt.setInteger(NBT_MODE, mode.getIndex());
        nbt.setInteger(NBT_RANGEX, rangeX);
        nbt.setInteger(NBT_RANGEY, rangeY);
        nbt.setInteger(NBT_RANGEZ, rangeZ);
        return nbt;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        super.deserializeNBT(nbt);
        if (nbt.hasKey(NBT_MODE)) mode = EnumMode.VALUES[nbt.getInteger(NBT_MODE)];
        if (nbt.hasKey(NBT_RANGEX)) rangeX = nbt.getInteger(NBT_RANGEX);
        if (nbt.hasKey(NBT_RANGEY)) rangeY = nbt.getInteger(NBT_RANGEY);
        if (nbt.hasKey(NBT_RANGEZ)) rangeZ = nbt.getInteger(NBT_RANGEZ);
    }

    private AxisAlignedBB getVacuumBB(@Nonnull BlockPos pos) {
        switch (tile.blockFacing()) {
            case DOWN:
                return new AxisAlignedBB(pos.add(-rangeX, 0, -rangeZ), pos.add(rangeX, rangeY, rangeZ));
            case UP:
                return new AxisAlignedBB(pos.add(-rangeX, 0, -rangeZ), pos.add(rangeX, -rangeY, rangeZ));
            case NORTH:
                return new AxisAlignedBB(pos.add(-rangeX, -rangeY, 0), pos.add(rangeX, rangeY, rangeZ));
            case SOUTH:
                return new AxisAlignedBB(pos.add(-rangeX, -rangeY, 0), pos.add(rangeX, rangeY, -rangeZ));
            case WEST:
                return new AxisAlignedBB(pos.add(0, -rangeY, -rangeZ), pos.add(-rangeX, rangeY, rangeZ));
            case EAST:
                return new AxisAlignedBB(pos.add(0, -rangeY, -rangeZ), pos.add(rangeX, rangeY, rangeZ));
        }
        return new AxisAlignedBB(pos, pos.add(1, 1, 1));
    }

    private void adjustRange(@Nonnull EntityPlayer playerIn) {
        int maxRange = ConfigValues.BaseVacuumRange + tile.getRangeUpCount();
        if (maxRange > 1) {
            int r = 0;
            switch (mode) {
                case X:
                    rangeX++;
                    if (rangeX > maxRange) rangeX = 1;
                    r = rangeX;
                    break;
                case Y:
                    rangeY++;
                    if (rangeY > maxRange) rangeY = 1;
                    r = rangeY;
                    break;
                case Z:
                    rangeZ++;
                    if (rangeZ > maxRange) rangeZ = 1;
                    r = rangeZ;
                    break;
            }
            playerIn.sendMessage(new TextComponentTranslation("modularitemframe.message.vacuum_range_change", mode.getName(), r));
        }
    }

    public enum EnumMode {
        X(0, "x"), Y(1, "y"), Z(2, "z");

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
            return this.name;
        }


        static {
            for (EnumMode enummode : values())
                VALUES[enummode.index] = enummode;
        }
    }
}
