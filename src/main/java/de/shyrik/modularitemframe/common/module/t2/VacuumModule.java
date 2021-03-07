package de.shyrik.modularitemframe.common.module.t2;

import de.shyrik.modularitemframe.ModularItemFrame;
import de.shyrik.modularitemframe.common.block.ModularFrameBlock;
import modularitemframe.api.ModuleTier;
import modularitemframe.api.inventory.ItemHandlerWrapper;
import modularitemframe.api.ModuleBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class VacuumModule extends ModuleBase {

    public static final ResourceLocation ID = new ResourceLocation(ModularItemFrame.MOD_ID, "module_t2_vacuum");
    public static final ResourceLocation BG = new ResourceLocation(ModularItemFrame.MOD_ID, "module/module_t2_vacuum");
    private static final TextComponent NAME = new TranslationTextComponent("modularitemframe.module.vacuum");

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
    public void tick(@NotNull World world, @NotNull BlockPos pos) {
        if (world.isRemote || frame.isPowered() || !canTick(world,60, 10)) return;

        ItemHandlerWrapper handler = frame.getAttachedInventory();
        if (handler != null) {
            List<ItemEntity> entities = world.getEntitiesWithinAABB(ItemEntity.class, getScanBox(), Entity::isAlive);
            for (ItemEntity entity : entities) {
                ItemStack entityStack = entity.getItem();
                if (entityStack.isEmpty() ||
                        handler.insert(frame.getItemFilter(), entityStack, true).getCount() == entityStack.getCount())
                    continue;

                ItemStack remain = handler.insert(entityStack, false);
                if (remain.isEmpty()) entity.remove();
                else entity.setItem(remain);
                ((ServerWorld) world).spawnParticle(ParticleTypes.POOF, entity.getPosX() - 0.1, entity.getPosY(), entity.getPosZ() - 0.1, 4, 0.2, 0.2, 0.2, 0.07);
                break;
            }
        }
    }
}
