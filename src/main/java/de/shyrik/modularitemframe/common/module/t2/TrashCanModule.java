package de.shyrik.modularitemframe.common.module.t2;

import com.google.common.collect.ImmutableList;
import de.shyrik.modularitemframe.ModularItemFrame;
import de.shyrik.modularitemframe.api.ModuleBase;
import de.shyrik.modularitemframe.common.block.ModularFrameBlock;
import de.shyrik.modularitemframe.api.Inventory.ItemHandlerWrapper;
import de.shyrik.modularitemframe.util.ItemHelper;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.TextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TrashCanModule  extends ModuleBase {
    public static final ResourceLocation ID = new ResourceLocation(ModularItemFrame.MOD_ID, "module_t2_trashcan");
    public static final ResourceLocation BG1 = new ResourceLocation(ModularItemFrame.MOD_ID, "module/module_t2_trashcan_1");
    public static final ResourceLocation BG2 = new ResourceLocation(ModularItemFrame.MOD_ID, "module/module_t2_trashcan_2");
    public static final ResourceLocation BG3 = new ResourceLocation(ModularItemFrame.MOD_ID, "module/module_t2_trashcan_3");
    private static final TextComponent NAME = new TranslationTextComponent("modularitemframe.module.trash_can");

    private final List<ResourceLocation> frontTex = ImmutableList.of(
            BG1, BG2, BG3
    );

    private static final String NBT_LAST_STACK = "last_stack";

    private ItemStack lastStack = ItemStack.EMPTY;
    private int texIndex = 0;

    @NotNull
    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @NotNull
    @Override
    public ResourceLocation frontTexture() {
        return frontTex.get(texIndex);
    }

    @NotNull
    @Override
    public List<ResourceLocation> getVariantFronts() {
        return frontTex;
    }

    @NotNull
    @Override
    public ResourceLocation innerTexture() {
        return ModularFrameBlock.INNER_HARD;
    }

    @NotNull
    @Override
    public TextComponent getName() {
        return NAME;
    }

    @Override
    public ActionResultType onUse(@NotNull World world, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull PlayerEntity player, @NotNull Hand hand, @NotNull Direction facing, BlockRayTraceResult hit) {
        if (!world.isRemote) {
            ItemStack held = player.getHeldItem(hand);
            if (!player.isSneaking() && !held.isEmpty() && frame.getItemFilter().test(held)) {
                if (ItemHelper.simpleAreStacksEqual(held, lastStack)) {
                    if (held.getCount() + lastStack.getCount() > lastStack.getMaxStackSize())
                        lastStack.setCount(lastStack.getMaxStackSize());
                    else lastStack.grow(held.getCount());
                } else {
                    lastStack = held.copy();
                }
                held.setCount(0);
                world.playSound(null, pos, SoundEvents.BLOCK_LAVA_EXTINGUISH, SoundCategory.PLAYERS, 1F, 0.7F);
            } else if (player.isSneaking() && held.isEmpty() && !lastStack.isEmpty()) {
                player.setHeldItem(hand, lastStack);
                lastStack = ItemStack.EMPTY;
                world.playSound(null, pos, SoundEvents.BLOCK_LAVA_EXTINGUISH, SoundCategory.PLAYERS, 1F, 0.7F);
            }

            markDirty();
        }
        return ActionResultType.SUCCESS;
    }

    @Override
    public void tick(@NotNull World world, @NotNull BlockPos pos) {
        if (!world.isRemote) {
            if (frame.isPowered()) return;
            if (!canTick(world,60, 10)) return;

            ItemHandlerWrapper trash = frame.getAttachedInventory();
            if (trash != null) {
                if (!trash.extract(frame.getItemFilter(), false).isEmpty()) {
                    world.playSound(null, pos, SoundEvents.BLOCK_LAVA_EXTINGUISH, SoundCategory.PLAYERS, 1F, 0.7F);
                }
            }
        } else {
            if (world.getGameTime() % 20 == 0) {
                texIndex = texIndex < frontTex.size() - 1 ? texIndex + 1 : 0;
            }
        }
    }

    @NotNull
    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT tag = super.serializeNBT();
        tag.put(NBT_LAST_STACK, lastStack.serializeNBT());
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundNBT tag) {
        super.deserializeNBT(tag);
        if (tag.contains(NBT_LAST_STACK)) lastStack = ItemStack.read(tag.getCompound(NBT_LAST_STACK));
    }
}
