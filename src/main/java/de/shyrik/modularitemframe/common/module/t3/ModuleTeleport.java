package de.shyrik.modularitemframe.common.module.t3;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.shyrik.modularitemframe.ModularItemFrame;
import de.shyrik.modularitemframe.api.util.FrameEnderRenderer;
import de.shyrik.modularitemframe.common.network.packet.TeleportEffectPacket;
import de.shyrik.modularitemframe.init.ConfigValues;
import de.shyrik.modularitemframe.api.ModuleBase;
import de.shyrik.modularitemframe.client.render.FrameRenderer;
import de.shyrik.modularitemframe.common.block.TileModularFrame;
import de.shyrik.modularitemframe.common.network.NetworkHandler;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.FakePlayer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

public class ModuleTeleport extends ModuleBase {

    public static final ResourceLocation LOC = new ResourceLocation(ModularItemFrame.MOD_ID, "module_t3_tele");

    private static final String NBT_LINK = "linked_pos";
    private static final String NBT_LINKX = "linked_posX";
    private static final String NBT_LINKY = "linked_posY";
    private static final String NBT_LINKZ = "linked_posZ";

    private BlockPos linkedLoc = null;

    @Override
    public ResourceLocation getId() {
        return LOC;
    }

    @Nonnull
    @Override
    @OnlyIn(Dist.CLIENT)
    public ResourceLocation frontTexture() {
        return new ResourceLocation(ModularItemFrame.MOD_ID, "block/module_t1_item");
    }

    @Override
    public String getModuleName() {
        return I18n.format("modularitemframe.module.tele");
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void specialRendering(FrameRenderer renderer, @Nonnull MatrixStack matrixStack, float partialTicks, @Nonnull IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {
        BlockPos pos = tile.getPos();
        FrameEnderRenderer.render(matrixStack, buffer, pos, renderer.getDispatcher().renderInfo.getProjectedView(), info -> {
            float x = pos.getX(), y = pos.getY(), z = pos.getZ();
            switch (tile.blockFacing()) {
                case DOWN:
                    info.buffer.pos(info.matrix,x + 0.85f, y + 0.08f, z + 0.85f).color(info.color1, info.color2, info.color3, 1.0F).endVertex();
                    info.buffer.pos(info.matrix,x + 0.85f, y + 0.08f, z + 0.14f).color(info.color1, info.color2, info.color3, 1.0F).endVertex();
                    info.buffer.pos(info.matrix,x + 0.14f, y + 0.08f, z + 0.14f).color(info.color1, info.color2, info.color3, 1.0F).endVertex();
                    info.buffer.pos(info.matrix,x + 0.14f, y + 0.08f, z + 0.85f).color(info.color1, info.color2, info.color3, 1.0F).endVertex();
                    break;
                case UP:
                    info.buffer.pos(info.matrix,x + 0.85f, y + 0.92f, z + 0.16f).color(info.color1, info.color2, info.color3, 1.0F).endVertex();
                    info.buffer.pos(info.matrix,x + 0.85f, y + 0.92f, z + 0.85f).color(info.color1, info.color2, info.color3, 1.0F).endVertex();
                    info.buffer.pos(info.matrix,x + 0.16f, y + 0.92f, z + 0.85f).color(info.color1, info.color2, info.color3, 1.0F).endVertex();
                    info.buffer.pos(info.matrix,x + 0.16f, y + 0.92f, z + 0.16f).color(info.color1, info.color2, info.color3, 1.0F).endVertex();
                    break;
                case NORTH:
                    info.buffer.pos(info.matrix,x + 0.85f, y + 0.85f, z + 0.08f).color(info.color1, info.color2, info.color3, 1.0F).endVertex();
                    info.buffer.pos(info.matrix,x + 0.14f, y + 0.85f, z + 0.08f).color(info.color1, info.color2, info.color3, 1.0F).endVertex();
                    info.buffer.pos(info.matrix,x + 0.14f, y + 0.14f, z + 0.08f).color(info.color1, info.color2, info.color3, 1.0F).endVertex();
                    info.buffer.pos(info.matrix,x + 0.85f, y + 0.14f, z + 0.08f).color(info.color1, info.color2, info.color3, 1.0F).endVertex();
                    break;
                case SOUTH:
                    info.buffer.pos(info.matrix,x + 0.14f, y + 0.85f, z + 0.92f).color(info.color1, info.color2, info.color3, 1.0F).endVertex();
                    info.buffer.pos(info.matrix,x + 0.85f, y + 0.85f, z + 0.92f).color(info.color1, info.color2, info.color3, 1.0F).endVertex();
                    info.buffer.pos(info.matrix,x + 0.85f, y + 0.14f, z + 0.92f).color(info.color1, info.color2, info.color3, 1.0F).endVertex();
                    info.buffer.pos(info.matrix,x + 0.14f, y + 0.14f, z + 0.92f).color(info.color1, info.color2, info.color3, 1.0F).endVertex();
                    break;
                case WEST:
                    info.buffer.pos(info.matrix,x + 0.08f, y + 0.85f, z + 0.16f).color(info.color1, info.color2, info.color3, 1.0F).endVertex();
                    info.buffer.pos(info.matrix,x + 0.08f, y + 0.85f, z + 0.85f).color(info.color1, info.color2, info.color3, 1.0F).endVertex();
                    info.buffer.pos(info.matrix,x + 0.08f, y + 0.16f, z + 0.85f).color(info.color1, info.color2, info.color3, 1.0F).endVertex();
                    info.buffer.pos(info.matrix,x + 0.08f, y + 0.16f, z + 0.16f).color(info.color1, info.color2, info.color3, 1.0F).endVertex();
                    break;
                case EAST:
                    info.buffer.pos(info.matrix,x + 0.92f, y + 0.85f, z + 0.85f).color(info.color1, info.color2, info.color3, 1.0F).endVertex();
                    info.buffer.pos(info.matrix,x + 0.92f, y + 0.85f, z + 0.16f).color(info.color1, info.color2, info.color3, 1.0F).endVertex();
                    info.buffer.pos(info.matrix,x + 0.92f, y + 0.16f, z + 0.16f).color(info.color1, info.color2, info.color3, 1.0F).endVertex();
                    info.buffer.pos(info.matrix,x + 0.92f, y + 0.16f, z + 0.85f).color(info.color1, info.color2, info.color3, 1.0F).endVertex();
                    break;
            }
            return true;
        });
    }

    @Override
    public void onFrameUpgradesChanged() {
        super.onFrameUpgradesChanged();

        if (linkedLoc != null) {
            if (!tile.getPos().withinDistance(linkedLoc, ConfigValues.BaseTeleportRange + (tile.getRangeUpCount() * 10))) {
                linkedLoc = null;
            }
        }
    }

    @Override
    public ActionResultType onBlockActivated(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nonnull PlayerEntity player, @Nonnull Hand hand, @Nonnull Direction facing, BlockRayTraceResult hit) {
        if (player instanceof FakePlayer) return ActionResultType.FAIL;

        if (!world.isRemote) {
            if (hasValidConnection(world, player)) {
                BlockPos target;
                if (tile.blockFacing().getAxis().isHorizontal() || tile.blockFacing() == Direction.UP)
                    target = linkedLoc.offset(Direction.DOWN);
                else target = linkedLoc;

                if (player.isBeingRidden()) {
                    player.removePassengers();
                }

                player.stopRiding();

                if (player.attemptTeleport(target.getX() + 0.5D, target.getY() + 0.5D, target.getZ() + 0.5D, true)) {
                    NetworkHandler.sendAround(new TeleportEffectPacket(player.getPosition()), world, player.getPosition(), 32);
                    NetworkHandler.sendAround(new TeleportEffectPacket(target), world, target, 32);
                }
            }
        }
        return ActionResultType.SUCCESS;
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
                if (tile.getPos().withinDistance(tmp, 1)) return;
                TileEntity targetTile = tile.getWorld().getTileEntity(tmp);
                int countRange = tile.getRangeUpCount();
                if (!(targetTile instanceof TileModularFrame) || !((((TileModularFrame) targetTile).module instanceof ModuleTeleport)))
                    playerIn.sendMessage(new TranslationTextComponent("modularitemframe.message.invalid_target"));
                else if (!tile.getPos().withinDistance(tmp, ConfigValues.BaseTeleportRange + (countRange * 10))) {
                    playerIn.sendMessage(new TranslationTextComponent("modularitemframe.message.too_far", ConfigValues.BaseTeleportRange + (countRange * 10)));
                } else {
                    linkedLoc = tmp;
                    ((ModuleTeleport) ((TileModularFrame) targetTile).module).linkedLoc = tile.getPos();
                    playerIn.sendMessage(new TranslationTextComponent("modularitemframe.message.link_established"));
                    nbt.remove(NBT_LINK);
                    driver.setTag(nbt);
                }
            }
        }
    }

