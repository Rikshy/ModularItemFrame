package de.shyrik.modularitemframe.common.module.t2;

import com.google.common.collect.ImmutableList;
import de.shyrik.modularitemframe.ModularItemFrame;
import de.shyrik.modularitemframe.api.ModuleBase;
import de.shyrik.modularitemframe.common.block.ModularFrameBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import java.util.List;

public class FanModule extends ModuleBase {
    public static final ResourceLocation ID = new ResourceLocation(ModularItemFrame.MOD_ID, "module_t2_fan");
    public static final ResourceLocation BG1 = new ResourceLocation(ModularItemFrame.MOD_ID, "module/module_t2_fan1");
    public static final ResourceLocation BG2 = new ResourceLocation(ModularItemFrame.MOD_ID, "module/module_t2_fan2");
    public static final ResourceLocation BG3 = new ResourceLocation(ModularItemFrame.MOD_ID, "module/module_t2_fan3");
    private static final TextComponent NAME = new TranslationTextComponent("modularitemframe.module.fan");

    private static final List<ResourceLocation> frontTex = ImmutableList.of(
            BG1, BG2, BG3
    );
    public static final double strengthScaling = 0.09;

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

    @Override
    public TextComponent getName() {
        return NAME;
    }

    @Override
    public void tick(@NotNull World world, @NotNull BlockPos pos) {
        if (frame.isPowered()) return;
        if (world.isRemote) {
            if (world.getGameTime() % 10 == 0) {
                texIndex = texIndex < frontTex.size() - 1 ? texIndex + 1 : 0;
            }
        }
        List<Entity> entities = world.getEntitiesWithinAABB(Entity.class, getFanBox(), entity ->
                (entity instanceof LivingEntity || entity instanceof ItemEntity)
                        && !entity.isSneaking() && entity.isAlive() && !entity.isSpectator());
        if (entities.isEmpty()) return;
        Direction facing = frame.getFacing();
        double xVel = facing.getXOffset() * strengthScaling;
        double yVel = facing.getYOffset() * strengthScaling;
        double zVel = facing.getZOffset() * strengthScaling;
        entities.forEach(livingEntity -> livingEntity.addVelocity(xVel, yVel, zVel));
    }

    private AxisAlignedBB getFanBox() {
        BlockPos pos = frame.getPos();
        int range = frame.getRangeUpCount() + ModularItemFrame.config.scanZoneRadius.get();
        switch (frame.getFacing()) {
            case DOWN:
                return new AxisAlignedBB(pos.add(0, 1, 0), pos.add(1, -range + 1, 1));
            case UP:
                return new AxisAlignedBB(pos.add(0, -1, 0), pos.add(1, range, 1));
            case NORTH:
                return new AxisAlignedBB(pos.add(0, 0, 1), pos.add(1, 1, -range + 1));
            case SOUTH:
                return new AxisAlignedBB(pos.add(0, 0, -1), pos.add(1, 1, range));
            case WEST:
                return new AxisAlignedBB(pos.add(1, 0, 0), pos.add(-range + 1, 1, 1));
            case EAST:
                return new AxisAlignedBB(pos.add(-1, 0, 0), pos.add(range, 1, 1));
        }
        return new AxisAlignedBB(pos, pos.add(1, 1, 1));
    }
}
