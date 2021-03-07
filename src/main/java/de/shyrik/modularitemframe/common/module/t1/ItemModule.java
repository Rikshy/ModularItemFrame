package de.shyrik.modularitemframe.common.module.t1;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.shyrik.modularitemframe.ModularItemFrame;
import modularitemframe.api.ModuleTier;
import modularitemframe.api.accessors.IFrameRenderer;
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
import org.jetbrains.annotations.NotNull;

public class ItemModule extends ModuleBase {

    public static final ResourceLocation ID = new ResourceLocation(ModularItemFrame.MOD_ID, "module_t1_item");
    public static final ResourceLocation BG = new ResourceLocation(ModularItemFrame.MOD_ID, "module/module_t1_item");
    private static final TextComponent NAME = new TranslationTextComponent("modularitemframe.module.item");

    private static final String NBT_DISPLAY = "display";
    private static final String NBT_ROTATION = "rotation";

    private int rotation = 0;
    private ItemStack displayItem = ItemStack.EMPTY;

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
    @OnlyIn(Dist.CLIENT)
    public ResourceLocation frontTexture() {
        return BG;
    }

    private void rotate(PlayerEntity player) {
        if (player.isSneaking()) {
            rotation += 20;
        } else {
            rotation -= 20;
        }
        if (rotation >= 360 || rotation <= -360) rotation = 0;
        markDirty();
    }

    @Override
    public void specialRendering(@NotNull IFrameRenderer renderer, float ticks, @NotNull MatrixStack matrixStack, @NotNull IRenderTypeBuffer buffer, int light, int overlay) {
        renderer.renderItem(displayItem, rotation, matrixStack, buffer, light, overlay);
    }

    public void screw(@NotNull World world, @NotNull BlockPos pos, @NotNull PlayerEntity player, ItemStack driver) {
        if (!world.isRemote) {
            rotate(player);
            markDirty();
        }
    }

    @Override
    public ActionResultType onUse(@NotNull World worldIn, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull PlayerEntity player, @NotNull Hand hand, @NotNull Direction facing, BlockRayTraceResult hit) {
        if (!worldIn.isRemote) {
            if (player.isSneaking()) {
                ItemStack copy = player.getHeldItem(hand).copy();
                copy.setCount(1);
                displayItem = copy;
                markDirty();
            }
        }
        return ActionResultType.SUCCESS;
    }

    @NotNull
    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT compound = super.serializeNBT();
        compound.put(NBT_DISPLAY, displayItem.serializeNBT());
        compound.putInt(NBT_ROTATION, rotation);
        return compound;
    }

    @Override
    public void deserializeNBT(@NotNull CompoundNBT nbt) {
        super.deserializeNBT(nbt);
        if (nbt.contains(NBT_DISPLAY)) displayItem = ItemStack.read(nbt.getCompound(NBT_DISPLAY));
        if (nbt.contains(NBT_ROTATION)) rotation = nbt.getInt(NBT_ROTATION);
    }
}
