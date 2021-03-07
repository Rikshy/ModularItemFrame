package de.shyrik.modularitemframe.common.module.t3;

import de.shyrik.modularitemframe.ModularItemFrame;
import de.shyrik.modularitemframe.common.block.ModularFrameBlock;
import de.shyrik.modularitemframe.util.ExperienceHelper;
import modularitemframe.api.ModuleBase;
import modularitemframe.api.ModuleTier;
import net.minecraft.block.BlockState;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.TextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class XPModule extends ModuleBase {
    public static final ResourceLocation ID = new ResourceLocation(ModularItemFrame.MOD_ID,"module_t3_xp");
    public static final ResourceLocation BG = new ResourceLocation(ModularItemFrame.MOD_ID,"module/module_t3_xp");
    private static final TextComponent NAME = new TranslationTextComponent("modularitemframe.module.xp");

    private static final int MAX_XP = 21862;

    private static final String NBT_XP = "xp";
    private static final String NBT_LEVEL = "level";

    private int experience;
    private int levels;

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
        return ModuleTier.T3;
    }

    @NotNull
    @Override
    public ResourceLocation frontTexture() {
        return BG;
    }

    @Override
    public void onBlockClicked(@NotNull World worldIn, @NotNull BlockPos pos, @NotNull PlayerEntity playerIn) {
        if (!worldIn.isRemote) {
            if (playerIn.isSneaking()) drainContainerXpToReachPlayerLevel(playerIn, 0);
            else drainContainerXpToReachPlayerLevel(playerIn, playerIn.experienceLevel + 1);
            frame.markDirty();
        }
    }

    @Override
    public ActionResultType onUse(@NotNull World world, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull PlayerEntity player, @NotNull Hand hand, @NotNull Direction facing, BlockRayTraceResult hit) {
        if (!world.isRemote) {
            if (player.isSneaking()) drainPlayerXpToReachPlayerLevel(player, 0);
            else drainPlayerXpToReachPlayerLevel(player, player.experienceLevel - 1);
            frame.markDirty();
        }
        return ActionResultType.SUCCESS;
    }

    @Override
    public void onRemove(@NotNull World world, @NotNull BlockPos pos, @NotNull Direction facing, @Nullable PlayerEntity playerIn, @NotNull ItemStack modStack) {
        super.onRemove(world, pos, facing, playerIn, modStack);
        if (playerIn == null || playerIn instanceof FakePlayer)
            world.addEntity(new ExperienceOrbEntity(world, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, experience));
        else playerIn.giveExperiencePoints(experience);
    }

    @Override
    public void tick(@NotNull World world, @NotNull BlockPos pos) {
        if (experience >= MAX_XP) return;
        if (world.isRemote || frame.isPowered() || !canTick(world,60, 10)) return;

        boolean gotXp = false;
        List<ExperienceOrbEntity> entities = world.getEntitiesWithinAABB(ExperienceOrbEntity.class, getScanBox());
        for (ExperienceOrbEntity entity : entities) {
            if (!entity.isAlive()) continue;


            int prevLvl = levels;
            int added = addExperience(entity.getXpValue());
            if (prevLvl != levels) {
                world.playSound(null, pos, SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.BLOCKS, 1F, 1F);
            }
            if (added > 0) {
                entity.remove();
                gotXp = true;
            }
        }
        if (gotXp) {
            markDirty();
            world.playSound(null, pos, SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.BLOCKS, 1F, 1F);
        }
    }

    @NotNull
    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = super.serializeNBT();
        nbt.putInt(NBT_XP, experience);
        nbt.putInt(NBT_LEVEL, levels);
        return nbt;
    }

    @Override
    public void deserializeNBT(@NotNull CompoundNBT nbt) {
        super.deserializeNBT(nbt);
        if (nbt.contains(NBT_XP)) experience = nbt.getInt(NBT_XP);
        if (nbt.contains(NBT_LEVEL)) levels = nbt.getInt(NBT_LEVEL);
    }

    private void drainPlayerXpToReachPlayerLevel(PlayerEntity player, int level) {
        int targetXP = ExperienceHelper.getExperienceForLevel(level);
        int drainXP = ExperienceHelper.getPlayerXP(player) - targetXP;
        if (drainXP <= 0) {
            return;
        }
        drainXP = addExperience(drainXP);
        if (drainXP > 0) {
            player.giveExperiencePoints(-drainXP);
        }
    }

    private void drainContainerXpToReachPlayerLevel(PlayerEntity player, int level) {
        int requiredXP = level == 0 ? experience : ExperienceHelper.getExperienceForLevel(level) - ExperienceHelper.getPlayerXP(player);

        requiredXP = Math.min(experience, requiredXP);

        addExperience(-requiredXP);
        player.giveExperiencePoints(requiredXP);
    }

    private int addExperience(int xpToAdd) {
        int j = ExperienceHelper.getMaxXp() - experience;
        if (xpToAdd > j) {
            xpToAdd = j;
        }

        experience += xpToAdd;
        levels = ExperienceHelper.getLevelForExperience(experience);
        return xpToAdd;
    }
}
