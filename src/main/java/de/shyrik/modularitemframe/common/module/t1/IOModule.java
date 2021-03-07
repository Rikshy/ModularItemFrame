package de.shyrik.modularitemframe.common.module.t1;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.shyrik.modularitemframe.ModularItemFrame;
import de.shyrik.modularitemframe.util.InventoryHelper;
import de.shyrik.modularitemframe.util.ItemHelper;
import modularitemframe.api.ModuleTier;
import modularitemframe.api.accessors.IFrameRenderer;
import modularitemframe.api.inventory.ItemHandlerWrapper;
import modularitemframe.api.ModuleBase;
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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

public class IOModule extends ModuleBase {

    public static final ResourceLocation ID = new ResourceLocation(ModularItemFrame.MOD_ID, "module_t1_io");
    public static final ResourceLocation BG = new ResourceLocation(ModularItemFrame.MOD_ID, "module/module_t1_io");
    private static final TextComponent NAME = new TranslationTextComponent("modularitemframe.module.io");

    private static final String NBT_LAST = "last_click";
    private static final String NBT_LAST_STACK = "last_stack";
    private static final String NBT_DISPLAY = "display";

    private long lastClick;
    private ItemStack displayItem = ItemStack.EMPTY;
    private ItemStack lastStack = ItemStack.EMPTY;

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
        return ModuleTier.T1;
    }

    @NotNull
    @Override
    @OnlyIn(Dist.CLIENT)
    public ResourceLocation frontTexture() {
        return BG;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void specialRendering(@NotNull IFrameRenderer renderer, float partialTicks, @NotNull MatrixStack matrixStack, @NotNull IRenderTypeBuffer buffer, int light, int overlay) {
        renderer.renderItem(displayItem, matrixStack, buffer, light, overlay);
    }

    @Override
    public void onBlockClicked(@NotNull World world, @NotNull BlockPos pos, @NotNull PlayerEntity player) {
        if (!world.isRemote) {
            ItemHandlerWrapper handler = frame.getAttachedInventory();
            if (handler != null) {
                ItemStack test = handler.extract(true);
                if (!test.isEmpty()) {
                    int amount = player.isSneaking() ? test.getMaxStackSize() : 1;

                    ItemStack extract = handler.extract(amount, false);
                    ItemStack remain = InventoryHelper.givePlayer(player, extract);

                    if (!extract.isEmpty())
                        ItemHelper.ejectStack(world, pos, frame.getFacing(), remain);

                    lastStack = handler.extract(1, true);
                    markDirty();
                }
            }
        }
    }

    @Override
    public ActionResultType onUse(@NotNull World world, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull PlayerEntity player, @NotNull Hand hand, @NotNull Direction facing, BlockRayTraceResult hit) {
        if (!world.isRemote) {
            ItemHandlerWrapper handler = frame.getAttachedInventory();
            if (handler != null) {
                ItemStack held = player.getHeldItem(hand);
                long time = world.getGameTime();

                if (time - lastClick <= 8L && !player.isSneaking() && !lastStack.isEmpty() && ItemStack.areItemsEqual(lastStack, held)) {
                    IItemHandler playerInv = InventoryHelper.getPlayerInv(player);
                    InventoryHelper.giveAllPossibleStacks(handler.getHandler(), playerInv, lastStack, held);
                } else if (!held.isEmpty()) {
                    ItemStack heldCopy = held.copy();
                    heldCopy.setCount(1);

                    if(handler.insert(heldCopy, false).isEmpty()) {
                        held.shrink(1);
                        //handler.markDirty();
                        lastStack = heldCopy;
                        lastClick = time;
                    }
                }

                markDirty();
            }
        }
        return ActionResultType.SUCCESS;
    }

    @Override
    public void tick(@NotNull World world, @NotNull BlockPos pos) {
        if(!world.isRemote) {
            ItemHandlerWrapper handler = frame.getAttachedInventory();
            if (handler != null) {
                ItemStack slotStack = handler.extract(1,true);
                if (!ItemHelper.areItemsEqual(slotStack, displayItem)) {
                    displayItem = slotStack;
                    markDirty();
                }
            }
        }
    }

    @NotNull
    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT cmp = super.serializeNBT();
        cmp.putLong(NBT_LAST, lastClick);
        cmp.put(NBT_LAST_STACK, lastStack.serializeNBT());
        cmp.put(NBT_DISPLAY, displayItem.serializeNBT());
        return cmp;
    }

    @Override
    public void deserializeNBT(@NotNull CompoundNBT cmp) {
        super.deserializeNBT(cmp);
        if (cmp.contains(NBT_LAST)) lastClick = cmp.getLong(NBT_LAST);
        if (cmp.contains(NBT_LAST_STACK)) lastStack = ItemStack.read(cmp.getCompound(NBT_LAST_STACK));
        if (cmp.contains(NBT_DISPLAY)) displayItem = ItemStack.read(cmp.getCompound(NBT_DISPLAY));
    }
}