    private boolean isTargetLocationValid(@Nonnull World worldIn) {
        if (tile.blockFacing().getAxis().isHorizontal() || tile.blockFacing() == Direction.UP)
            return worldIn.isAirBlock(linkedLoc.offset(Direction.DOWN));
        else return worldIn.isAirBlock(linkedLoc.offset(Direction.UP));
    }

    private boolean hasValidConnection(@Nonnull World world, @Nullable PlayerEntity player) {
        if (linkedLoc == null) {
            if (player != null) player.sendMessage(new TranslationTextComponent("modularitemframe.message.no_target"));
            return false;
        }
        TileEntity targetTile = world.getTileEntity(linkedLoc);
        if (!(targetTile instanceof TileModularFrame) || !(((TileModularFrame) targetTile).module instanceof ModuleTeleport)) {
            if (player != null)
                player.sendMessage(new TranslationTextComponent("modularitemframe.message.invalid_target"));
            return false;
        }
        if (!isTargetLocationValid(world)) {
            if (player != null)
                player.sendMessage(new TranslationTextComponent("modularitemframe.message.location_blocked"));
            return false;
        }
        return true;
    }

    @Override
    public void onRemove(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull Direction facing, @Nullable PlayerEntity playerIn) {
        if (hasValidConnection(worldIn, null)) {
            ((ModuleTeleport) ((TileModularFrame) Objects.requireNonNull(worldIn.getTileEntity(linkedLoc))).module).linkedLoc = null;
        }
        super.onRemove(worldIn, pos, facing, playerIn);
    }

    @Nonnull
    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT compound = super.serializeNBT();
        if (linkedLoc != null) {
            compound.putInt(NBT_LINKX, linkedLoc.getX());
            compound.putInt(NBT_LINKY, linkedLoc.getY());
            compound.putInt(NBT_LINKZ, linkedLoc.getZ());
        }
        return compound;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        super.deserializeNBT(nbt);
        if (nbt.hasUniqueId(NBT_LINKX))
            linkedLoc = new BlockPos(nbt.getInt(NBT_LINKX), nbt.getInt(NBT_LINKY), nbt.getInt(NBT_LINKZ));
    }
}
