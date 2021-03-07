package de.shyrik.modularitemframe.common.module.t2;

import de.shyrik.modularitemframe.ModularItemFrame;
import de.shyrik.modularitemframe.api.ModuleBase;
import de.shyrik.modularitemframe.common.block.ModularFrameBlock;
import de.shyrik.modularitemframe.api.Inventory.filter.ItemClassFilter;
import de.shyrik.modularitemframe.api.Inventory.ItemHandlerWrapper;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.TextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

public class BlockPlaceModule  extends ModuleBase {
    public static final ResourceLocation ID = new ResourceLocation(ModularItemFrame.MOD_ID, "module_t2_place");
    public static final ResourceLocation BG = new ResourceLocation(ModularItemFrame.MOD_ID, "module/module_t2_place");
    private static final TextComponent NAME = new TranslationTextComponent("modularitemframe.module.block_placer");

    public static class FrameItemPlacementContext extends BlockItemUseContext {
        public FrameItemPlacementContext(World world, ItemStack itemStack, BlockPos placePos, Direction direction) {
            super(world, null, Hand.MAIN_HAND, itemStack, new BlockRayTraceResult(
                    new Vector3d(
                            placePos.getX(),
                            placePos.getY(),
                            placePos.getZ()),
                    direction,
                    placePos,
                    true));
        }
    }

    @NotNull
    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @NotNull
    @Override
    public ResourceLocation frontTexture() {
        return BG;
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
    public void tick(@NotNull World world, @NotNull BlockPos pos) {
        if (world.isRemote || frame.isPowered() || !canTick(world,60, 10)) return;
        ItemHandlerWrapper inventory = frame.getAttachedInventory();
        if (inventory == null) return;

        ItemStack itemToPlace = inventory.extract(
                new ItemClassFilter(BlockItem.class), 1, true);

        if (!itemToPlace.isEmpty()) {
            Direction facing = frame.getFacing();

            for (int offset = 0; offset <= Math.pow(frame.getRangeUpCount() + 1, 2); offset++) {
                BlockPos placePos = pos.offset(facing, offset);

                ActionResultType placeResult = ((BlockItem) itemToPlace.getItem()).tryPlace(
                        new FrameItemPlacementContext(world, itemToPlace, placePos, facing)
                );

                if (placeResult.isSuccessOrConsume()) {
                    inventory.extract(new ItemClassFilter(BlockItem.class),1, false);
                    break;
                }
            }
        }
    }
}