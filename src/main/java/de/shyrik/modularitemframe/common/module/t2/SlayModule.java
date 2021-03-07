package de.shyrik.modularitemframe.common.module.t2;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.shyrik.modularitemframe.ModularItemFrame;
import de.shyrik.modularitemframe.api.ModuleBase;
import de.shyrik.modularitemframe.client.FrameRenderer;
import de.shyrik.modularitemframe.common.block.ModularFrameBlock;
import de.shyrik.modularitemframe.api.Inventory.filter.ItemClassFilter;
import de.shyrik.modularitemframe.api.Inventory.ItemHandlerWrapper;
import de.shyrik.modularitemframe.util.ItemHelper;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.TextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class SlayModule extends ModuleBase {
    public static final ResourceLocation ID = new ResourceLocation(ModularItemFrame.MOD_ID, "module_t2_slay");
    public static final ResourceLocation BG = new ResourceLocation(ModularItemFrame.MOD_ID, "module/module_t2_slay");
    private static final TextComponent NAME = new TranslationTextComponent("modularitemframe.module.slay");

    private static final String NBT_WEAPON = "weapon";
    private static final String NBT_ROTATION = "rotation";

    private int rotation = 0;
    private ItemStack weapon = ItemStack.EMPTY;

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
    public void specialRendering(@NotNull FrameRenderer renderer, float ticks, @NotNull MatrixStack matrixStack, @NotNull IRenderTypeBuffer buffer, int light, int overlay) {
        renderer.renderInside(weapon, rotation, 0F, 0.40F, ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, matrixStack, buffer, light, overlay);
    }

    @Override
    public void onRemove(@NotNull World world, @NotNull BlockPos pos, @NotNull Direction facing, PlayerEntity player, @NotNull ItemStack moduleStack) {
        ItemHelper.ejectStack(world, pos, facing, weapon);
    }

    @Override
    public ActionResultType onUse(@NotNull World world, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull PlayerEntity player, @NotNull Hand hand, @NotNull Direction facing, BlockRayTraceResult hit) {
        if (!world.isRemote) {
            ItemStack held = player.getHeldItem(hand);
            if (held.isEmpty()) {
                player.setHeldItem(hand, weapon.copy());
                weapon.setCount(0);
            } else {
                if (weapon.isEmpty() && held.getItem() instanceof SwordItem) {
                    weapon = held.copy();
                    player.setHeldItem(hand, ItemStack.EMPTY);
                }
            }

            markDirty();
        }
        return ActionResultType.SUCCESS;
    }

    @Override
    public void tick(@NotNull World world, @NotNull BlockPos pos) {
        if (world.isRemote || frame.isPowered()) return;

        if (weapon.isEmpty()) {
            weapon = getNextStack();
            rotation = 0;
        } else {
            if (rotation >= 360) {
                rotation -= 360;
                hitIt(world);
            }
            rotation += 15 * (frame.getSpeedUpCount() + 1);
        }

        markDirty();
    }

    @NotNull
    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT tag = super.serializeNBT();
        tag.put(NBT_WEAPON, weapon.serializeNBT());
        tag.putInt(NBT_ROTATION, rotation);
        return tag;
    }

    @Override
    public void deserializeNBT(@NotNull CompoundNBT tag) {
        super.deserializeNBT(tag);
        if (tag.contains(NBT_WEAPON)) weapon = ItemStack.read(tag.getCompound(NBT_WEAPON));
        if (tag.contains(NBT_ROTATION)) rotation = tag.getInt(NBT_ROTATION);
    }

    private void hitIt(World world) {
        Collection<AttributeModifier> mods = weapon.getAttributeModifiers(EquipmentSlotType.MAINHAND).get(Attributes.ATTACK_DAMAGE);
        if (!mods.isEmpty()) {
            AttributeModifier mod = mods.stream().findFirst().get();
            world.getEntitiesWithinAABB(MobEntity.class, getScanBox(), mobEntity -> true).forEach(mobEntity -> mobEntity.attackEntityFrom(DamageSource.causeMobDamage(null), (float) mod.getAmount()));
            weapon.attemptDamageItem(1, world.rand, null);
        }
    }

    private ItemStack getNextStack() {
        ItemHandlerWrapper handler = frame.getAttachedInventory();
        if (handler != null) {
            return handler.extract(new ItemClassFilter(SwordItem.class), 1, false);
        }

        return ItemStack.EMPTY;
    }
}
