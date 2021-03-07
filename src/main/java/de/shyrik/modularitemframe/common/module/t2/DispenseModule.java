package de.shyrik.modularitemframe.common.module.t2;

import de.shyrik.modularitemframe.ModularItemFrame;
import de.shyrik.modularitemframe.common.block.ModularFrameBlock;
import de.shyrik.modularitemframe.util.ItemHelper;
import modularitemframe.api.ModuleTier;
import modularitemframe.api.inventory.ItemHandlerWrapper;
import modularitemframe.api.ModuleBase;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.TextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

public class DispenseModule extends ModuleBase {

    public static final ResourceLocation ID = new ResourceLocation(ModularItemFrame.MOD_ID, "module_t2_dispense");
    public static final ResourceLocation BG = new ResourceLocation(ModularItemFrame.MOD_ID, "module/module_t2_dispense");
    private static final TextComponent NAME = new TranslationTextComponent("modularitemframe.module.dispense");

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
    public ActionResultType onUse(@NotNull World world, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull PlayerEntity player, @NotNull Hand hand, @NotNull Direction facing, BlockRayTraceResult hit) {
        if (!world.isRemote) {
            ItemStack held = player.getHeldItem(hand);
            if (!held.isEmpty()) {
                ItemHelper.ejectStack(world, pos, facing, held.copy());
                world.playSound(null, pos, SoundEvents.BLOCK_DISPENSER_DISPENSE, SoundCategory.BLOCKS, 1F, 1F);
                held.setCount(0);
            }
        }
        return ActionResultType.SUCCESS;
    }

    @Override
    public void tick(@NotNull World world, @NotNull BlockPos pos) {
        if (world.isRemote || frame.isPowered() || !canTick(world,60, 10)) return;

        ItemHandlerWrapper inventory = frame.getAttachedInventory();
        if (inventory != null) {
            ItemStack extracted = inventory.extract(frame.getItemFilter(), 1, false);
            if (!extracted.isEmpty()) {
                ItemHelper.ejectStack(world, pos, frame.getFacing(), extracted);
                world.playSound(null, pos, SoundEvents.BLOCK_DISPENSER_DISPENSE, SoundCategory.BLOCKS, 1F, 1F);
            }
        }
    }
}
