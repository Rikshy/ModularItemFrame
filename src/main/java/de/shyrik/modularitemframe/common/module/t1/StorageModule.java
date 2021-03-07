package de.shyrik.modularitemframe.common.module.t1;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.shyrik.modularitemframe.ModularItemFrame;
import de.shyrik.modularitemframe.api.ModuleBase;
import de.shyrik.modularitemframe.client.FrameRenderer;
import de.shyrik.modularitemframe.api.Inventory.ItemStackHandlerWrapper;
import de.shyrik.modularitemframe.api.Inventory.OpenItemStackHandler;
import de.shyrik.modularitemframe.util.InventoryHelper;
import de.shyrik.modularitemframe.util.ItemHelper;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.TextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class StorageModule extends ModuleBase {
    public static final ResourceLocation ID = new ResourceLocation(ModularItemFrame.MOD_ID, "module_t1_storage");
    public static final ResourceLocation BG = new ResourceLocation(ModularItemFrame.MOD_ID, "module/module_t1_storage");
    private static final TextComponent NAME = new TranslationTextComponent("modularitemframe.module.storage");

    private static final String NBT_LAST = "last_click";
    private static final String NBT_LAST_STACK = "last_stack";
    private static final String NBT_INVENTORY= "inventory";

    private final ItemStackHandlerWrapper inventory = new ItemStackHandlerWrapper(new OpenItemStackHandler());

    private long lastClick;
    private ItemStack lastStack = ItemStack.EMPTY;

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
    public TextComponent getName() {
        return NAME;
    }

    @Override
    public void specialRendering(@NotNull FrameRenderer renderer, float partialTicks, @NotNull MatrixStack matrixStack, @NotNull IRenderTypeBuffer buffer, int light, int overlay) {
        renderer.renderInside(lastStack, matrixStack, buffer, light, overlay);
    }

    @Override
    public void onBlockClicked(@NotNull World world, @NotNull BlockPos pos, @NotNull PlayerEntity player) {
        if (!world.isRemote) {
            ItemStack test = inventory.extract(true);
            if (!test.isEmpty()) {
                int amount = player.isSneaking() ? test.getMaxStackSize() : 1;

                ItemStack extract = inventory.extract(amount, false);
                ItemStack remain = InventoryHelper.givePlayer(player, extract);

                if (!extract.isEmpty())
                    ItemHelper.ejectStack(world, pos, frame.getFacing(), remain);

                lastStack = inventory.extract(1, true);
                markDirty();
            }
        }
    }

    @Override
    public ActionResultType onUse(@NotNull World world, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull PlayerEntity player, @NotNull Hand hand, @NotNull Direction facing, BlockRayTraceResult hit) {
        if (!world.isRemote) {
            ItemStack held = player.getHeldItem(hand);
            long time = world.getGameTime();

            if (time - lastClick <= 8L && !player.isSneaking() && !lastStack.isEmpty() && ItemStack.areItemsEqual(lastStack, held)) {
                IItemHandler playerInv = InventoryHelper.getPlayerInv(player);
                InventoryHelper.giveAllPossibleStacks(inventory.getHandler(), playerInv, lastStack, held);
            } else if (!held.isEmpty()) {
                ItemStack heldCopy = held.copy();
                heldCopy.setCount(1);

                if(inventory.insert(held, false).isEmpty()) {
                    held.shrink(1);

                    lastStack = heldCopy;
                    lastClick = time;
                }
            }

            markDirty();
        }
        return ActionResultType.SUCCESS;
    }

    @Override
    public void onFrameUpgradesChanged(World world, BlockPos pos, Direction facing) {
        int newCapacity = (int)Math.pow(2, frame.getCapacityUpCount() + 1);

        if (newCapacity != inventory.getSlots()) {
            IItemHandler cpy = inventory.copy().getHandler();
            inventory.setSize(newCapacity);
            for (int slot = 0; slot < cpy.getSlots(); slot++) {
                if (slot < inventory.getSlots())
                    inventory.insert(cpy.getStackInSlot(slot), false);
                else
                    ItemHelper.ejectStack(world, pos, facing, cpy.getStackInSlot(slot));
            }

            markDirty();
        }
    }

    @Override
    public void onRemove(@NotNull World worldIn, @NotNull BlockPos pos, @NotNull Direction facing, @Nullable PlayerEntity playerIn, @NotNull ItemStack modStack) {
        super.onRemove(worldIn, pos, facing, playerIn, modStack);
        for( int slot = 0; slot < inventory.getSlots(); slot++) {
            ItemHelper.ejectStack(worldIn, pos, frame.getFacing(), inventory.getStackInSlot(slot));
        }
    }

    @NotNull
    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = super.serializeNBT();
        nbt.put(NBT_INVENTORY, inventory.serializeNBT());
        nbt.putLong(NBT_LAST, lastClick);
        nbt.put(NBT_LAST_STACK, lastStack.serializeNBT());
        return nbt;
    }

    @Override
    public void deserializeNBT(@NotNull CompoundNBT nbt) {
        super.deserializeNBT(nbt);
        if (nbt.contains(NBT_INVENTORY)) inventory.deserializeNBT(nbt.getCompound(NBT_INVENTORY));
        if (nbt.contains(NBT_LAST)) lastClick = nbt.getLong(NBT_LAST);
        if (nbt.contains(NBT_LAST_STACK)) lastStack = ItemStack.read(nbt.getCompound(NBT_LAST_STACK));
    }
}
