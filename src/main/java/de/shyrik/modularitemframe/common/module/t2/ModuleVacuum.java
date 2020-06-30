package de.shyrik.modularitemframe.common.module.t2;

import de.shyrik.modularitemframe.ModularItemFrame;
import de.shyrik.modularitemframe.api.util.ItemHandlerHelper;
import de.shyrik.modularitemframe.api.util.ItemHelper;
import de.shyrik.modularitemframe.init.ConfigValues;
import de.shyrik.modularitemframe.api.ModuleBase;
import de.shyrik.modularitemframe.common.block.BlockModularFrame;
import de.shyrik.modularitemframe.common.network.NetworkHandler;
import de.shyrik.modularitemframe.common.network.packet.SpawnParticlesPacket;
import net.minecraft.block.BlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nonnull;
import java.util.List;

public class ModuleVacuum extends ModuleBase {

    public static final ResourceLocation LOC = new ResourceLocation(ModularItemFrame.MOD_ID, "module_t2_vacuum");
    public static final ResourceLocation BG_LOC = new ResourceLocation(ModularItemFrame.MOD_ID, "block/module_t2_vacuum");
    private static final String NBT_MODE = "rangemode";
    private static final String NBT_RANGEX = "rangex";
    private static final String NBT_RANGEY = "rangey";
    private static final String NBT_RANGEZ = "rangez";

    private EnumMode mode = EnumMode.X;
    private int rangeX = ConfigValues.BaseVacuumRange;
    private int rangeY = ConfigValues.BaseVacuumRange;
    private int rangeZ = ConfigValues.BaseVacuumRange;

    @Override
    public ResourceLocation getId() {
        return LOC;
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
    public String getModuleName() {
        return I18n.format("modularitemframe.module.vacuum");
    }

    @Override
    public void screw(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull PlayerEntity playerIn, ItemStack driver) {
        if (world.isRemote) return;

        if (playerIn.isSneaking()) {
            int modeIdx = mode.getIndex() + 1;
            if (modeIdx == EnumMode.values().length) modeIdx = 0;
            mode = EnumMode.VALUES[modeIdx];
            playerIn.sendMessage(new TranslationTextComponent("modularitemframe.message.vacuum_mode_change", mode.getName()));
        } else {
            adjustRange(playerIn);
        }
        tile.markDirty();
    }

    @Override
    public ActionResultType onBlockActivated(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nonnull PlayerEntity playerIn, @Nonnull Hand hand, @Nonnull Direction facing, BlockRayTraceResult hit) {
        return ActionResultType.FAIL;
    }

    @Override
    public void tick(@Nonnull World world, @Nonnull BlockPos pos) {
        if (world.getGameTime() % (60 - 10 * tile.getSpeedUpCount()) != 0) return;

        IItemHandlerModifiable handler = (IItemHandlerModifiable) tile.getAttachedInventory();
        if (handler != null) {
            List<ItemEntity> entities = world.getEntitiesWithinAABB(ItemEntity.class, getVacuumBB(pos));
            for (ItemEntity entity : entities) {
                ItemStack entityStack = entity.getItem();
                if (!entity.isAlive() || entityStack.isEmpty() || ItemHandlerHelper.getFittingSlot(handler, entityStack) < 0)
                    continue;

                ItemStack remain = ItemHandlerHelper.giveStack(handler, entityStack);
                if (remain.isEmpty()) entity.remove();
                else entity.setItem(remain);
                NetworkHandler.sendAround(new SpawnParticlesPacket(ParticleTypes.EXPLOSION.getRegistryName(), entity.getPosition(), 1), entity.world, entity.getPosition(), 32);
                break;
            }
        }
    }

    @Override
    public void onFrameUpgradesChanged() {
        super.onFrameUpgradesChanged();

        int maxRange = ConfigValues.BaseVacuumRange + tile.getRangeUpCount();
        rangeX = Math.min(rangeX, maxRange);
        rangeY = Math.min(rangeY, maxRange);
        rangeZ = Math.min(rangeZ, maxRange);
    }

    @Nonnull
    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = super.serializeNBT();
        nbt.putInt(NBT_MODE, mode.getIndex());
        nbt.putInt(NBT_RANGEX, rangeX);
        nbt.putInt(NBT_RANGEY, rangeY);
        nbt.putInt(NBT_RANGEZ, rangeZ);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        super.deserializeNBT(nbt);
        if (nbt.contains(NBT_MODE)) mode = EnumMode.VALUES[nbt.getInt(NBT_MODE)];
        if (nbt.contains(NBT_RANGEX)) rangeX = nbt.getInt(NBT_RANGEX);
        if (nbt.contains(NBT_RANGEY)) rangeY = nbt.getInt(NBT_RANGEY);
        if (nbt.contains(NBT_RANGEZ)) rangeZ = nbt.getInt(NBT_RANGEZ);
    }

    private AxisAlignedBB getVacuumBB(@Nonnull BlockPos pos) {
        switch (tile.blockFacing()) {
            case DOWN:
                return new AxisAlignedBB(pos.add(-rangeX, 0, -rangeZ), pos.add(rangeX, -rangeY, rangeZ));
            case UP:
                return new AxisAlignedBB(pos.add(-rangeX, 0, -rangeZ), pos.add(rangeX, rangeY, rangeZ));
            case NORTH:
                return new AxisAlignedBB(pos.add(-rangeX, -rangeY, 0), pos.add(rangeX, rangeY, -rangeZ));
            case SOUTH:
                return new AxisAlignedBB(pos.add(-rangeX, -rangeY, 0), pos.add(rangeX, rangeY, rangeZ));
            case WEST:
                return new AxisAlignedBB(pos.add(0, -rangeY, -rangeZ), pos.add(rangeX, rangeY, rangeZ));
            case EAST:
                return new AxisAlignedBB(pos.add(0, -rangeY, -rangeZ), pos.add(-rangeX, rangeY, rangeZ));
        }
        return new AxisAlignedBB(pos, pos.add(1, 1, 1));
    }

    private void adjustRange(@Nonnull PlayerEntity playerIn) {
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
            playerIn.sendMessage(new TranslationTextComponent("modularitemframe.message.vacuum_range_change", mode.getName(), r));
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
