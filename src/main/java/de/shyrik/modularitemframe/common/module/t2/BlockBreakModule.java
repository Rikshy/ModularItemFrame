package de.shyrik.modularitemframe.common.module.t2;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import de.shyrik.modularitemframe.ModularItemFrame;
import de.shyrik.modularitemframe.common.block.ModularFrameBlock;
import de.shyrik.modularitemframe.util.ItemHelper;
import modularitemframe.api.ModuleTier;
import modularitemframe.api.accessors.IFrameRenderer;
import modularitemframe.api.inventory.ItemHandlerWrapper;
import modularitemframe.api.ModuleBase;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BlockBreakModule  extends ModuleBase {
    public static final ResourceLocation ID = new ResourceLocation(ModularItemFrame.MOD_ID, "module_t2_break");
    public static final ResourceLocation BG = new ResourceLocation(ModularItemFrame.MOD_ID, "module/module_t2_break");
    private static final TextComponent NAME = new TranslationTextComponent("modularitemframe.module.block_breaker");

    private static final String NBT_PROGRESS = "progress";

    private static final ItemStack displayItem = new ItemStack(Items.IRON_PICKAXE);
    private static final List<Integer> rotation = ImmutableList.of(
            10,
            0,
            -10,
            -20,
            -30,
            -40,
            -50,
            -60,
            -70,
            -80,
            -90
    );

    private int breakProgress = 0;
    private BlockState lastTarget = null;
    private BlockPos lastPos = null;
    private Integer breakId = null;

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
        return ModuleTier.T2;
    }

    @NotNull
    @Override
    public ResourceLocation frontTexture() {
        return BG;
    }

    @Override
    public void specialRendering(@NotNull IFrameRenderer renderer, float ticks, @NotNull MatrixStack matrixStack, @NotNull IRenderTypeBuffer buffer, int light, int overlay) {
        renderer.renderItem(displayItem, -rotation.get(breakProgress) + 10, matrixStack, buffer, light, overlay);
    }

    @Override
    public void tick(@NotNull World world, @NotNull BlockPos pos) {
        if (world.isRemote || frame.isPowered()) return;

        BlockPos targetPos;
        BlockState targetState;
        int offset = 1;

        do {
            targetPos = pos.offset(frame.getFacing(), offset);
            targetState = world.getBlockState(targetPos);
        } while (targetState.isAir() && offset++ <= Math.pow(frame.getRangeUpCount() + 1, 2));

        float hardness = targetState.getBlockHardness(world, targetPos);

        if (targetState.isAir() || hardness < 0) {
            resetState(world, null, null,  null);
            markDirty();
            return;
        }

        if (targetState != lastTarget || !targetPos.equals(lastPos)) {
            resetState(world, targetState, targetPos,  world.rand.nextInt());
        }

        if (world.getGameTime() % Math.ceil((2 * hardness) / (frame.getSpeedUpCount() + 1)) != 0) return;

        if (++breakProgress >= 10) {
            ItemHandlerWrapper inv = frame.getAttachedInventory();
            boolean drop = true;
            if (inv != null) {
                drop = false;

                TileEntity targetEntity = targetState.hasTileEntity() ? world.getTileEntity(pos) : null;
                List<ItemStack> drops = Block.getDrops(targetState,(ServerWorld) world, targetPos, targetEntity);
                for (ItemStack dropStack : drops) {
                    ItemStack remain = inv.insert(dropStack, false);
                    if (!remain.isEmpty()) {
                        ItemHelper.ejectStack(world, pos, frame.getFacing(), remain);
                    }
                }
            }

            world.destroyBlock(targetPos, drop);
        } else {
            markDirty();
            world.sendBlockBreakProgress(breakId, targetPos, breakProgress);
        }
    }

    @NotNull
    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT tag = super.serializeNBT();
        tag.putInt(NBT_PROGRESS, breakProgress);
        return tag;
    }

    @Override
    public void deserializeNBT(@NotNull CompoundNBT tag) {
        super.deserializeNBT(tag);
        if (tag.contains(NBT_PROGRESS)) breakProgress = tag.getInt(NBT_PROGRESS);
    }

    private void resetState(World world, BlockState state, BlockPos pos, Integer newBreakId) {
        if (breakId != null) world.sendBlockBreakProgress(breakId, lastPos, -1);
        breakProgress = 0;
        lastTarget = state;
        lastPos = pos;
        breakId = newBreakId;
    }
}
