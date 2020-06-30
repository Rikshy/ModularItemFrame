package de.shyrik.modularitemframe.common.module.t3;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.shyrik.modularitemframe.ModularItemFrame;
import de.shyrik.modularitemframe.api.ModuleBase;
import de.shyrik.modularitemframe.api.util.FrameEnderRenderer;
import de.shyrik.modularitemframe.api.util.ItemHelper;
import de.shyrik.modularitemframe.client.render.FrameRenderer;
import de.shyrik.modularitemframe.common.block.BlockModularFrame;
import de.shyrik.modularitemframe.common.block.TileModularFrame;
import de.shyrik.modularitemframe.common.network.NetworkHandler;
import de.shyrik.modularitemframe.common.network.packet.SpawnParticlesPacket;
import de.shyrik.modularitemframe.init.ConfigValues;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.TileEntity;
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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

public class ItemModuleTeleporter extends ModuleBase {

    public static final ResourceLocation LOC = new ResourceLocation(ModularItemFrame.MOD_ID, "module_t3_itemtele");
    public static final ResourceLocation BG_IN = new ResourceLocation(ModularItemFrame.MOD_ID, "block/module_t3_itemtelein");
    public static final ResourceLocation BG_OUT = new ResourceLocation(ModularItemFrame.MOD_ID, "block/module_t3_itemteleout");
    public static final ResourceLocation BG_NONE = new ResourceLocation(ModularItemFrame.MOD_ID, "block/module_t3_itemtelenone");

    private static final String NBT_LINK = "item_linked_pos";
    private static final String NBT_LINKX = "linked_posX";
    private static final String NBT_LINKY = "linked_posY";
    private static final String NBT_LINKZ = "linked_posZ";
    private static final String NBT_DIR = "direction";

    private BlockPos linkedLoc = null;
    private EnumMode direction = EnumMode.NONE;

    @Override
    public ResourceLocation getId() {
        return LOC;
    }

    @Nonnull
    @Override
    @OnlyIn(Dist.CLIENT)
    public ResourceLocation frontTexture() {
        switch (direction) {
            case VACUUM:
                return BG_IN;
            case DISPENSE:
                return BG_OUT;
            case NONE:
                return BG_NONE;
        }
        return BG_NONE;
    }

