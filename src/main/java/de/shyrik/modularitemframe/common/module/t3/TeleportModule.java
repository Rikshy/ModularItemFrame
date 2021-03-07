package de.shyrik.modularitemframe.common.module.t3;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.shyrik.modularitemframe.ModularItemFrame;
import de.shyrik.modularitemframe.common.block.ModularFrameBlock;
import de.shyrik.modularitemframe.common.block.ModularFrameTile;
import modularitemframe.api.ModuleTier;
import modularitemframe.api.accessors.IFrameRenderer;
import modularitemframe.api.ModuleBase;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.TextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.FakePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TeleportModule extends ModuleBase {

    public static final ResourceLocation ID = new ResourceLocation(ModularItemFrame.MOD_ID, "module_t3_tele");
    public static final ResourceLocation BG = new ResourceLocation(ModularItemFrame.MOD_ID, "module/module_t3_tele");
    private static final TextComponent NAME = new TranslationTextComponent("modularitemframe.module.tele");

    private static final String NBT_LINK = "linked_pos";
    private static final String NBT_DIM = "linked_dim";

    private BlockPos linkedLoc = null;
    private ResourceLocation linkedDim = null;

    @NotNull
    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @NotNull
    @Override
    public TextComponent getName() {
        return NAME;
    }

    @NotNull
    @Override
    public ModuleTier moduleTier() {
        return ModuleTier.T3;
    }

    @NotNull
    @Override
    public ResourceLocation frontTexture() {
        return BG;
    }

    @Override
    public void specialRendering(@NotNull IFrameRenderer renderer, float partialTicks, @NotNull MatrixStack matrixStack, @NotNull IRenderTypeBuffer buffer, int light, int overlay) {
        if (linkedLoc != null) {
            renderer.renderEnder(matrixStack, buffer, 0.85F, 0.08F, 0.14F);
        }
    }

    @Override
    public ActionResultType onUse(@NotNull World world, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull PlayerEntity player, @NotNull Hand hand, @NotNull Direction facing, BlockRayTraceResult hit) {
        if (player instanceof FakePlayer) return ActionResultType.FAIL;

        if (!world.isRemote && linkedLoc != null) {
            BlockPos target = getTargetLocation(world);
            if (target == null) {
                player.sendMessage(new TranslationTextComponent("modularitemframe.message.teleport.location_blocked"), Util.DUMMY_UUID);
                return ActionResultType.FAIL;
            }

            player.removePassengers();
            player.stopRiding();

            World targetWorld = getDimWorld(world);
            double offset = targetWorld.getBlockState(linkedLoc).get(ModularFrameBlock.FACING) == Direction.UP ? 0.15 : 0;
            if (world.getDimensionKey().compareTo(targetWorld.getDimensionKey()) != 0){
                player.changeDimension((ServerWorld) targetWorld);
            }
            player.teleportKeepLoaded(target.getX() + 0.5D, target.getY() + offset, target.getZ() + 0.5D);
            world.playSound(null, target, SoundEvents.ITEM_CHORUS_FRUIT_TELEPORT, SoundCategory.PLAYERS, 1F, 1F);
        }
        return ActionResultType.SUCCESS;
    }

    @Override
    public void screw(@NotNull World world, @NotNull BlockPos pos, @NotNull PlayerEntity player, ItemStack driver) {
        CompoundNBT nbt = driver.getTag();
        if (player.isSneaking()) {
            if (nbt == null) nbt = new CompoundNBT();
            nbt.putLong(NBT_LINK, frame.getPos().toLong());
            nbt.putString(NBT_DIM, world.getDimensionKey().getLocation().toString());
            driver.setTag(nbt);
            player.sendMessage(new TranslationTextComponent("modularitemframe.message.teleport.loc_saved"), Util.DUMMY_UUID);
        } else {
            if (nbt != null && nbt.contains(NBT_LINK)) {
                ResourceLocation dim = new ResourceLocation(nbt.getString(NBT_DIM));
                BlockPos tmp = BlockPos.fromLong(nbt.getLong(NBT_LINK));
                World targetWorld = getDimWorld(world, dim);
                TileEntity targetTile = targetWorld.getTileEntity(tmp);

                if (!(targetTile instanceof ModularFrameTile) || !((((ModularFrameTile) targetTile).getModule() instanceof TeleportModule))) {
                    player.sendMessage(new TranslationTextComponent("modularitemframe.message.teleport.invalid_target"), Util.DUMMY_UUID);
                    return;
                }
                ModularFrameTile targetFrame = (ModularFrameTile) targetTile;

                if (!isInRange(targetFrame, dim.compareTo(world.getDimensionKey().getLocation()) != 0)) {
                    player.sendMessage(new TranslationTextComponent("modularitemframe.message.teleport.too_far"), Util.DUMMY_UUID);
                    return;
                }

                breakLink(world);
                linkedLoc = tmp;
                linkedDim = dim;

                TeleportModule targetModule = (TeleportModule) targetFrame.getModule();
                targetModule.breakLink(world);
                targetModule.linkedLoc = frame.getPos();
                targetModule.linkedDim = world.getDimensionKey().getLocation();

                player.sendMessage(new TranslationTextComponent("modularitemframe.message.teleport.link_established"), Util.DUMMY_UUID);
                nbt.remove(NBT_LINK);
                nbt.remove(NBT_DIM);
                driver.setTag(nbt);

                targetTile.markDirty();
                markDirty();
            }
        }
    }

    @Override
    public void onRemove(@NotNull World world, @NotNull BlockPos pos, @NotNull Direction facing, @Nullable PlayerEntity player, @NotNull ItemStack moduleStack) {
        super.onRemove(world, pos, facing, player, moduleStack);
        if (!world.isRemote) {
            breakLink(world);
        }
    }

    @Override
    public void onFrameUpgradesChanged(World world, BlockPos pos, Direction facing) {
        super.onFrameUpgradesChanged(world, pos, facing);
        if (!world.isRemote && linkedLoc != null) {
            World targetWorld = getDimWorld(world);
            TileEntity targetTile = targetWorld.getTileEntity(linkedLoc);
            if (!(targetTile instanceof ModularFrameTile) || !isInRange((ModularFrameTile) targetTile, targetWorld != world)) {
                breakLink(world);
            }
        }
    }

    @NotNull
    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = super.serializeNBT();
        if (linkedLoc != null) {
            nbt.putLong(NBT_LINK, linkedLoc.toLong());
        }
        if (linkedDim != null) {
            nbt.putString(NBT_DIM, linkedDim.toString());
        }
        return nbt;
    }

    @Override
    public void deserializeNBT(@NotNull CompoundNBT nbt) {
        super.deserializeNBT(nbt);
        linkedLoc = nbt.contains(NBT_LINK) ? BlockPos.fromLong(nbt.getLong(NBT_LINK)) : null;
        linkedDim = nbt.contains(NBT_DIM) ? ResourceLocation.tryCreate(nbt.getString(NBT_DIM)) : null;
    }

    private BlockPos getTargetLocation(World world) {
        World targetWorld = getDimWorld(world);
        if (targetWorld.getBlockState(linkedLoc).get(ModularFrameBlock.FACING) == Direction.DOWN) {
            BlockPos pos2 = linkedLoc.offset(Direction.DOWN);
            if (!targetWorld.getBlockState(pos2).getMaterial().blocksMovement())
                return linkedLoc;
        } else {
            BlockPos pos2 = linkedLoc.offset(Direction.DOWN);
            if (!targetWorld.getBlockState(pos2).getMaterial().blocksMovement())
                return pos2;
            pos2 = linkedLoc.offset(Direction.UP);
            if (!targetWorld.getBlockState(pos2).getMaterial().blocksMovement())
                return linkedLoc;
        }

        return null;
    }

    private void breakLink(World world) {
        if (linkedLoc != null) {
            World targetWorld = getDimWorld(world);
            TileEntity be = targetWorld.getTileEntity(linkedLoc);
            if (be instanceof ModularFrameTile && ((ModularFrameTile) be).getModule() instanceof TeleportModule) {
                TeleportModule targetModule = (TeleportModule) ((ModularFrameTile) be).getModule();
                targetModule.linkedLoc = null;
                targetModule.linkedDim = null;
                targetModule.markDirty();
            }

            linkedLoc = null;
            linkedDim = null;
            markDirty();
        }
    }

    private World getDimWorld(World sourceWorld) {
        return getDimWorld(sourceWorld, linkedDim);
    }

    private World getDimWorld(World sourceWorld, ResourceLocation targetId) {
        World targetWorld = sourceWorld;
        if (targetId != null && targetId.compareTo(sourceWorld.getDimensionKey().getLocation()) != 0) {
            targetWorld = sourceWorld.getServer().getWorld(RegistryKey.getOrCreateKey(Registry.WORLD_KEY, targetId));
        }
        return targetWorld;
    }

    private boolean isInRange(ModularFrameTile targetFrame, boolean isCrossDim) {
        if (targetFrame.hasInfinity() && frame.hasInfinity()) {
            return true;
        } else if (!isCrossDim) {
            int sourceRange = ModularItemFrame.config.getBaseTeleportRange() + (frame.getRangeUpCount() * 10);
            int targetRange = ModularItemFrame.config.getBaseTeleportRange() + (targetFrame.getRangeUpCount() * 10);
            return (frame.hasInfinity() || frame.getPos().withinDistance(targetFrame.getPos(), sourceRange)) &&
                    (targetFrame.hasInfinity() || targetFrame.getPos().withinDistance(frame.getPos(), targetRange));
        }

        return false;
    }
}
