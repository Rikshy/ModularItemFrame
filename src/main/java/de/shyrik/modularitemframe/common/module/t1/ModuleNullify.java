package de.shyrik.modularitemframe.common.module.t1;

import de.shyrik.modularitemframe.ModularItemFrame;
import de.shyrik.modularitemframe.api.ModuleBase;
import de.shyrik.modularitemframe.api.utils.ItemUtils;
import de.shyrik.modularitemframe.api.utils.RenderUtils;
import de.shyrik.modularitemframe.client.render.FrameRenderer;
import de.shyrik.modularitemframe.common.network.NetworkHandler;
import de.shyrik.modularitemframe.common.network.packet.PlaySoundPacket;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;

import javax.annotation.Nonnull;

public class ModuleNullify extends ModuleBase {

    public static final ResourceLocation LOC = new ResourceLocation(ModularItemFrame.MOD_ID, "module_t1_nullify");
    public static final ResourceLocation BG_LOC = new ResourceLocation(ModularItemFrame.MOD_ID, "block/module_t1_null");
    private static final String NBT_LASTSTACK = "laststack";

    private ItemStack lastStack = ItemStack.EMPTY;

    private final FluidStack lavaStack;
    private final TextureAtlasSprite still;
    private final TextureAtlasSprite flowing;

    @Override
    @OnlyIn(Dist.CLIENT)
    public void specialRendering(FrameRenderer renderer, double x, double y, double z, float partialTicks, int destroyStage) {
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();

        int color = lavaStack.getFluid().getColor();
        RenderUtils.translateAgainstPlayer(tile.getPos(), false);

        switch (tile.blockFacing()) {
            case UP:
                RenderUtils.renderFluid(lavaStack, tile.getPos(), 0.3d, 0.92d, 0.3d, 0.0d, 0.0d, 0.0d, 0.4d, 0.05d, 0.4d, color, still, flowing);
                break;
            case DOWN:
                RenderUtils.renderFluid(lavaStack, tile.getPos(), 0.3d, 0.03d, 0.3d, 0.0d, 0.0d, 0.0d, 0.4d, 0.05d, 0.4d, color, still, flowing);
                break;
            case NORTH:
                RenderUtils.renderFluid(lavaStack, tile.getPos(), 0.3d, 0.3d, 0.03d, 0.0d, 0.0d, 0.0d, 0.4d, 0.4d, 0.05d, color, still, flowing);
                break;
            case EAST:
                RenderUtils.renderFluid(lavaStack, tile.getPos(), 0.92d, 0.3d, 0.3d, 0.0d, 0.0d, 0.0d, 0.05d, 0.4d, 0.4d, color, still, flowing);
                break;
            case WEST:
                RenderUtils.renderFluid(lavaStack, tile.getPos(), 0.03d, 0.3d, 0.3d, 0.0d, 0.0d, 0.0d, 0.05d, 0.4d, 0.4d, color, still, flowing);
                break;
            case SOUTH:
                RenderUtils.renderFluid(lavaStack, tile.getPos(), 0.3d, 0.3d, 0.92d, 0.0d, 0.0d, 0.0d, 0.4d, 0.4d, 0.05d, color, still, flowing);
                break;
        }
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    public ModuleNullify() {
        super();
        lavaStack = FluidUtil.getFluidContained(new ItemStack(Items.LAVA_BUCKET)).orElse(null);
        assert lavaStack != null;
        Fluid lava = lavaStack.getFluid();
        still = Minecraft.getInstance().getTextureMap().getSprite(lava.getStill());
        flowing = Minecraft.getInstance().getTextureMap().getSprite(lava.getFlowing());
    }

    @Override
    public String getModuleName() {
        return I18n.format("modularitemframe.module.nullify");
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
    public boolean onBlockActivated(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull EntityPlayer playerIn, @Nonnull EnumHand hand, @Nonnull EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!worldIn.isRemote) {
            ItemStack held = playerIn.getHeldItem(hand);
            if (!playerIn.isSneaking() && !held.isEmpty()) {
                if (ItemUtils.simpleAreStacksEqual(held, lastStack)) {
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
        return true;
    }

    @Nonnull
    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound compound = super.serializeNBT();
        compound.put(NBT_LASTSTACK, lastStack.serializeNBT());
        return compound;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        super.deserializeNBT(nbt);
        if (nbt.hasUniqueId(NBT_LASTSTACK)) lastStack = ItemStack.read(nbt.getCompound(NBT_LASTSTACK));
    }
}
