package de.shyrik.modularitemframe.common.module.t1;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.shyrik.modularitemframe.ModularItemFrame;
import de.shyrik.modularitemframe.api.ModuleBase;
import de.shyrik.modularitemframe.api.util.FrameFluidRenderer;
import de.shyrik.modularitemframe.api.util.ItemHelper;
import de.shyrik.modularitemframe.api.util.RandomUtils;
import de.shyrik.modularitemframe.client.render.FrameRenderer;
import de.shyrik.modularitemframe.common.network.NetworkHandler;
import de.shyrik.modularitemframe.common.network.packet.PlaySoundPacket;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;

public class ModuleNullify extends ModuleBase {

    public static final ResourceLocation LOC = new ResourceLocation(ModularItemFrame.MOD_ID, "module_t1_null");
    public static final ResourceLocation BG_LOC = new ResourceLocation(ModularItemFrame.MOD_ID, "block/module_t1_null");
    private static final String NBT_LASTSTACK = "laststack";

    private ItemStack lastStack = ItemStack.EMPTY;

    private final FluidStack lavaStack;
    private final TextureAtlasSprite still;
    private final TextureAtlasSprite flowing;

    @Override
    @OnlyIn(Dist.CLIENT)
    public void specialRendering(FrameRenderer renderer, @Nonnull MatrixStack matrixStack, float partialTicks, @Nonnull IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {
        switch (tile.blockFacing()) {
            case UP:
                FrameFluidRenderer.renderFluidCuboid(lavaStack, matrixStack, buffer, combinedLight, 0.3f, 0.07f, 0.3f, 0.7f, 0.07f, 0.7f);
                break;
            case DOWN:
                FrameFluidRenderer.renderFluidCuboid(lavaStack, matrixStack, buffer, combinedLight, 0.3f, 0.93f, 0.3f, 0.7f, 0.93f, 0.7f);
                break;
            case NORTH:
                FrameFluidRenderer.renderFluidCuboid(lavaStack, matrixStack, buffer, combinedLight, 0.3f, 0.3f, 0.93f, 0.7f, 0.7f, 0.93f);
                break;
            case EAST:
                FrameFluidRenderer.renderFluidCuboid(lavaStack, matrixStack, buffer, combinedLight, 0.07f, 0.3f, 0.3f, 0.07f, 0.7f, 0.7f);
                break;
            case WEST:
                FrameFluidRenderer.renderFluidCuboid(lavaStack, matrixStack, buffer, combinedLight, 0.93f, 0.3f, 0.3f, 0.93f, 0.7f, 0.7f);
                break;
            case SOUTH:
                FrameFluidRenderer.renderFluidCuboid(lavaStack, matrixStack, buffer, combinedLight, 0.3f, 0.3f, 0.07f, 0.7f, 0.7f, 0.07f);
                break;
        }
    }

    public ModuleNullify() {
        super();
        lavaStack = new FluidStack(Fluids.LAVA, 1000);
        still = RandomUtils.getSprite(Fluids.LAVA.getAttributes().getStillTexture());//Minecraft.getInstance().getModelManager().getAtlasTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE).getSprite(lava.getAttributes().getStillTexture());
        flowing = RandomUtils.getSprite(Fluids.LAVA.getAttributes().getFlowingTexture());// ModelBakery.LOCATION_LAVA_FLOW.getSprite();
    }

    @Override
    public String getModuleName() {
        return I18n.format("modularitemframe.module.null");
    }

    @Override
    public ResourceLocation getId() {
        return LOC;
    }

    @Nonnull
    @Override
    @OnlyIn(Dist.CLIENT)
    public ResourceLocation frontTexture() {
        return BG_LOC;
    }

    @Override
    public ActionResultType onBlockActivated(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nonnull PlayerEntity playerIn, @Nonnull Hand hand, @Nonnull Direction facing, BlockRayTraceResult hit) {
        if (!worldIn.isRemote) {
            ItemStack held = playerIn.getHeldItem(hand);
            if (!playerIn.isSneaking() && !held.isEmpty()) {
                if (ItemHelper.simpleAreStacksEqual(held, lastStack)) {
                    if (held.getCount() + lastStack.getCount() > lastStack.getMaxStackSize())
                        lastStack.setCount(lastStack.getMaxStackSize());
                    else lastStack.grow(held.getCount());
                } else {
                    lastStack = held.copy();
                }
                held.setCount(0);
                NetworkHandler.sendAround(new PlaySoundPacket(pos, SoundEvents.BLOCK_LAVA_EXTINGUISH.getName(), SoundCategory.BLOCKS.getName(), 0.4F, 0.7F), worldIn, tile.getPos(), 32);
            } else if (playerIn.isSneaking() && held.isEmpty() && !lastStack.isEmpty()) {
                playerIn.setHeldItem(hand, lastStack);
                lastStack = ItemStack.EMPTY;
                NetworkHandler.sendAround(new PlaySoundPacket(pos, SoundEvents.ENTITY_ENDER_PEARL_THROW.getName(), SoundCategory.BLOCKS.getName(), 0.4F, 0.7F), worldIn, tile.getPos(), 32);
            }
        }
        return ActionResultType.SUCCESS;
    }

    @Nonnull
    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT compound = super.serializeNBT();
        compound.put(NBT_LASTSTACK, lastStack.serializeNBT());
        return compound;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        super.deserializeNBT(nbt);
        if (nbt.contains(NBT_LASTSTACK)) lastStack = ItemStack.read(nbt.getCompound(NBT_LASTSTACK));
    }
}
