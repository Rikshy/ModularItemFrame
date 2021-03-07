package de.shyrik.modularitemframe.common.module.t3;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import de.shyrik.modularitemframe.ModularItemFrame;
import de.shyrik.modularitemframe.api.ModuleBase;
import de.shyrik.modularitemframe.client.FrameRenderer;
import de.shyrik.modularitemframe.common.block.ModularFrameBlock;
import de.shyrik.modularitemframe.common.block.ModularFrameTile;
import de.shyrik.modularitemframe.util.ItemHelper;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.TextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ItemTeleportModule extends ModuleBase {

    public static final ResourceLocation ID = new ResourceLocation(ModularItemFrame.MOD_ID, "module_t3_itemtele");
    public static final ResourceLocation BG_IN = new ResourceLocation(ModularItemFrame.MOD_ID, "module/module_t3_itemtelein");
    public static final ResourceLocation BG_OUT = new ResourceLocation(ModularItemFrame.MOD_ID, "module/module_t3_itemteleout");
    public static final ResourceLocation BG_NONE = new ResourceLocation(ModularItemFrame.MOD_ID, "module/module_t3_itemtelenone");
    private static final TextComponent NAME = new TranslationTextComponent("modularitemframe.module.itemtele");

    private final List<ResourceLocation> frontTex = ImmutableList.of(
            BG_NONE, BG_IN, BG_OUT
    );

    private static final String NBT_LINK = "item_linked_pos";
    private static final String NBT_DIM = "item_linked_dim";
    private static final String NBT_DIR = "direction";

    private BlockPos linkedLoc = null;
    private ResourceLocation linkedDim = null;
    private EnumMode direction = EnumMode.NONE;


    @NotNull
    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @NotNull
    @Override
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

    @NotNull
    @Override
    public TextComponent getName() {
        return NAME;
    }

    @NotNull
    @Override
    public List<ResourceLocation> getVariantFronts() {
        return frontTex;
    }

    @NotNull
    @Override
    public ResourceLocation innerTexture() {
        return ModularFrameBlock.INNER_HARDEST;
    }

    @Override
    public void specialRendering(@NotNull FrameRenderer renderer, float partialTicks, @NotNull MatrixStack matrixStack, @NotNull IRenderTypeBuffer buffer, int light, int overlay) {
        if(direction != EnumMode.NONE) {
            renderer.renderEnder(frame, matrixStack, buffer, 0.625f, 0.063f, 0.375f);
        }
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
                TileEntity targetBlockEntity = targetWorld.getTileEntity(tmp);

                if (!(targetBlockEntity instanceof ModularFrameTile) || !((((ModularFrameTile) targetBlockEntity).getModule() instanceof ItemTeleportModule))) {
                    player.sendMessage(new TranslationTextComponent("modularitemframe.message.teleport.invalid_target"), Util.DUMMY_UUID);
                    return;
                }

                ModularFrameTile targetFrame =  (ModularFrameTile) targetBlockEntity;

                if (!isInRange(targetFrame, dim.compareTo(world.getDimensionKey().getLocation()) != 0)){
                    player.sendMessage(new TranslationTextComponent("modularitemframe.message.teleport.too_far"), Util.DUMMY_UUID);
                    return;
                }

                breakLink(world);
                linkedLoc = tmp;
                linkedDim = dim;
                direction = EnumMode.DISPENSE;

                ItemTeleportModule targetModule = (ItemTeleportModule) targetFrame.getModule();
                targetModule.breakLink(world);
                targetModule.linkedLoc = frame.getPos();
                targetModule.linkedDim = world.getDimensionKey().getLocation();
                targetModule.direction = EnumMode.VACUUM;

                player.sendMessage(new TranslationTextComponent("modularitemframe.message.teleport.link_established"), Util.DUMMY_UUID);
                nbt.remove(NBT_LINK);
                nbt.remove(NBT_DIM);
                driver.setTag(nbt);

                targetModule.markDirty();
                markDirty();
            }
        }
    }

    @Override
    public ActionResultType onUse(@NotNull World world, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull PlayerEntity player, @NotNull Hand hand, @NotNull Direction facing, BlockRayTraceResult hit) {
        if (world.isRemote) return ActionResultType.FAIL;
        if (direction != EnumMode.VACUUM) return ActionResultType.FAIL;
        if (!hasValidConnection(world)) return ActionResultType.FAIL;

        ItemStack held = player.getHeldItem(hand);

        if (!held.isEmpty()) {
        World targetWorld = getDimWorld(world);
        ItemHelper.ejectStack(targetWorld, linkedLoc, targetWorld.getBlockState(linkedLoc).get(ModularFrameBlock.FACING), held.copy());
        world.playSound(player, pos, SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.BLOCKS, 1F, 1F);
        targetWorld.playSound(null, linkedLoc, SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.BLOCKS, 1F, 1F);
        targetWorld.playSound(null, linkedLoc, SoundEvents.BLOCK_DISPENSER_DISPENSE, SoundCategory.BLOCKS, 1F, 1F);
        held.setCount(0);
    }
        return ActionResultType.SUCCESS;
    }

    @Override
    public void onRemove(@NotNull World world, @NotNull BlockPos pos, @NotNull Direction facing, @Nullable PlayerEntity player, @NotNull ItemStack moduleStack) {
        if (!world.isRemote) {
            breakLink(world);
        }
    }

    @Override
    public void tick(@NotNull World world, @NotNull BlockPos pos) {
        if (world.isRemote || frame.isPowered() || !canTick(world,60, 10)) return;
        if (direction != EnumMode.VACUUM || !hasValidConnection(world)) return;

        List<ItemEntity> entities = world.getEntitiesWithinAABB(ItemEntity.class, getScanBox(), itemEntity -> true);
        for (ItemEntity entity : entities) {
            ItemStack entityStack = entity.getItem();
            if (!frame.getItemFilter().test(entityStack)) continue;
            if (!entity.isAlive() || entityStack.isEmpty()) continue;

            World targetWorld = getDimWorld(world);
            ItemHelper.ejectStack(targetWorld, linkedLoc, targetWorld.getBlockState(linkedLoc).get(ModularFrameBlock.FACING), entityStack);
            world.playSound(null, pos, SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.BLOCKS, 1F, 1F);
            targetWorld.playSound(null, linkedLoc, SoundEvents.BLOCK_DISPENSER_DISPENSE, SoundCategory.BLOCKS, 1F, 1F);
            entity.remove();
            ((ServerWorld) world).spawnParticle(ParticleTypes.POOF, entity.getPosX() - 0.1, entity.getPosY(), entity.getPosZ() - 0.1, 4, 0.2, 0.2, 0.2, 0.07);
            break;
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
        CompoundNBT tag = super.serializeNBT();
        if (linkedLoc != null) {
            tag.putLong(NBT_LINK, linkedLoc.toLong());
        }
        if (linkedDim != null) {
            tag.putString(NBT_DIM, linkedDim.toString());
        }
        tag.putInt(NBT_DIR, direction.index);
        return tag;
    }

    @Override
    public void deserializeNBT(@NotNull CompoundNBT nbt) {
        super.deserializeNBT(nbt);
        linkedLoc = nbt.contains(NBT_LINK) ? BlockPos.fromLong(nbt.getLong(NBT_LINK)) : null;
        linkedDim = nbt.contains(NBT_DIM) ? ResourceLocation.tryCreate(nbt.getString(NBT_DIM)) : null;
        if (nbt.contains(NBT_DIR)) direction = EnumMode.values()[nbt.getInt(NBT_DIR)];
    }

    private void breakLink(World world) {
        if (linkedLoc != null) {
            TileEntity be = world.getTileEntity(linkedLoc);
            if (be instanceof ModularFrameTile && ((ModularFrameTile) be).getModule() instanceof ItemTeleportModule) {
                ItemTeleportModule targetModule = (ItemTeleportModule) ((ModularFrameTile) be).getModule();
                targetModule.linkedLoc = null;
                targetModule.linkedDim = null;
                targetModule.direction = EnumMode.NONE;
                targetModule.markDirty();
            }

            linkedLoc = null;
            linkedDim = null;
            direction = EnumMode.NONE;
            markDirty();
        }
    }

    private boolean hasValidConnection(@NotNull World world) {
        if (linkedLoc == null) return false;
        World targetWorld = getDimWorld(world);
        TileEntity blockEntity = targetWorld.getTileEntity(linkedLoc);
        return blockEntity instanceof ModularFrameTile
                && ((ModularFrameTile) blockEntity).getModule() instanceof ItemTeleportModule
                && ((ItemTeleportModule) ((ModularFrameTile) blockEntity).getModule()).direction != direction;
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
            int sourceRange = ModularItemFrame.config.teleportRange.get() + (frame.getRangeUpCount() * 10);
            int targetRange = ModularItemFrame.config.teleportRange.get() + (targetFrame.getRangeUpCount() * 10);
            return (frame.hasInfinity() || frame.getPos().withinDistance(targetFrame.getPos(), sourceRange)) &&
                    (targetFrame.hasInfinity() || targetFrame.getPos().withinDistance(frame.getPos(), targetRange));
        }

        return false;
    }


    public enum EnumMode {
        VACUUM(0, "modularitemframe.mode.in"),
        DISPENSE(1, "modularitemframe.mode.out"),
        NONE(2, "modularitemframe.mode.no");

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