    @Nonnull
    @Override
    @OnlyIn(Dist.CLIENT)
    public ResourceLocation innerTexture() {
        return BlockModularFrame.INNER_HARDEST_LOC;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void specialRendering(FrameRenderer renderer, @Nonnull MatrixStack matrixStack, float partialTicks, @Nonnull IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {
        BlockPos pos = tile.getPos();

        if(direction == EnumMode.NONE) {
            FrameEnderRenderer.render(matrixStack, buffer, pos, renderer.getDispatcher().renderInfo.getProjectedView(), info -> {
                float x = pos.getX(), y = pos.getY(), z = pos.getZ();
                switch (tile.blockFacing()) {
                    case DOWN:
                        info.buffer.pos(info.matrix, x + 0.7f, y + 0.08f, z + 0.7f).color(info.color1, info.color2, info.color3, 1.0F).endVertex();
                        info.buffer.pos(info.matrix, x + 0.7f, y + 0.08f, z + 0.3f).color(info.color1, info.color2, info.color3, 1.0F).endVertex();
                        info.buffer.pos(info.matrix, x + 0.3f, y + 0.08f, z + 0.3f).color(info.color1, info.color2, info.color3, 1.0F).endVertex();
                        info.buffer.pos(info.matrix, x + 0.3f, y + 0.08f, z + 0.7f).color(info.color1, info.color2, info.color3, 1.0F).endVertex();
                        break;
                    case UP:
                        info.buffer.pos(info.matrix, x + 0.7f, y + 0.92f, z + 0.3f).color(info.color1, info.color2, info.color3, 1.0F).endVertex();
                        info.buffer.pos(info.matrix, x + 0.7f, y + 0.92f, z + 0.7f).color(info.color1, info.color2, info.color3, 1.0F).endVertex();
                        info.buffer.pos(info.matrix, x + 0.3f, y + 0.92f, z + 0.7f).color(info.color1, info.color2, info.color3, 1.0F).endVertex();
                        info.buffer.pos(info.matrix, x + 0.3f, y + 0.92f, z + 0.3f).color(info.color1, info.color2, info.color3, 1.0F).endVertex();
                        break;
                    case NORTH:
                        info.buffer.pos(info.matrix, x + 0.7f, y + 0.7f, z + 0.08f).color(info.color1, info.color2, info.color3, 1.0F).endVertex();
                        info.buffer.pos(info.matrix, x + 0.3f, y + 0.7f, z + 0.08f).color(info.color1, info.color2, info.color3, 1.0F).endVertex();
                        info.buffer.pos(info.matrix, x + 0.3f, y + 0.3f, z + 0.08f).color(info.color1, info.color2, info.color3, 1.0F).endVertex();
                        info.buffer.pos(info.matrix, x + 0.7f, y + 0.3f, z + 0.08f).color(info.color1, info.color2, info.color3, 1.0F).endVertex();
                        break;
                    case SOUTH:
                        info.buffer.pos(info.matrix, x + 0.3f, y + 0.7f, z + 0.92f).color(info.color1, info.color2, info.color3, 1.0F).endVertex();
                        info.buffer.pos(info.matrix, x + 0.7f, y + 0.7f, z + 0.92f).color(info.color1, info.color2, info.color3, 1.0F).endVertex();
                        info.buffer.pos(info.matrix, x + 0.7f, y + 0.3f, z + 0.92f).color(info.color1, info.color2, info.color3, 1.0F).endVertex();
                        info.buffer.pos(info.matrix, x + 0.3f, y + 0.3f, z + 0.92f).color(info.color1, info.color2, info.color3, 1.0F).endVertex();
                        break;
                    case WEST:
                        info.buffer.pos(info.matrix, x + 0.08f, y + 0.7f, z + 0.3f).color(info.color1, info.color2, info.color3, 1.0F).endVertex();
                        info.buffer.pos(info.matrix, x + 0.08f, y + 0.7f, z + 0.7f).color(info.color1, info.color2, info.color3, 1.0F).endVertex();
                        info.buffer.pos(info.matrix, x + 0.08f, y + 0.3f, z + 0.7f).color(info.color1, info.color2, info.color3, 1.0F).endVertex();
                        info.buffer.pos(info.matrix, x + 0.08f, y + 0.3f, z + 0.3f).color(info.color1, info.color2, info.color3, 1.0F).endVertex();
                        break;
                    case EAST:
                        info.buffer.pos(info.matrix, x + 0.92f, y + 0.7f, z + 0.7f).color(info.color1, info.color2, info.color3, 1.0F).endVertex();
                        info.buffer.pos(info.matrix, x + 0.92f, y + 0.7f, z + 0.3f).color(info.color1, info.color2, info.color3, 1.0F).endVertex();
                        info.buffer.pos(info.matrix, x + 0.92f, y + 0.3f, z + 0.3f).color(info.color1, info.color2, info.color3, 1.0F).endVertex();
                        info.buffer.pos(info.matrix, x + 0.92f, y + 0.3f, z + 0.7f).color(info.color1, info.color2, info.color3, 1.0F).endVertex();
                        break;
                }
                return true;
            });
        }
    }

    @Override
    public String getModuleName() {
        return I18n.format("modularitemframe.module.itemtele");
    }

    @Override
    public void onFrameUpgradesChanged() {
        super.onFrameUpgradesChanged();

        if (linkedLoc != null) {
            if (!tile.getPos().withinDistance(linkedLoc, ConfigValues.BaseTeleportRange + (tile.getRangeUpCount() * 10))) {
                linkedLoc = null;
                direction = EnumMode.NONE;
            }
        }
    }

    @Override
    public void screw(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull PlayerEntity playerIn, ItemStack driver) {
        CompoundNBT nbt = driver.getTag();
        if (playerIn.isSneaking()) {
            if (nbt == null) nbt = new CompoundNBT();
            nbt.putLong(NBT_LINK, tile.getPos().toLong());
            driver.setTag(nbt);
            playerIn.sendMessage(new TranslationTextComponent("modularitemframe.message.loc_saved"));
        } else {
            if (nbt != null && nbt.contains(NBT_LINK)) {
                BlockPos tmp = BlockPos.fromLong(nbt.getLong(NBT_LINK));
                TileEntity targetTile = tile.getWorld().getTileEntity(tmp);
                int countRange = tile.getRangeUpCount();
                if (!(targetTile instanceof TileModularFrame) || !((((TileModularFrame) targetTile).module instanceof ItemModuleTeleporter)))
                    playerIn.sendMessage(new TranslationTextComponent("modularitemframe.message.invalid_target"));
                else if (!tile.getPos().withinDistance(tmp, ConfigValues.BaseTeleportRange + (countRange * 10))) {
                    playerIn.sendMessage(new TranslationTextComponent("modularitemframe.message.too_far", ConfigValues.BaseTeleportRange + (countRange * 10)));
                } else {
                    linkedLoc = tmp;
                    direction = EnumMode.DISPENSE;
                    reloadModel = true;

                    ItemModuleTeleporter targetModule = (ItemModuleTeleporter) ((TileModularFrame) targetTile).module;
                    targetModule.linkedLoc = tile.getPos();
                    targetModule.direction = EnumMode.VACUUM;
                    targetModule.reloadModel = true;

                    playerIn.sendMessage(new TranslationTextComponent("modularitemframe.message.link_established"));
                    nbt.remove(NBT_LINK);
                    driver.setTag(nbt);

                    targetTile.markDirty();
                    tile.markDirty();
                }
            }
        }
    }

    @Override
    public ActionResultType onBlockActivated(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nonnull PlayerEntity playerIn, @Nonnull Hand hand, @Nonnull Direction facing, BlockRayTraceResult hit) {
        if (direction != EnumMode.VACUUM) return ActionResultType.FAIL;
        if (!hasValidConnection(worldIn)) return ActionResultType.FAIL;

        ItemStack held = playerIn.getHeldItem(hand);

        if (!held.isEmpty()) {
            ItemHelper.ejectStack(worldIn, linkedLoc, worldIn.getBlockState(linkedLoc).get(BlockModularFrame.FACING), held);
            held.setCount(0);
        }
        return ActionResultType.SUCCESS;
    }

    @Override
    public void onRemove(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull Direction facing, @Nullable PlayerEntity playerIn) {
        if (hasValidConnection(worldIn)) {
            ItemModuleTeleporter targetModule = (ItemModuleTeleporter) ((TileModularFrame) Objects.requireNonNull(worldIn.getTileEntity(linkedLoc))).module;
            targetModule.linkedLoc = null;
            targetModule.direction = EnumMode.NONE;
            targetModule.reloadModel = true;
        }
    }

    @Override
    public void tick(@Nonnull World world, @Nonnull BlockPos pos) {
        if (direction != EnumMode.VACUUM) return;
        if (ConfigValues.DisableAutomaticItemTransfer) return;
        if (!hasValidConnection(world)) return;
        if (world.getGameTime() % (60 - 10 * tile.getSpeedUpCount()) != 0) return;

        List<ItemEntity> entities = world.getEntitiesWithinAABB(ItemEntity.class, getVacuumBB(pos));
        for (ItemEntity entity : entities) {
            ItemStack entityStack = entity.getItem();
            if (!entity.isAlive() || entityStack.isEmpty()) continue;

            ItemHelper.ejectStack(world, linkedLoc, world.getBlockState(linkedLoc).get(BlockModularFrame.FACING), entityStack);
            entity.remove();
            NetworkHandler.sendAround(new SpawnParticlesPacket(ParticleTypes.EXPLOSION.getRegistryName(), entity.getPosition(), 1), world, entity.getPosition(), 32);
            break;
        }
    }

    @Nonnull
    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT compound = super.serializeNBT();
        if (linkedLoc != null) {
            compound.putInt(NBT_LINKX, linkedLoc.getX());
            compound.putInt(NBT_LINKY, linkedLoc.getY());
            compound.putInt(NBT_LINKZ, linkedLoc.getZ());
            compound.putInt(NBT_DIR, direction.index);
        }
        return compound;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        super.deserializeNBT(nbt);
        if (nbt.contains(NBT_LINKX))
            linkedLoc = new BlockPos(nbt.getInt(NBT_LINKX), nbt.getInt(NBT_LINKY), nbt.getInt(NBT_LINKZ));
        if (nbt.contains(NBT_DIR)) direction = EnumMode.values()[nbt.getInt(NBT_DIR)];
    }

    private boolean hasValidConnection(@Nonnull World world) {
        if (linkedLoc == null) return false;
        TileEntity tile = world.getTileEntity(linkedLoc);
        if (!(tile instanceof TileModularFrame)
                || !(((TileModularFrame) tile).module instanceof ItemModuleTeleporter)
                || ((ItemModuleTeleporter) ((TileModularFrame) tile).module).direction != EnumMode.DISPENSE)
            return false;
        return true;
    }

    private AxisAlignedBB getVacuumBB(@Nonnull BlockPos pos) {
        int range = ConfigValues.BaseVacuumRange + tile.getRangeUpCount();
        switch (tile.blockFacing()) {
            case DOWN:
                return new AxisAlignedBB(pos.add(-5, 0, -5), pos.add(5, -5, 5));
            case UP:
                return new AxisAlignedBB(pos.add(-5, 0, -5), pos.add(5, 5, 5));
            case NORTH:
                return new AxisAlignedBB(pos.add(-5, -5, 0), pos.add(5, 5, -5));
            case SOUTH:
                return new AxisAlignedBB(pos.add(-5, -5, 0), pos.add(5, 5, 5));
            case WEST:
                return new AxisAlignedBB(pos.add(0, -5, -5), pos.add(5, 5, 5));
            case EAST:
                return new AxisAlignedBB(pos.add(0, -5, -5), pos.add(-5, 5, 5));
        }
        return new AxisAlignedBB(pos, pos.add(1, 1, 1));
    }


    public enum EnumMode {
        VACUUM(0, "modularitemframe.mode.in"),
        DISPENSE(1, "modularitemframe.mode.out"),
        NONE(2, "modularitemframe.mode.no");

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
