package de.shyrik.modularitemframe.common.upgrade;

import de.shyrik.modularitemframe.ModularItemFrame;
import modularitemframe.api.UpgradeBase;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class SecurityUpgrade extends UpgradeBase {
    public static final ResourceLocation ID = new ResourceLocation(ModularItemFrame.MOD_ID, "upgrade_security");
    private static final TextComponent NAME = new TranslationTextComponent("modularitemframe.upgrade.security");

    private static final String NBT_PLAYER = "player_id";

    private UUID playerId = null;

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

    @Override
    public int getMaxCount() {
        return 1;
    }

    @Override
    public void onInsert(@NotNull World world, @NotNull BlockPos pos, @NotNull Direction facing, PlayerEntity player, ItemStack upStack) {
        playerId = player.getUniqueID();
    }

    @NotNull
    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT tag = super.serializeNBT();
        tag.putUniqueId(NBT_PLAYER, playerId);
        return tag;
    }

    @Override
    public void deserializeNBT(@NotNull CompoundNBT tag) {
        super.deserializeNBT(tag);
        playerId = tag.contains(NBT_PLAYER) ? tag.getUniqueId(NBT_PLAYER) : null;
    }

    public boolean hasAccess(PlayerEntity player) {
        return player.getUniqueID().compareTo(playerId) == 0;
    }
}
