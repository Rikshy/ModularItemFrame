package de.shyrik.modularitemframe.common.module.t2;

import de.shyrik.modularitemframe.ModularItemFrame;
import de.shyrik.modularitemframe.api.ConfigValues;
import de.shyrik.modularitemframe.api.ModuleFrameBase;
import de.shyrik.modularitemframe.api.utils.ItemUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class ModuleVacuum extends ModuleFrameBase {

    private static final String NBT_MODE = "rangemode";
    private static final String NBT_RANGEX = "rangex";
    private static final String NBT_RANGEY = "rangey";
    private static final String NBT_RANGEZ = "rangez";

    private EnumMode mode = EnumMode.X;
    private int rangeX = ConfigValues.MaxVacuumRange;
    private int rangeY = ConfigValues.MaxVacuumRange;
    private int rangeZ = ConfigValues.MaxVacuumRange;

    @Nonnull
    @Override
    public ResourceLocation frontTexture() {
        return new ResourceLocation(ModularItemFrame.MOD_ID, "blocks/vacuum_bg");
    }

    @Nonnull
    @Override
    public ResourceLocation backTexture() {
        return new ResourceLocation(ModularItemFrame.MOD_ID, "blocks/vacuum_bg");
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
        if (world.getTotalWorldTime() % ConfigValues.VacuumCooldown != 0) return;

        IItemHandlerModifiable handler = getNeighborTileItemCap();
        if (handler != null) {
            List<EntityItem> entities = world.getEntitiesWithinAABB(EntityItem.class, getVacuumBB(pos));
            for (EntityItem entity : entities) {
                ItemStack entityStack = entity.getItem();
                if (entity.isDead || entityStack.isEmpty() || ItemUtils.getFittingSlot(handler, entityStack) < 0)
                    continue;

                ItemStack remain = ItemUtils.giveStack(handler, entityStack);
                if (remain.isEmpty()) entity.setDead();
                else entity.setItem(remain);
                world.spawnParticle(EnumParticleTypes.EXPLOSION_NORMAL, entity.posX, entity.posY, entity.posZ, world.rand.nextGaussian(), 0.0D, world.rand.nextGaussian());
                break;
            }
        }
    }

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

    @Nullable
    private IItemHandlerModifiable getNeighborTileItemCap() {
        EnumFacing facing = tile.blockFacing();
        TileEntity te = tile.getNeighbor(facing);

        if (te != null)
            return (IItemHandlerModifiable) te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing.getOpposite());
        return null;
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
        if (ConfigValues.MaxVacuumRange > 1) {
            int r = 0;
            switch (mode) {
                case X:
                    rangeX++;
                    if (rangeX > ConfigValues.MaxVacuumRange) rangeX = 1;
                    r = rangeX;
                    break;
                case Y:
                    rangeY++;
                    if (rangeY > ConfigValues.MaxVacuumRange) rangeY = 1;
                    r = rangeY;
                    break;
                case Z:
                    rangeZ++;
                    if (rangeZ > ConfigValues.MaxVacuumRange) rangeZ = 1;
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
